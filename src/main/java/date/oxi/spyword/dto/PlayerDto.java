package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Schema
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerDto {

        @Schema(description = "Player id", example = "25b43e41-1305-4ace-8c6c-871da6487cf9")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final UUID id;

        @Schema(description = "Player name", example = "John")
        @JsonProperty()
        private final String name;
}
