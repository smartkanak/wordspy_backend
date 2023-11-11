package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import date.oxi.spyword.model.RoundState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundDto {

    @Schema(description = "Round id", example = "25b43e41-1305-4ace-8c6c-871da6487cf9")
    private UUID id;

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
    private Set<UUID> playersWhoTookTurn;

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
}
