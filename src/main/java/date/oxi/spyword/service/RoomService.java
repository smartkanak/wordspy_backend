package date.oxi.spyword.service;

import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.entity.PlayerEntity;
import date.oxi.spyword.entity.RoomEntity;
import date.oxi.spyword.entity.RoundEntity;
import date.oxi.spyword.exception.HttpForbiddenException;
import date.oxi.spyword.exception.HttpNotFoundException;
import date.oxi.spyword.mapper.RoomMapper;
import date.oxi.spyword.model.RoundState;
import date.oxi.spyword.repository.RoomRepository;
import date.oxi.spyword.utils.RoomCodeGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    @NonNull
    private final RoundService roundService;

    @NonNull
    private final PlayerService playerService;

    @NonNull
    private final RoomRepository repo;

    public RoomDto getRoomInfo(@NonNull String code) {
        RoomEntity foundRoom = getRoomOrThrowException(code);
        return RoomMapper.INSTANCE.toDto(foundRoom);
    }

    public RoomDto createRoom(@NonNull final String hostIdStr) {
        UUID hostId = UUID.fromString(hostIdStr);
        PlayerEntity host = getPlayerOrThrowException(hostId);

        // Create
        RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();
        String roomCode = roomCodeGenerator.generateUniqueCode();

        // Create Room Entity
        RoundEntity round = RoundEntity.builder().build();
        RoomEntity room = RoomEntity.builder()
                .code(roomCode)
                .host(host)
                .round(round)
                .build();
        room.getPlayers().add(host);

        // Update Database
        RoomEntity savedRoom = repo.save(room);

        return RoomMapper.INSTANCE.toDto(savedRoom);
    }

    public RoomDto joinRoom(@NonNull final String code, @NonNull final String playerUUIDStr) {
        UUID playerUUID = UUID.fromString(playerUUIDStr);
        final RoomEntity foundRoom = getRoomOrThrowException(code);

        Set<RoundState> allowedStates = Set.of(RoundState.WAITING_FOR_PLAYERS, RoundState.SPY_WON, RoundState.GOOD_TEAM_WON);
        RoundState currentState = foundRoom.getRound().getState();
        if (!allowedStates.contains(currentState)) {
            throw new HttpForbiddenException("Cannot join room in this state");
        }

        final RoomEntity roomWithAddedPlayer = addPlayerToRoom(foundRoom, playerUUID);

        return RoomMapper.INSTANCE.toDto(roomWithAddedPlayer);
    }

    public RoomEntity addPlayerToRoom(@NonNull final RoomEntity room, @NonNull final UUID player) {
        PlayerEntity playerEntity = playerService.findPlayerByUUID(player).orElseThrow(
                () -> new HttpNotFoundException("Player not found")
        );

        room.getPlayers().add(playerEntity);
        return repo.save(room);
    }

    public RoomDto startRound(
            @NonNull final String code,
            @NonNull final String playerUuidStr,
            @NonNull final Optional<Integer> minRounds,
            @NonNull final Optional<Integer> maxRounds,
            @NonNull final Optional<String> goodWord,
            @NonNull final Optional<String> badWord
    ) {
        UUID playerUUID = UUID.fromString(playerUuidStr);
        final RoomEntity foundRoom = getRoomOrThrowException(code);

        if (!foundRoom.getHost().getId().equals(playerUUID)) {
            throw new HttpForbiddenException("Only host can start round");
        }

        if (foundRoom.getPlayers().size() < 3) {
            throw new HttpForbiddenException("Not enough players");
        }

        Set<RoundState> allowedStates = Set.of(RoundState.WAITING_FOR_PLAYERS, RoundState.SPY_WON, RoundState.GOOD_TEAM_WON);
        RoundState currentState = foundRoom.getRound().getState();
        if (!allowedStates.contains(currentState)) {
            throw new HttpForbiddenException("Cannot start round in this current state: " + currentState);
        }

        HashSet<UUID> currentPlayerIds = getPlayerIds(foundRoom);

        foundRoom.setRound(
                roundService.start(
                        foundRoom.getRound().getId(),
                        currentPlayerIds,
                        minRounds,
                        maxRounds,
                        goodWord,
                        badWord
                )
        );

        RoomEntity updatedRoom = repo.save(foundRoom);
        return RoomMapper.INSTANCE.toDto(updatedRoom);
    }

    @NonNull
    private static HashSet<UUID> getPlayerIds(RoomEntity foundRoom) {
        return foundRoom.getPlayers().stream()
                .map(PlayerEntity::getId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public RoomDto takeTurn(
            @NonNull final String code,
            @NonNull final String playerIdTakingTurnStr
    ) {
        UUID playerIdTakingTurn = UUID.fromString(playerIdTakingTurnStr);
        final RoomEntity foundRoom = getRoomOrThrowException(code);

        RoundState currentState = foundRoom.getRound().getState();
        if (!currentState.equals(RoundState.PLAYERS_EXCHANGE_WORDS)) {
            throw new HttpForbiddenException("Cannot take turn in this current state: " + currentState);
        }

        UUID actualPlayersTurn = foundRoom.getRound().getPlayersTurnId();
        if (!actualPlayersTurn.equals(playerIdTakingTurn)) {
            throw new HttpForbiddenException("Cannot take turn, it is not your turn, but of player with ID: " + actualPlayersTurn);
        }

        HashSet<UUID> currentPlayerIds = getPlayerIds(foundRoom);

        foundRoom.setRound(
                roundService.takeTurn(
                        playerIdTakingTurn,
                        foundRoom.getRound(),
                        currentPlayerIds
                )
        );

        RoomEntity updatedRoom = repo.save(foundRoom);
        return RoomMapper.INSTANCE.toDto(updatedRoom);
    }

    public RoomDto voteToEnd(
            @NonNull final String code,
            @NonNull final String playerIdVotingStr,
            @NonNull final Boolean voteForEnd
    ) {
        UUID playerIdVoting = UUID.fromString(playerIdVotingStr);
        RoomEntity foundRoom = getRoomOrThrowException(code);

        RoundState currentState = foundRoom.getRound().getState();
        if (!currentState.equals(RoundState.VOTING_TO_END_GAME)) {
            throw new HttpForbiddenException("Cannot vote to end in this current state: " + currentState);
        }

        HashSet<UUID> currentPlayerIds = getPlayerIds(foundRoom);
        if (!currentPlayerIds.contains(playerIdVoting)) {
            throw new HttpForbiddenException("Cannot vote to end, you are not in the room");
        }

        foundRoom.setRound(
                roundService.voteToEnd(
                        playerIdVoting,
                        foundRoom.getRound(),
                        currentPlayerIds,
                        voteForEnd
                )
        );

        RoomEntity updatedRoom = repo.save(foundRoom);
        return RoomMapper.INSTANCE.toDto(updatedRoom);
    }

    public RoomDto voteForSpy(
            @NonNull final String code,
            @NonNull final String playerIdVotingStr,
            @NonNull final UUID voteForSpyId
    ) {
        UUID playerIdVoting = UUID.fromString(playerIdVotingStr);
        RoomEntity foundRoom = getRoomOrThrowException(code);

        RoundState currentState = foundRoom.getRound().getState();
        if (!currentState.equals(RoundState.VOTING_FOR_SPY) && !currentState.equals(RoundState.VOTING_FOR_SPY_RETRY)) {
            throw new HttpForbiddenException("Cannot vote for spy in this current state: " + currentState);
        }

        HashSet<UUID> currentPlayerIds = getPlayerIds(foundRoom);
        if (!currentPlayerIds.contains(playerIdVoting)) {
            throw new HttpForbiddenException("Cannot vote for spy, you are not in the room");
        }

        foundRoom.setRound(
                roundService.voteForSpy(
                        playerIdVoting,
                        foundRoom.getRound(),
                        currentPlayerIds,
                        voteForSpyId
                )
        );

        RoomEntity updatedRoom = repo.save(foundRoom);
        return RoomMapper.INSTANCE.toDto(updatedRoom);
    }

    public RoomDto spyGuessWord(
            @NonNull final String code,
            @NonNull final String playerIdGuessingStr,
            @NonNull final String guessedWord
    ) {
        UUID playerIdGuessing = UUID.fromString(playerIdGuessingStr);
        RoomEntity foundRoom = getRoomOrThrowException(code);

        RoundState currentState = foundRoom.getRound().getState();
        if (currentState.equals(RoundState.SPY_GUESS_WORD)) {
            throw new HttpForbiddenException("Cannot guess word in this current state: " + currentState);
        }

        if (!foundRoom.getRound().getSpyId().equals(playerIdGuessing)) {
            throw new HttpForbiddenException("Cannot guess word, you are not the spy");
        }

        foundRoom.setRound(
                roundService.spyGuessWord(
                        foundRoom.getRound(),
                        guessedWord
                )
        );

        RoomEntity updatedRoom = repo.save(foundRoom);
        return RoomMapper.INSTANCE.toDto(updatedRoom);
    }

    private RoomEntity getRoomOrThrowException(@NonNull String code) {
        return repo.findByCode(code).orElseThrow(
                () -> new HttpNotFoundException("Room not found")
        );
    }

    private PlayerEntity getPlayerOrThrowException(@NonNull UUID playerId) {
        return playerService.findPlayerByUUID(playerId).orElseThrow(
                () -> new HttpNotFoundException("Player not found")
        );
    }
}
