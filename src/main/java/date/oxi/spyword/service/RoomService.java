package date.oxi.spyword.service;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.dto.RoundDto;
import date.oxi.spyword.utils.RoomCodeGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    @NonNull
    private final RoundService roundService;

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

    public void startRound(RoundDto round, HashSet<UUID> currentPlayerIds) {
        roundService.start(round, currentPlayerIds);
    }

    public void takeTurn(UUID playerIdTakingTurn, RoundDto round, HashSet<UUID> currentPlayerIds) {
        roundService.takeTurn(playerIdTakingTurn, round, currentPlayerIds);
    }
}
