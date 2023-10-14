package date.oxi.spyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class RoomDto {

    @Schema(description = "Room id", example = "25b43e41-1305-4ace-8c6c-871da6487cf9")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Schema(description = "Room code", example = "KDJSUE")
    private String code;

    @Schema(description = "Host of the room")
    @OneToOne(cascade = CascadeType.ALL)
    private PlayerDto host;

    @Schema(description = "Players in the room")
    @OneToMany(cascade = CascadeType.PERSIST)
    private Set<PlayerDto> players;

    @Schema(description = "Current round in the room")
    @OneToOne(cascade = CascadeType.ALL)
    private RoundDto round;

    public RoomDto(PlayerDto host, String code, RoundDto round) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.host = host;
        this.players = new HashSet<>();
        this.players.add(host);
        this.round = round;
    }
}