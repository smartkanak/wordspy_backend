package date.oxi.spyword.api;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.utils.RoomCodeGenerator;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final List<RoomDto> rooms = new ArrayList<>();
    RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RoomDto> createRoom(
            @NonNull PlayerDto host
    ) {
        String roomCode = roomCodeGenerator.generateUniqueCode();
        RoomDto roomDto = new RoomDto(host, roomCode);

        rooms.add(roomDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(roomDto);
    }

}
