package date.oxi.spyword.mapper;

import date.oxi.spyword.dto.RoomDto;
import date.oxi.spyword.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoomMapper {

    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    RoomDto toDto(RoomEntity roomEntity);
}
