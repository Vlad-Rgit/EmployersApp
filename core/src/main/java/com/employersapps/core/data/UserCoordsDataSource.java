package com.employersapps.core.data;

import com.employersapps.core.domain.UserCoords;
import com.employersapps.core.utils.Deferrable;

public interface UserCoordsDataSource {
    Deferrable<Void> sendLocation(UserCoords userCoords);
}
