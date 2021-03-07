package com.employersapps.core.utils;

import org.jetbrains.annotations.Nullable;

public interface SessionManager {
    void setJwtToken(String token);
    @Nullable String getJwtToken();
}
