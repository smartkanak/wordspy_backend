package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

@Data
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDto {

    @Schema(description = "Room id", example = "25b43e41-1305-4ace-8c6c-871da6487cf9")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final UUID id;

    @Schema(description = "Room code", example = "John")
    private final String code;

    @Schema(description = "Host of the room")
    private final PlayerDto host;

    @Schema(description = "Players in the room")
    private final HashSet<PlayerDto> players;

    @Schema(description = "Current round in the room")
    private final RoundDto round;

    public RoomDto(PlayerDto host, String code) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.host = host;
        this.players = new HashSet<>();
        this.players.add(host);
        this.round = new RoundDto();
    }
}