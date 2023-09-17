package date.oxi.spyword.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreatePlayer() throws Exception {
        // Arrange
        String playerName = "John";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/players/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + playerName + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.languageCode").value("en"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(playerName));
    }
}