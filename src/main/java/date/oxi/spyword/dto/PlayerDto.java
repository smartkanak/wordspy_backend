package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerDto {

    @Schema(description = "Player id", example = "25b43e41-1305-4ace-8c6c-871da6487cf9")
    private UUID id;

    @Schema(description = "Player name", example = "John")
    private String name;

    @Schema(description = "Player language code", example = "en")
    private String languageCode;
}
