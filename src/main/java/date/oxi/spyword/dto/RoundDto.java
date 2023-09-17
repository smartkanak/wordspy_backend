package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import date.oxi.spyword.model.RoundState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundDto {

    @Schema(description = "The good word for the good team")
    private final String goodWord;

    @Schema(description = "The bad word for the bad team")
    private final String badWord;

    @Schema(description = "The state of the round")
    private final RoundState state;

    @Schema(description = "The player whose turn it is")
    private final PlayerDto turn;

    @Schema(description = "The player who is the spy")
    private final PlayerDto spy;

    public static RoundDto reset() {
        return RoundDto.builder()
                .goodWord(null)
                .badWord(null)
                .state(RoundState.WAITING)
                .turn(null)
                .spy(null)
                .build();
    }
}
