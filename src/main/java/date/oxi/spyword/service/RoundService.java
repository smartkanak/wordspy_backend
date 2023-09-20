package date.oxi.spyword.service;

import date.oxi.spyword.dto.RoundDto;
import date.oxi.spyword.model.RoundState;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

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
        round.setRoundState(RoundState.PLAYERS_EXCHANGE_WORDS);
    }

    public void takeTurn(UUID playerIdTakingTurn, RoundDto round, HashSet<UUID> currentPlayerIds) {
        round.getPlayersWhoTookTurn().add(playerIdTakingTurn);

        prepareNextTurn(round, currentPlayerIds);
    }

    private void prepareNextTurn(RoundDto round, HashSet<UUID> currentPlayerIds) {
        // Get the players who have not yet had their turn
        HashSet<UUID> playersWithoutTurn = new HashSet<>(currentPlayerIds);
        playersWithoutTurn.removeAll(round.getPlayersWhoTookTurn());

        if (playersWithoutTurn.isEmpty()) {
            // If everyone had their turn, begin next round
            nextRound(round, currentPlayerIds);
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
