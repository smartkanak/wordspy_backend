package date.oxi.spyword.service;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.repository.PlayerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    @NonNull
    private final PlayerRepository repo;

    public PlayerDto createPlayer(String name, String languageCode) {
        return repo.save(PlayerDto.register(name, languageCode));
    }
}