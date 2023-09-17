package date.oxi.spyword.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class CreatePlayerRequest {

        @Schema(description = "Player name", example = "John")
        private final String name;

        @Schema(description = "Player language code", example = "en")
        private final String languageCode;
}
