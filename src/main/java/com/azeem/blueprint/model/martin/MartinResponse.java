/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.model.martin;

public class MartinResponse {
    public String answer;
    public String sql;
    public String reasoning;

    public MartinResponse(String answer, String sql, String reasoning) {
        this.answer = answer;
        this.sql = sql;
        this.reasoning = reasoning;
    }
}
