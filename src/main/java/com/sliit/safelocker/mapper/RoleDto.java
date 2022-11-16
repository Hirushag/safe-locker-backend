package com.sliit.safelocker.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean isExternal;




}
