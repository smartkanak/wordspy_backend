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
            @NonNull PlayerDto player
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

            roomService.startRound(foundRoom.getRound(), currentPlayerIds);

            // return updated room
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }

    @PostMapping("/{code}/take-turn")
    public ResponseEntity<RoomDto> passOnTurn(
            @PathVariable
            @NonNull String code,
            @RequestBody // Map PlayerDto to their ID
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
            // pass on turn
            HashSet<UUID> currentPlayerIds = foundRoom.getPlayers().stream()
                    .map(PlayerDto::getId)
                    .collect(Collectors.toCollection(HashSet::new));

            roomService.takeTurn(player.getId(), foundRoom.getRound(), currentPlayerIds);

            // return updated room
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }
}
