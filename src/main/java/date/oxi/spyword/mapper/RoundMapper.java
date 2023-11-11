package date.oxi.spyword.mapper;

import date.oxi.spyword.dto.RoundDto;
import date.oxi.spyword.entity.RoundEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoundMapper {

    RoundMapper INSTANCE = Mappers.getMapper(RoundMapper.class);

    RoundDto toDto(RoundEntity roundEntity);
}
