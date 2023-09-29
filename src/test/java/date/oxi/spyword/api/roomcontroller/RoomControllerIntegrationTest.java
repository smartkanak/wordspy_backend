package date.oxi.spyword.api.roomcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import date.oxi.spyword.api.RoomController;
import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.model.RoundState;
import date.oxi.spyword.service.RoomService;
import date.oxi.spyword.service.RoundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@Import({RoomService.class, RoundService.class})
public class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Data for tests
    private static final PlayerDto player_1 = PlayerDto.register("Hamza", "de");
    private static final PlayerDto player_2 = PlayerDto.register("Sven", "de");
    private static final PlayerDto player_3 = PlayerDto.register("Tom", "de");
    private static RoomDto room_1 = null;

    // Flow builder for building test scenarios
    private RoomControllerTestFlowBuilder flowBuilder;

    @BeforeEach
    public void setUpFlowBuilder() {
        flowBuilder = new RoomControllerTestFlowBuilder(mockMvc);
    }

    @Test
    public void basicIntegrationTest() throws Exception {
        createRoomTest();
        getRoomInfoTest();
        joinRoomTest();
        startRoundTest();
        takeTurnTest();
    }

    /**
     * Test creating a room
     *
     * @throws Exception if the request fails
     */
    public void createRoomTest() throws Exception {
        // Act
        ResultActions result = flowBuilder
                .createRoom(player_1)
                .getResult()
                .andExpect(status().isCreated());

        // Print info
        RoomDto room = roomFromResult(result);
        System.out.println("\n### createRoomTest - player_1 created room");
        System.out.println(strFromRoom(room));

        // Assert
        assertNotNull(room.getId()); // UUID got created
        assertEquals(player_1.getId(), room.getHost().getId()); // player_1 is the host
        assertEquals(RoundState.WAITING_FOR_PLAYERS, room.getRound().getState()); // initial round state is waiting

        // Update
        room_1 = room;
    }

    /**
     * Test getting a room
     *
     * @throws Exception if the request fails
     */
    public void getRoomInfoTest() throws Exception {
        // Act
        ResultActions result = flowBuilder
                .getRoomInfo(player_1, room_1.getCode())
                .getResult()
                .andExpect(status().isOk());

        // Print info
        RoomDto room = roomFromResult(result);
        System.out.println("\n### getRoomInfoTest - room info was queried");
        System.out.println(strFromRoom(room));

        // Assert
        assertNotNull(room); // room was found
        assertEquals(room.getCode(), room_1.getCode()); // room code is correct

        // Update
        room_1 = room;
    }

    /**
     * Test joining a room
     *
     * @throws Exception if the request fails
     */
    public void joinRoomTest() throws Exception {
        // Act
        ResultActions result = flowBuilder
                .joinRoom(player_2, room_1.getCode())
                .joinRoom(player_3, room_1.getCode())
                .getResult()
                .andExpect(status().isOk());

        // Print info
        RoomDto room = roomFromResult(result);
        System.out.println("\n### joinRoomTest - player_2 and player_3 joined room");
        System.out.println(strFromRoom(room));

        // Assert
        assertEquals(3, room.getPlayers().size()); // 3 players in room
        assertEquals(player_1.getId(), room.getHost().getId()); // player_1 is still host
        assertTrue(room.getPlayers().contains(player_2)); // player_2 is in room
        assertTrue(room.getPlayers().contains(player_3)); // player_3 is in room

        // Update
        room_1 = room;
    }

    /**
     * Test starting a round
     *
     * @throws Exception if the request fails
     */
    public void startRoundTest() throws Exception {
        // Act
        ResultActions result = flowBuilder
                .startRound(player_1, room_1.getCode())
                .getResult()
                .andExpect(status().isOk());

        // Print info
        RoomDto room = roomFromResult(result);
        System.out.println("\n### startRoundTest - player_1 started round");
        System.out.println(strFromRoom(room));

        // Assert
        assertNotNull(room.getRound().getGoodWord()); // good word was generated
        assertNotNull(room.getRound().getBadWord()); // bad word was generated
        assertNotNull(room.getRound().getSpyId()); // spy id was generated
        assertTrue(room.getPlayers().contains(player_2));
        assertEquals(RoundState.PLAYERS_EXCHANGE_WORDS, room.getRound().getState()); // state is exchanging words
        assertTrue(
                // assert that the spy is in the room
                room.getPlayers().stream()
                        .anyMatch(player ->
                                player.getId().toString()
                                        .equals(room.getRound().getSpyId().toString()))
        );

        // Update
        room_1 = room;
    }

    /**
     * Test taking a turn
     *
     * @throws Exception if the request fails
     */
    public void takeTurnTest() throws Exception {
        PlayerDto playerWhosTurnIs = room_1.getPlayers().stream()
                .filter(player -> player.getId().equals(room_1.getRound().getPlayersTurnId()))
                .findFirst()
                .orElse(null);
        assert playerWhosTurnIs != null;

        // Act
        ResultActions result = flowBuilder
                .takeTurn(playerWhosTurnIs, room_1.getCode())
                .getResult()
                .andExpect(status().isOk());

        // Print info
        RoomDto room = roomFromResult(result);
        System.out.println("\n### takeTurnTest - first player took his turn");
        System.out.println(strFromRoom(room));

        // Assert
        assertEquals(1, room.getRound().getNumber()); // Still round 1
        assertEquals(RoundState.PLAYERS_EXCHANGE_WORDS, room.getRound().getState()); // still exchanging words
        assertNotEquals(playerWhosTurnIs.getId(), room.getRound().getPlayersTurnId()); // turn was passed on

        // Update
        room_1 = room;
    }

    private RoomDto roomFromResult(ResultActions result) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), RoomDto.class);
    }


    public String strFromRoom(RoomDto room) {
        return "RoomDto(" +
                "\n  id = " + room.getId() +
                "\n  code = '" + room.getCode() + '\'' +
                "\n  host = " + room.getHost() +
                "\n  players = " + room.getPlayers() +
                "\n  round = " + room.getRound() +
                "\n)";
    }
}
