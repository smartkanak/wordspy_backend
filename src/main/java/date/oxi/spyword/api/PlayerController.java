package date.oxi.spyword.api;

import date.oxi.spyword.dto.PlayerDto;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final List<PlayerDto> players = new ArrayList<>();

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PlayerDto> createPlayer(
            @NonNull String name
    ) {
        UUID playerId = UUID.randomUUID();

        PlayerDto playerDto = PlayerDto.builder()
                .id(playerId)
                .name(name.trim())
                .build();

        players.add(playerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(playerDto);
    }
}
