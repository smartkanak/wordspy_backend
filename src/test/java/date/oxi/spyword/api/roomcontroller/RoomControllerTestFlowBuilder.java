package date.oxi.spyword.api.roomcontroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import date.oxi.spyword.dto.PlayerDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RequiredArgsConstructor
public class RoomControllerTestFlowBuilder {
    private final MockMvc mockMvc;

    @Getter
    private ResultActions result = null;

    /**
     * Builder for create a room
     *
     * @param host The host of the room
     * @return this for chaining
     * @throws Exception if the request fails
     */
    public RoomControllerTestFlowBuilder createRoom(PlayerDto host) throws Exception {
        result = mockMvc.perform(post("/api/v1/rooms/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonNodeFromPlayer(host).toString())
                .accept(MediaType.APPLICATION_JSON));

        return this;
    }

    /**
     * Builder for getting a room
     *
     * @param roomCode The room code of the room to get
     * @return this for chaining
     * @throws Exception if the request fails
     */
    public RoomControllerTestFlowBuilder getRoomInfo(PlayerDto playerInRoom, String roomCode) throws Exception {
        result = mockMvc.perform(get("/api/v1/rooms/" + roomCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonNodeFromPlayer(playerInRoom).toString())
                .accept(MediaType.APPLICATION_JSON));

        return this;
    }

    /**
     * Builder for joining a room
     *
     * @param playerToJoin The player to join the room
     * @param roomCode     The room code of the room to join
     * @return this for chaining
     * @throws Exception if the request fails
     */
    public RoomControllerTestFlowBuilder joinRoom(PlayerDto playerToJoin, String roomCode) throws Exception {
        result = mockMvc.perform(post("/api/v1/rooms/" + roomCode + "/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonNodeFromPlayer(playerToJoin).toString())
                .accept(MediaType.APPLICATION_JSON));

        return this;
    }


    /**
     * Builder for starting a round
     *
     * @param playerToJoin The player who starts the round
     * @param roomCode     The room code of the room to start the round
     * @return this for chaining
     * @throws Exception if the request fails
     */
    public RoomControllerTestFlowBuilder startRound(PlayerDto playerToJoin, String roomCode) throws Exception {
        result = mockMvc.perform(post("/api/v1/rooms/" + roomCode + "/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonNodeFromPlayer(playerToJoin).toString())
                .accept(MediaType.APPLICATION_JSON));

        return this;
    }


    /**
     * Builder for taking a turn
     *
     * @param playerWhosTurnIs The player who takes the turn
     * @param roomCode     The room code of the room to take the turn
     * @return this for chaining
     * @throws Exception if the request fails
     */
    public RoomControllerTestFlowBuilder takeTurn(PlayerDto playerWhosTurnIs, String roomCode) throws Exception {
        result = mockMvc.perform(post("/api/v1/rooms/" + roomCode + "/take-turn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonNodeFromPlayer(playerWhosTurnIs).toString())
                .accept(MediaType.APPLICATION_JSON));

        return this;
    }

    private JsonNode jsonNodeFromPlayer(PlayerDto player) {
        return JsonNodeFactory.instance.objectNode()
                .put("id", player.getId().toString())
                .put("name", player.getName())
                .put("languageCode", player.getLanguageCode());
    }
}
