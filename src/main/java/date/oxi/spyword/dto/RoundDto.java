package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import date.oxi.spyword.model.RoundState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

@Data
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundDto {

    @Schema(description = "The good word for the good team")
    private String goodWord;

    @Schema(description = "The bad word for the bad team")
    private String badWord;

    @Schema(description = "The player who is the spy")
    private UUID spyId;

    @Schema(description = "UUID of the player whose turn it is")
    private UUID playersTurnId;

    @Schema(description = "The state of the round")
    private RoundState state;

    @Schema(description = "List of player UUID's who already took their turn in this round")
    private HashSet<UUID> playersWhoTookTurn;

    @Schema(description = "The number of the round")
    private Integer number;

    @Schema(description = "The minimum number of rounds")
    private Integer minRounds;

    @Schema(description = "The maximum number of rounds")
    private Integer maxRounds;

    @Schema(description = "List of player UUID's who voted to end the game")
    private Map<UUID, Boolean> playersWhoVotedForEndingGame;

    @Schema(description = "List of player UUID's who voted for a possible spy")
    private Map<UUID, UUID> playersWhoVotedForSpy;

    @Schema(description = "Player UUID's and how often they were voted for as a spy")
    private Map<UUID, Integer> spyVoteCounter;

    public RoundDto() {
        this.goodWord = null;
        this.badWord = null;
        this.spyId = null;
        this.playersTurnId = null;
        this.state = RoundState.WAITING_FOR_PLAYERS;
        this.playersWhoTookTurn = new HashSet<>();
        this.number = 1;
        this.minRounds = null;
        this.maxRounds = null;
        this.playersWhoVotedForEndingGame = new HashMap<>();
        this.playersWhoVotedForSpy = new HashMap<>();
        this.spyVoteCounter = null;
    }

    public void reset() {
        RoundDto newRound = new RoundDto();
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
