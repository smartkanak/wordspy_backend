package date.oxi.spyword.api;

import date.oxi.spyword.dto.CreatePlayerRequest;
import date.oxi.spyword.dto.PlayerDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Players")
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final List<PlayerDto> players = new ArrayList<>();

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PlayerDto> createPlayer(
            @RequestBody
            @NonNull CreatePlayerRequest createPlayerRequest
    ) {

        PlayerDto playerDto = PlayerDto.register(
                createPlayerRequest.getName(),
                createPlayerRequest.getLanguageCode()
        );

        players.add(playerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(playerDto);
    }
}
