package date.oxi.spyword.service;

import date.oxi.spyword.dto.RoundDto;
import date.oxi.spyword.model.RoundState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoundService {

    public void start(RoundDto round, HashSet<UUID> currentPlayersUUIDs) {
        // Get
        String goodWord = "Zauberstab"; // TODO: Get real data
        String badWord = "Flugbesen";
        UUID spyId = getRandomSetElement(currentPlayersUUIDs);
        UUID playersTurnId = getRandomSetElement(currentPlayersUUIDs);

        // Set
        round.setGoodWord(goodWord);
        round.setBadWord(badWord);
        round.setSpyId(spyId);
        round.setPlayersTurnId(playersTurnId);
        round.setState(RoundState.PLAYERS_EXCHANGE_WORDS);
    }

    public void takeTurn(UUID playerIdTakingTurn, RoundDto round, HashSet<UUID> currentPlayerIds) {
        round.getPlayersWhoTookTurn().add(playerIdTakingTurn);

        prepareNextTurn(round, currentPlayerIds);
    }

    public void voteToEnd(UUID playerIdVoting, RoundDto round, HashSet<UUID> currentPlayerIds, Boolean voteForEnd) {
        Map<UUID, Boolean> votes = round.getPlayersWhoVotedForEndingGame();

        votes.put(playerIdVoting, voteForEnd);

        if (votes.keySet().containsAll(currentPlayerIds)) {
            // if all current player ids are inside the votes map
            if (votes.values().stream().filter(v -> v).count() > currentPlayerIds.size() / 2) {
                // if more than half of the players voted to end the game start voting for spy
                round.setState(RoundState.VOTING_FOR_SPY);
            } else {
                // else continue game
                votes.clear();
                round.increaseMinRounds();
                nextRound(round, currentPlayerIds);
                round.setState(RoundState.PLAYERS_EXCHANGE_WORDS);
            }
        }
    }

    private void prepareNextTurn(RoundDto round, HashSet<UUID> currentPlayerIds) {
        // Get the players who have not yet had their turn
        HashSet<UUID> playersWithoutTurn = new HashSet<>(currentPlayerIds);
        playersWithoutTurn.removeAll(round.getPlayersWhoTookTurn());

        if (playersWithoutTurn.isEmpty()) {
            // If everyone had their turn, you could begin next round
            if (round.getNumber() >= round.getMaxRounds()) {
                // If the max number of rounds has been reached, begin voting
                round.setState(RoundState.VOTING_FOR_SPY);
            } else if (round.getNumber() >= round.getMinRounds()) {
                // If the min number of rounds has been reached, begin voting if players want to end game
                round.setState(RoundState.VOTING_TO_END_GAME);
            } else {
                // Else continue with next round
                nextRound(round, currentPlayerIds);
            }
        } else {
            // Else randomly choose one of the players with no turn yet
            UUID nextPlayersTurnId = getRandomSetElement(playersWithoutTurn);
            round.setPlayersTurnId(nextPlayersTurnId);
        }
    }

    private void nextRound(RoundDto round, HashSet<UUID> currentPlayerIds) {
        // Any players turn
        UUID playersTurnId = getRandomSetElement(currentPlayerIds);
        round.setPlayersTurnId(playersTurnId);
        // Clear Turn history
        round.getPlayersWhoTookTurn().clear();
        // Next round
        round.increaseRoundNumber();
    }

    private static <E> E getRandomSetElement(Set<E> set) {
        return set.stream().skip(new Random().nextInt(set.size())).findFirst().orElse(null);
    }
}
