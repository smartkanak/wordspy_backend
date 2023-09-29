package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import date.oxi.spyword.model.RoundState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

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

    public RoundDto() {
        this.goodWord = null;
        this.badWord = null;
        this.spyId = null;
        this.playersTurnId = null;
        this.state = RoundState.WAITING_FOR_PLAYERS;
        this.playersWhoTookTurn = new HashSet<>();
        this.number = 1;
    }

    public void increaseRoundNumber() {
        number += 1;
    }

}
