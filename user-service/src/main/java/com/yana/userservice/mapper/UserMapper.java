package com.yana.userservice.mapper;

import com.yana.userservice.dto.UserCreateRequest;
import com.yana.userservice.dto.UserResponse;
import com.yana.userservice.entity.User;
import com.yana.userservice.entity.UserDetailsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    UserResponse toUserResponse(User user);

    UserCreateRequest toUserRequest(User user);

    User toUser(UserCreateRequest userRequest);

    UserDetailsImpl toUserDetails(User user);

}
