package com.sliit.safelocker.mapper;

import com.sliit.safelocker.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleDtoMapper implements DtoMapper<Role,RoleDto>{
    public  Role mapFrom(RoleDto roleDto){

        return  Role.builder()
                .name(roleDto.getName())
                .id(roleDto.getId())
                .code(roleDto.getCode())
                .description(roleDto.getDescription())
                .build();
    }


    public RoleDto mapTo(Role role){

        return RoleDto.builder()
                .name(role.getName())
                .code(role.getCode())
                .description(role.getDescription())
                .build();
    }
}
