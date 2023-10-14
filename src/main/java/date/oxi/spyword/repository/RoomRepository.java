package date.oxi.spyword.repository;

import date.oxi.spyword.dto.RoomDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<RoomDto, UUID> {
    RoomDto findByCode(String code);
}
