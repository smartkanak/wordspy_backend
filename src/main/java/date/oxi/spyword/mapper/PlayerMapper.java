package date.oxi.spyword.mapper;

import date.oxi.spyword.dto.PlayerDto;
import date.oxi.spyword.entity.PlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlayerMapper {

    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    PlayerDto toDto(PlayerEntity playerEntity);
}
