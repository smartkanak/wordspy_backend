package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDto {

    @Schema(description = "Room id", example = "25b43e41-1305-4ace-8c6c-871da6487cf9")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Schema(description = "Room code", example = "KDJSUE")
    private String code;

    @Schema(description = "Host of the room")
    private PlayerDto host;

    @Schema(description = "Players in the room")
    private Set<PlayerDto> players;

    @Schema(description = "Current round in the room")
    private RoundDto round;
}