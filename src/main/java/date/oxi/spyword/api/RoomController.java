package date.oxi.spyword.api;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.model.RoundState;
import date.oxi.spyword.utils.RoomCodeGenerator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Tag(name = "Rooms")
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final List<RoomDto> rooms = new ArrayList<>();
    RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RoomDto> createRoom(
            @RequestBody
            @NonNull PlayerDto host
    ) {
        String roomCode = roomCodeGenerator.generateUniqueCode();
        RoomDto roomDto = new RoomDto(host, roomCode);

        rooms.add(roomDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(roomDto);
    }

    @GetMapping("/{code}")
    public ResponseEntity<RoomDto> getRoomInfo(
            @PathVariable
            @NonNull String code
    ) {
        RoomDto foundRoom = getRoomByCode(code);

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
        RoomDto foundRoom = getRoomByCode(code);

        if (foundRoom == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!foundRoom.getRound().getRoundState().equals(RoundState.WAITING)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else if (foundRoom.getPlayers().contains(player)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            foundRoom.addPlayer(player);
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }

    private RoomDto getRoomByCode(String code) {
        return rooms.stream()
                .filter(room -> room.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @PostMapping("/{code}/start")
    public ResponseEntity<RoomDto> startRound(
            @PathVariable
            @NonNull String code,
            @RequestBody
            @NonNull PlayerDto player
    ) {
        RoomDto foundRoom = getRoomByCode(code);

        if (foundRoom == null) {
            // no room found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!foundRoom.getHost().equals(player)) {
            // starting player is not the host
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else if (foundRoom.getPlayers().size() < 3) {
            // not enough players
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else if (!foundRoom.getRound().getRoundState().equals(RoundState.WAITING)) {
            // round already started
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else {
            // start round
            foundRoom.startRound();
            return ResponseEntity.status(HttpStatus.OK).body(foundRoom);
        }
    }
}
