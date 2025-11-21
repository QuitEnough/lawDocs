package com.yana.filestorage.mapper;

import com.yana.filestorage.api.dto.DirectoryResponse;
import com.yana.filestorage.entity.Directory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DirectoryMapper {

    DirectoryResponse toDto(Directory directory);

    Directory toEntity(DirectoryResponse dto);

}
