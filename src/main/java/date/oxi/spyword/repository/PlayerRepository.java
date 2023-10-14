package date.oxi.spyword.repository;

import date.oxi.spyword.dto.PlayerDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerRepository extends JpaRepository<PlayerDto, UUID> {
}
