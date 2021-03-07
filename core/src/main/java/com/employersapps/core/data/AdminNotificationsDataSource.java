package com.employersapps.core.data;

import com.employersapps.core.domain.AdminNotification;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;

public interface AdminNotificationsDataSource {
    Deferrable<ServerResponse> postNotification(AdminNotification notification);
}
