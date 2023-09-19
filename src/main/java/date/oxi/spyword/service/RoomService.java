package date.oxi.spyword.service;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.dto.RoundDto;
import date.oxi.spyword.utils.RoomCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RoomService {

    private final List<RoomDto> rooms = new ArrayList<>();

    public RoomDto getRoomByCode(String code) {
        return rooms.stream()
                .filter(room -> room.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public RoomDto createRoom(PlayerDto host) {
        // Create
        RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();
        String roomCode = roomCodeGenerator.generateUniqueCode();
        RoomDto roomDto = new RoomDto(host, roomCode);

        // Update Database
        rooms.add(roomDto);

        return roomDto;
    }

    public void addPlayerToRoom(RoomDto room, PlayerDto player) {
        room.getPlayers().add(player);
    }

    public void startRound(Integer playersCount, RoundDto round) {
        Random random = new Random();
        int spyIndex = random.nextInt(playersCount);
        int turnIndex = random.nextInt(playersCount);
        round.start(spyIndex, turnIndex);
    }
}
