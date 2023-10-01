package date.oxi.spyword.api;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.model.RoundState;
import date.oxi.spyword.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
            @NonNull PlayerDto host
    ) {
        RoomDto roomDto = roomService.createRoom(host);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomDto);
    }

    @GetMapping("/{code}")
    public ResponseEntity<RoomDto> getRoomInfo(
            @PathVariable
            @NonNull String code
    ) {
        RoomDto foundRoom = roomService.getRoomByCode(code);

        if (foundRoom == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }

    @PostMapping("/{code}/join")
    public ResponseEntity<RoomDto> joinRoom(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull PlayerDto player
    ) {
        RoomDto foundRoom = roomService.getRoomByCode(code);

        if (foundRoom == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!foundRoom.getRound().getState().equals(RoundState.WAITING_FOR_PLAYERS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else if (!foundRoom.getPlayers().contains(player)) {
            roomService.addPlayerToRoom(foundRoom, player);
        }
        return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
    }

    @PostMapping("/{code}/start")
    public ResponseEntity<RoomDto> startRound(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull PlayerDto player,
            @RequestParam
            Optional<Integer> minRounds,
            @RequestParam
            Optional<Integer> maxRounds,
            @RequestParam
            Optional<String> goodWord,
            @RequestParam
            Optional<String> badWord
    ) {
        RoomDto foundRoom = roomService.getRoomByCode(code);

        if (foundRoom == null) {
            // no room found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!foundRoom.getHost().equals(player)) {
            // starting player is not the host
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else if (foundRoom.getPlayers().size() < 3) {
            // not enough players
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else if (!foundRoom.getRound().getState().equals(RoundState.WAITING_FOR_PLAYERS)) {
            // round already started
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else {
            // start round
            HashSet<UUID> currentPlayerIds = foundRoom.getPlayers().stream()
                    .map(PlayerDto::getId)
                    .collect(Collectors.toCollection(HashSet::new));

            roomService.startRound(foundRoom.getRound(), currentPlayerIds, minRounds, maxRounds, goodWord, badWord);

            // return updated room
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }

    @PostMapping("/{code}/take-turn")
    public ResponseEntity<RoomDto> passOnTurn(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull PlayerDto player
    ) {
        RoomDto foundRoom = roomService.getRoomByCode(code);

        if (foundRoom == null) {
            // no room found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!foundRoom.getRound().getState().equals(RoundState.PLAYERS_EXCHANGE_WORDS)) {
            // round is not in the correct state
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else if (!foundRoom.getRound().getPlayersTurnId().equals(player.getId())) {
            // player is not the one whose turn it is
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else {
            // else take turn and pass on
            HashSet<UUID> currentPlayerIds = foundRoom.getPlayers().stream()
                    .map(PlayerDto::getId)
                    .collect(Collectors.toCollection(HashSet::new));

            roomService.takeTurn(player.getId(), foundRoom.getRound(), currentPlayerIds);

            // return updated room
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }

    @PostMapping("/{code}/vote-to-end")
    public ResponseEntity<RoomDto> voteToEnd(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull PlayerDto player,
            @RequestParam
            @NonNull Boolean voteForEnd
    ) {
        RoomDto foundRoom = roomService.getRoomByCode(code);

        if (foundRoom == null) {
            // no room found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!foundRoom.getRound().getState().equals(RoundState.VOTING_TO_END_GAME)) {
            // round is not in the correct state
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else if (!foundRoom.getPlayers().contains(player)) {
            // player is not in the room
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else {
            // else vote to end
            HashSet<UUID> currentPlayerIds = foundRoom.getPlayers().stream()
                    .map(PlayerDto::getId)
                    .collect(Collectors.toCollection(HashSet::new));

            roomService.voteToEnd(player.getId(), foundRoom.getRound(), currentPlayerIds, voteForEnd);

            // return updated room
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }

    @PostMapping("/{code}/vote-for-spy")
    public ResponseEntity<RoomDto> voteForSpy(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull PlayerDto player,
            @RequestParam
            @NonNull UUID voteForSpyId
    ) {
        RoomDto foundRoom = roomService.getRoomByCode(code);

        if (foundRoom == null) {
            // no room found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        RoundState state = foundRoom.getRound().getState();
        if (!state.equals(RoundState.VOTING_FOR_SPY) && !state.equals(RoundState.NO_MAJORITY_SPY_VOTES)) {
            // round is not in the correct state
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else if (!foundRoom.getPlayers().contains(player)) {
            // player is not in the room
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else {
            // else vote for spy
            HashSet<UUID> currentPlayerIds = foundRoom.getPlayers().stream()
                    .map(PlayerDto::getId)
                    .collect(Collectors.toCollection(HashSet::new));

            roomService.voteForSpy(player.getId(), foundRoom.getRound(), currentPlayerIds, voteForSpyId);

            // return updated room
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }

    @PostMapping("/{code}/spy-guess-word")
    public ResponseEntity<RoomDto> spyGuessWord(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull PlayerDto player,
            @RequestParam
            @NonNull String guessedWord
    ) {
        RoomDto foundRoom = roomService.getRoomByCode(code);

        if (foundRoom == null) {
            // no room found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!foundRoom.getRound().getState().equals(RoundState.SPY_GUESS_WORD)) {
            // round is not in the correct state
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else if (!foundRoom.getRound().getSpyId().equals(player.getId())) {
            // player guessing is not the spy
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(foundRoom);
        } else {
            // let the spy guess the word
            roomService.spyGuessWord(foundRoom.getRound(), guessedWord);

            // return updated room
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }
}
