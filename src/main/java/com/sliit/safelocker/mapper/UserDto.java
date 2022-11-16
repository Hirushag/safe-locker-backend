package com.sliit.safelocker.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {
    private final Long id;
    private final String name;
    private final String email;
    private final String username;
    private final String password;
    private final boolean isActive;
    private final String section;
    private final String designation;
    private final RoleDto role;
}
