package date.oxi.spyword.repository;

import date.oxi.spyword.entity.RoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {
}