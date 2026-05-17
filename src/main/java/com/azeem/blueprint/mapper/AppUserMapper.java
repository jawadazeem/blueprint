/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.mapper;

import com.azeem.blueprint.entity.AppUserEntity;
import com.azeem.blueprint.model.user.AppUser;
import org.springframework.stereotype.Component;

/** Mapper class to convert between AppUser domain model and AppUserEntity database entity. */
@Component
public class AppUserMapper {
  public AppUserEntity mapToEntity(AppUser appUser) {
    AppUserEntity appUserEntity = new AppUserEntity();
    appUserEntity.setId(appUser.id());
    appUserEntity.setProvider(appUser.provider());
    appUserEntity.setProviderSubject(appUser.providerSubject());
    appUserEntity.setEmail(appUser.email());
    appUserEntity.setDisplayName(appUser.displayName());
    appUserEntity.setPictureUrl(appUser.pictureUrl());
    appUserEntity.setRole(appUser.role());
    appUserEntity.setCreatedAt(appUser.createdAt());
    appUserEntity.setLastLoginAt(appUser.lastLoginAt());
    return appUserEntity;
  }

  public AppUser mapToDomain(AppUserEntity appUserEntity) {
    return new AppUser(
        appUserEntity.getId(),
        appUserEntity.getProvider(),
        appUserEntity.getProviderSubject(),
        appUserEntity.getEmail(),
        appUserEntity.getDisplayName(),
        appUserEntity.getPictureUrl(),
        appUserEntity.getRole(),
        appUserEntity.getCreatedAt(),
        appUserEntity.getLastLoginAt());
  }
}
