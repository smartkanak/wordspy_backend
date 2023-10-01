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
        RoomDto room = new RoomDto(host, roomCode, new RoundDto());

        // Update Database
        rooms.add(room);

        return room;
    }

    public void addPlayerToRoom(RoomDto room, PlayerDto player) {
        room.getPlayers().add(player);
    }

    public void startRound(
            RoundDto round,
            HashSet<UUID> currentPlayerIds,
            Optional<Integer> minRounds,
            Optional<Integer> maxRounds,
            Optional<String> goodWord,
            Optional<String> badWord
    ) {
        roundService.start(round, currentPlayerIds, minRounds, maxRounds, goodWord, badWord);
    }

    public void takeTurn(UUID playerIdTakingTurn, RoundDto round, HashSet<UUID> currentPlayerIds) {
        roundService.takeTurn(playerIdTakingTurn, round, currentPlayerIds);
    }

    public void voteToEnd(UUID playerIdVoting, RoundDto round, HashSet<UUID> currentPlayerIds, Boolean voteForEnd) {
        roundService.voteToEnd(playerIdVoting, round, currentPlayerIds, voteForEnd);
    }

    public void voteForSpy(UUID playerIdVoting, RoundDto round, HashSet<UUID> currentPlayerIds, UUID voteForSpyId) {
        roundService.voteForSpy(playerIdVoting, round, currentPlayerIds, voteForSpyId);
    }

    public void spyGuessWord(RoundDto round, String guessedWord) {
        roundService.spyGuessWord(round, guessedWord);
    }
}
