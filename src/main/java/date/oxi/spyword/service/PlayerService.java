package date.oxi.spyword.service;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.entity.PlayerEntity;
import date.oxi.spyword.mapper.PlayerMapper;
import date.oxi.spyword.repository.PlayerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    @NonNull
    private final PlayerRepository repo;

    public PlayerDto createPlayer(String name, String languageCode) {
        PlayerEntity persistedPlayerEntity = repo.save(
                PlayerEntity.builder()
                        .name(name)
                        .languageCode(languageCode)
                        .build()
        );

        return PlayerMapper.INSTANCE.toDto(persistedPlayerEntity);
    }

    public Optional<PlayerEntity> findPlayerByUUID(UUID playerUUID) {
        return repo.findById(playerUUID);
    }
}