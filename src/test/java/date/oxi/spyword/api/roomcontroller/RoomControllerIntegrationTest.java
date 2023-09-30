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
        // create room
        createRoomTest(player_1).andExpect(status().isCreated());
        System.out.println("\n### createRoomTest - player_1 created room");
        System.out.println(strFromRoom(room_1));
        assertNotNull(room_1.getId()); // UUID got created
        assertEquals(player_1.getId(), room_1.getHost().getId()); // player_1 is the host
        assertEquals(RoundState.WAITING_FOR_PLAYERS, room_1.getRound().getState()); // initial round state is waiting

        // get room info
        getRoomInfoTest(player_1, room_1.getCode()).andExpect(status().isOk());
        System.out.println("\n### getRoomInfoTest - room info was queried");
        System.out.println(strFromRoom(room_1));
        assertNotNull(room_1); // room was found

        // join room
        joinRoomTest(player_2, room_1.getCode()).andExpect(status().isOk());
        joinRoomTest(player_3, room_1.getCode()).andExpect(status().isOk());
        System.out.println("\n### joinRoomTest - player_2 and player_3 joined room");
        System.out.println(strFromRoom(room_1));
        assertEquals(3, room_1.getPlayers().size()); // 3 players in room
        assertEquals(player_1.getId(), room_1.getHost().getId()); // player_1 is still host
        assertTrue(room_1.getPlayers().contains(player_2)); // player_2 is in room
        assertTrue(room_1.getPlayers().contains(player_3)); // player_3 is in room

        // start round
        startRoundTest(player_1, room_1.getCode()).andExpect(status().isOk());
        System.out.println("\n### startRoundTest - player_1 started round");
        System.out.println(strFromRoom(room_1));
        assertNotNull(room_1.getRound().getGoodWord()); // good word was generated
        assertNotNull(room_1.getRound().getBadWord()); // bad word was generated
        assertNotNull(room_1.getRound().getSpyId()); // spy id was generated
        assertTrue(room_1.getPlayers().contains(player_2));
        assertEquals(RoundState.PLAYERS_EXCHANGE_WORDS, room_1.getRound().getState()); // state is exchanging words
        assertTrue(
                // assert that the spy is in the room
                room_1.getPlayers().stream()
                        .anyMatch(p ->
                                p.getId().toString()
                                        .equals(room_1.getRound().getSpyId().toString()))
        );

        // test taking a turn
        PlayerDto playerTakingTurn = getPlayerWhosturnIs();
        takeTurnTest(playerTakingTurn).andExpect(status().isOk());
        System.out.println("\n### takeTurnTest - first player took his turn");
        System.out.println(strFromRoom(room_1));
        assertEquals(1, room_1.getRound().getNumber()); // Still round 1
        assertEquals(RoundState.PLAYERS_EXCHANGE_WORDS, room_1.getRound().getState()); // still exchanging words
        assertNotEquals(playerTakingTurn.getId(), room_1.getRound().getPlayersTurnId()); // turn was passed on
    }

    /**
     * Test creating a room
     *
     * @param player The player who creates the room
     * @return The result of the request
     * @throws Exception if the request fails
     */
    public ResultActions createRoomTest(PlayerDto player) throws Exception {
        ResultActions result = flowBuilder
                .createRoom(player)
                .getResult();

        room_1 = roomFromResult(result);
        return result;
    }

    /**
     * Test getting a room
     *
     * @param player   The player who gets the room
     * @param roomCode The room code of the room to get
     * @return The result of the request
     * @throws Exception if the request fails
     */
    public ResultActions getRoomInfoTest(PlayerDto player, String roomCode) throws Exception {
        ResultActions result = flowBuilder
                .getRoomInfo(player, roomCode)
                .getResult();

        room_1 = roomFromResult(result);
        return result;
    }

    /**
     * Test joining a room
     *
     * @param player   The player who joins the room
     * @param roomCode The room code of the room to join
     * @return The result of the request
     * @throws Exception if the request fails
     */
    public ResultActions joinRoomTest(PlayerDto player, String roomCode) throws Exception {
        ResultActions result = flowBuilder
                .joinRoom(player, roomCode)
                .getResult();

        room_1 = roomFromResult(result);
        return result;
    }

    /**
     * Test starting a round
     *
     * @param player   The player who starts the round
     * @param roomCode The room code of the room to start the round
     * @return The result of the request
     * @throws Exception if the request fails
     */
    public ResultActions startRoundTest(PlayerDto player, String roomCode) throws Exception {
        ResultActions result = flowBuilder
                .startRound(player, roomCode)
                .getResult();

        room_1 = roomFromResult(result);
        return result;
    }

    /**
     * Test taking a turn
     *
     * @param playerTakingTurn The player who takes the turn
     * @return The result of the request
     * @throws Exception if the request fails
     */
    public ResultActions takeTurnTest(PlayerDto playerTakingTurn) throws Exception {

        ResultActions result = flowBuilder
                .takeTurn(playerTakingTurn, room_1.getCode())
                .getResult();

        room_1 = roomFromResult(result);
        return result;
    }

    private static PlayerDto getPlayerWhosturnIs() {
        PlayerDto playerWhosTurnIs = room_1.getPlayers().stream()
                .filter(player -> player.getId().equals(room_1.getRound().getPlayersTurnId()))
                .findFirst()
                .orElse(null);
        assert playerWhosTurnIs != null;
        return playerWhosTurnIs;
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
