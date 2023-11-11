package date.oxi.spyword.entity;

import date.oxi.spyword.model.RoundState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String goodWord;

    private String badWord;

    private UUID spyId;

    private UUID playersTurnId;

    @Builder.Default
    private RoundState state = RoundState.WAITING_FOR_PLAYERS;

    @ElementCollection
    @Builder.Default
    private Set<UUID> playersWhoTookTurn = new HashSet<>();

    @Builder.Default
    private Integer number = 1;

    private Integer minRounds;

    private Integer maxRounds;

    @ElementCollection
    @Builder.Default
    private Map<UUID, Boolean> playersWhoVotedForEndingGame = new HashMap<>();

    @ElementCollection
    @Builder.Default
    private Map<UUID, UUID> playersWhoVotedForSpy = new HashMap<>();

    @ElementCollection
    @Builder.Default
    private Map<UUID, Integer> spyVoteCounter = new HashMap<>();

    public void reset() {
        RoundEntity newRound = new RoundEntity();
        this.goodWord = newRound.getGoodWord();
        this.badWord = newRound.getBadWord();
        this.spyId = newRound.getSpyId();
        this.playersTurnId = newRound.getPlayersTurnId();
        this.state = newRound.getState();
        this.playersWhoTookTurn = newRound.getPlayersWhoTookTurn();
        this.number = newRound.getNumber();
        this.minRounds = newRound.getMinRounds();
        this.maxRounds = newRound.getMaxRounds();
        this.playersWhoVotedForEndingGame = newRound.getPlayersWhoVotedForEndingGame();
        this.playersWhoVotedForSpy = newRound.getPlayersWhoVotedForSpy();
        this.spyVoteCounter = newRound.getSpyVoteCounter();
    }

    public void increaseRoundNumber() {
        number += 1;
    }

    public void increaseMinRounds() {
        minRounds += 1;
    }

}
