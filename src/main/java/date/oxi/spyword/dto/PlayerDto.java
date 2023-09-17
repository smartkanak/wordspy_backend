package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Schema
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerDto {

    @Schema(description = "Player id", example = "25b43e41-1305-4ace-8c6c-871da6487cf9")
    private final UUID id;

    @Schema(description = "Player name", example = "John")
    private final String name;

    @Schema(description = "Player language code", example = "en")
    private final String languageCode;

    public static PlayerDto register(
            @NonNull String name,
            String languageCode
    ) {
        if (languageCode == null) {
            languageCode = "en";
        }

        return PlayerDto.builder()
                .id(UUID.randomUUID())
                .name(name)
                .languageCode(languageCode)
                .build();
    }
}
