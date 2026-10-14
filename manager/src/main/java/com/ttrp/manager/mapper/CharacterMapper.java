package com.ttrp.manager.mapper;

import com.ttrp.manager.dto.character.CharacterRequest;
import com.ttrp.manager.dto.character.CharacterResponse;
import com.ttrp.manager.entity.Character;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CharacterMapper {

    @Mapping(target = "userId", source = "user.id")
    CharacterResponse toResponse(Character character);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Character toEntity(CharacterRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    void updateEntity(@MappingTarget Character character, CharacterRequest request);
}
