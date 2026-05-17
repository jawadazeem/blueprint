/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.model.user;

import java.time.Instant;
import java.util.UUID;

/**
 * AppUser DTO
 *
 * <p>Represents an application's logged-in user.
 *
 * <p>Users login through Google using OAuth 2.0
 */
public record AppUser(
    UUID id, // domain identity (also DB primary key)
    String provider,
    String providerSubject,
    String email,
    String displayName,
    String pictureUrl,
    String role,
    Instant createdAt,
    Instant lastLoginAt) {}
