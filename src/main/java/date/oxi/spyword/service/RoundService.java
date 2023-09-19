package date.oxi.spyword.service;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.dto.RoundDto;
import date.oxi.spyword.model.RoundState;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
public class RoundService {

    public void start(RoundDto round, HashSet<PlayerDto> players) {
        // Get
        String goodWord = "Zauberstab"; // TODO: Get real data
        String badWord = "Flugbesen";
        PlayerDto spy = getRandomSetElement(players);
        PlayerDto playersTurn = getRandomSetElement(players);

        // Set
        round.setGoodWord(goodWord);
        round.setBadWord(badWord);
        round.setSpyId(spy.getId());
        round.setPlayersTurnId(playersTurn.getId());
        round.setRoundState(RoundState.RUNNING);
    }

    public void nextTurn(RoundDto round, HashSet<UUID> currentPlayersUUIDs) {
        // Get the players who have not yet had their turn
        HashSet<UUID> playersWithoutTurn = new HashSet<>(currentPlayersUUIDs);
        playersWithoutTurn.removeAll(round.getPlayersWhoTookTurn());

        if (playersWithoutTurn.isEmpty()) {
            // If everyone had their turn, begin next round
            nextRound(round, currentPlayersUUIDs);
        } else {
            // Else randomly choose one of the players with no turn yet
            UUID nextPlayersTurnId = getRandomSetElement(playersWithoutTurn);
            round.setPlayersTurnId(nextPlayersTurnId);
        }
    }

    private void nextRound(RoundDto round, HashSet<UUID> currentPlayersUUIDs) {
        // Any players turn
        UUID playersTurnId = getRandomSetElement(currentPlayersUUIDs);
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
