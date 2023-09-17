package date.oxi.spyword.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import date.oxi.spyword.dto.PlayerDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateRoom() throws Exception {
        // Arrange
        PlayerDto host = PlayerDto.register("Hamza", "de");

        JsonNode requestBody = JsonNodeFactory.instance.objectNode()
                .put("id", host.getId().toString())
                .put("name", host.getName())
                .put("languageCode", host.getLanguageCode());

        // Act
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/rooms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody.toString())
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", Matchers.matchesRegex("^[A-Z]{6}$")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.host.id").value(host.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.round.state").value("WAITING"));
    }
}
