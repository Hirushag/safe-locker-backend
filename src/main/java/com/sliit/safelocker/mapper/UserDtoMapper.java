package com.sliit.safelocker.mapper;
import com.sliit.safelocker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements DtoMapper<User,UserDto> {

    @Autowired
    RoleDtoMapper roleDtoMapper;

    @Override
    public User mapFrom(UserDto dto) {
        return  User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .isActive(dto.isActive())
                .role(roleDtoMapper.mapFrom(dto.getRole()))
                .build();

    }

    @Override
    public UserDto mapTo(User object) {
        return  UserDto.builder()
                .id(object.getId())
                .name(object.getName())
                .email(object.getEmail())
                .password(object.getPassword())
                .username(object.getUsername())
                .isActive(object.isActive())
                .build();

    }
}
