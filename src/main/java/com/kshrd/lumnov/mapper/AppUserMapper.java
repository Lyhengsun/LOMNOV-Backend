package com.kshrd.lumnov.mapper;

import org.mapstruct.Mapper;

import com.kshrd.lumnov.model.dto.response.AppUserResponse;
import com.kshrd.lumnov.model.entity.AppUser;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    AppUserResponse toAppUserResponse(AppUser appUser);
}