/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.AppUser;

import com.azeem.blueprint.entity.AppUserEntity;
import com.azeem.blueprint.mapper.AppUserMapper;
import com.azeem.blueprint.model.user.AppUser;
import com.azeem.blueprint.repository.AppUserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {
  private final AppUserRepository appUserRepository;
  private final AppUserMapper appUserMapper;

  public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
    this.appUserRepository = appUserRepository;
    this.appUserMapper = appUserMapper;
  }

  public AppUser getAppUserById(UUID userId) {
    return appUserMapper.mapToDomain(appUserRepository.getReferenceById(userId));
  }

  public AppUserEntity getAppUserEntityById(UUID userId) {
    return appUserRepository.getReferenceById(userId);
  }
}
