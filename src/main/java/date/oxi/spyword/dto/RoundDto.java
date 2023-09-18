package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import date.oxi.spyword.model.RoundState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundDto {

    @Schema(description = "The good word for the good team")
    private String goodWord;

    @Schema(description = "The bad word for the bad team")
    private String badWord;

    @Schema(description = "The player who is the spy")
    private Integer spyIndex;

    @Schema(description = "The player whose turn it is")
    private Integer turnIndex;

    @Schema(description = "The state of the round")
    private RoundState roundState;

    public RoundDto() {
        this.goodWord = null;
        this.badWord = null;
        this.spyIndex = null;
        this.turnIndex = null;
        this.roundState = RoundState.WAITING;
    }

    public void start(Integer spyIndex, Integer turnIndex) {
        this.goodWord = "Zauberstab";
        this.badWord = "Flugbesen";
        this.spyIndex = spyIndex;
        this.turnIndex = turnIndex;
        this.roundState = RoundState.RUNNING;
    }
}
