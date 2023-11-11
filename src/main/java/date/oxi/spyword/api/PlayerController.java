package date.oxi.spyword.api;

import date.oxi.spyword.dto.CreatePlayerRequestDto;
import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.service.PlayerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Players")
@RequiredArgsConstructor
@RequestMapping("/api/v1/players")
public class PlayerController {

    @NonNull
    private final PlayerService playerService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PlayerDto> createPlayer(
            @RequestBody
            @NonNull CreatePlayerRequestDto request
    ) {

        PlayerDto playerDto = playerService.createPlayer(
                request.getName(),
                request.getLanguageCode()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(playerDto);
    }
}