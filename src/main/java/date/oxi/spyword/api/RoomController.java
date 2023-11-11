package date.oxi.spyword.api;

import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@Tag(name = "Rooms")
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @NonNull
    private final RoomService roomService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RoomDto> createRoom(
            @RequestBody
            @NonNull String playerUuidStr
    ) {
        RoomDto roomDto = roomService.createRoom(playerUuidStr);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomDto);
    }

    @GetMapping("/{code}")
    public ResponseEntity<RoomDto> getRoomInfo(
            @PathVariable
            @NonNull String code
    ) {
        RoomDto foundRoom = roomService.getRoomInfo(code);
        return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
    }

    @PostMapping("/{code}/join")
    public ResponseEntity<RoomDto> joinRoom(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull String playerUuidStr
    ) {
        RoomDto updatedRoom = roomService.joinRoom(code, playerUuidStr);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRoom);
    }

    @PostMapping("/{code}/start")
    public ResponseEntity<RoomDto> startRound(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull String playerUuidStr,
            @RequestParam
            Optional<Integer> minRounds,
            @RequestParam
            Optional<Integer> maxRounds,
            @RequestParam
            Optional<String> goodWord,
            @RequestParam
            Optional<String> badWord
    ) {
        RoomDto updatedRoom = roomService.startRound(code, playerUuidStr, minRounds, maxRounds, goodWord, badWord);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRoom);
    }

    @PostMapping("/{code}/take-turn")
    public ResponseEntity<RoomDto> passOnTurn(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull String playerUuidStr
    ) {
        RoomDto updatedRoom = roomService.takeTurn(code, playerUuidStr);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRoom);
    }


    @PostMapping("/{code}/vote-to-end")
    public ResponseEntity<RoomDto> voteToEnd(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull String playerUuidStr,
            @RequestParam
            @NonNull Boolean voteForEnd
    ) {
        RoomDto updatedRoom = roomService.voteToEnd(code, playerUuidStr, voteForEnd);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRoom);

    }

    @PostMapping("/{code}/vote-for-spy")
    public ResponseEntity<RoomDto> voteForSpy(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull String playerUuidStr,
            @RequestParam
            @NonNull UUID voteForSpyId
    ) {
        RoomDto updatedRoom = roomService.voteForSpy(code, playerUuidStr, voteForSpyId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRoom);
    }

    @PostMapping("/{code}/spy-guess-word")
    public ResponseEntity<RoomDto> spyGuessWord(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull String playerUuidStr,
            @RequestParam
            @NonNull String guessedWord
    ) {
        RoomDto updatedRoom = roomService.spyGuessWord(code, playerUuidStr, guessedWord);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRoom);
    }
}