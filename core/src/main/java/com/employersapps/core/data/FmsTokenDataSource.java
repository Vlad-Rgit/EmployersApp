package com.employersapps.core.data;

import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;

public interface FmsTokenDataSource {
    Deferrable<ServerResponse> updateToken(String token);
}
