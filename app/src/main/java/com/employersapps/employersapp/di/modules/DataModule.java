package com.employersapps.employersapp.di.modules;

import com.employersapps.core.data.AdminNotificationsDataSource;
import com.employersapps.core.data.ChatDataSource;
import com.employersapps.core.data.ChatMuteStateDataSource;
import com.employersapps.core.data.EditEmployersDataSource;
import com.employersapps.core.data.EmployerChangesDataSource;
import com.employersapps.core.data.EmployersDataSource;
import com.employersapps.core.data.FmsTokenDataSource;
import com.employersapps.core.data.MarkedMessageDataSource;
import com.employersapps.core.data.MessagesDataSource;
import com.employersapps.core.data.NewsDataSource;
import com.employersapps.core.data.NotificationDataSource;
import com.employersapps.core.data.PostDataSource;
import com.employersapps.core.data.RealtimeMessagesDataSource;
import com.employersapps.core.data.RolesDataSource;
import com.employersapps.core.data.UserCoordsDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.employersapp.framework.data.ApiAdminNotificationDataSource;
import com.employersapps.employersapp.framework.data.ApiChatDataSource;
import com.employersapps.employersapp.framework.data.ApiChatMuteStateDataSource;
import com.employersapps.employersapp.framework.data.ApiEditEmployerDataSource;
import com.employersapps.employersapp.framework.data.ApiEmployersDataSource;
import com.employersapps.employersapp.framework.data.ApiFmsTokenDataSource;
import com.employersapps.employersapp.framework.data.ApiMessagesDataSource;
import com.employersapps.employersapp.framework.data.ApiNewsDataSource;
import com.employersapps.employersapp.framework.data.ApiPostsDataSource;
import com.employersapps.employersapp.framework.data.ApiRolesDataSource;
import com.employersapps.employersapp.framework.data.ApiUserCoordsDataSource;
import com.employersapps.employersapp.framework.data.ApiUserDataSource;
import com.employersapps.employersapp.framework.data.RoomMarkedMessageDataSource;
import com.employersapps.employersapp.framework.data.RoomNotificationDataSource;
import com.employersapps.employersapp.framework.data.WebSocketEmployerChangedDataSource;
import com.employersapps.employersapp.framework.data.WebSocketRealtimeMessagesDataSource;

import dagger.Binds;
import dagger.Module;

@Module
public interface DataModule {
    @Binds
    UserDataSource bindUserDataSource(ApiUserDataSource impl);

    @Binds
    NewsDataSource bindNewsDataSource(ApiNewsDataSource impl);

    @Binds
    NotificationDataSource bindNotificationsDataSource(RoomNotificationDataSource impl);

    @Binds
    EmployersDataSource bindEmployersDataSource(ApiEmployersDataSource impl);

    @Binds
    EmployerChangesDataSource bindEmployersChangesDataSource(
            WebSocketEmployerChangedDataSource impl
    );

    @Binds
    UserCoordsDataSource binApiUserCoordsDataSource(
            ApiUserCoordsDataSource impl
    );

    @Binds
    MessagesDataSource bindMessagesDataSource(
            ApiMessagesDataSource impl
    );

    @Binds
    FmsTokenDataSource bindFmsTokenDataSource(
            ApiFmsTokenDataSource impl
    );

    @Binds
    RealtimeMessagesDataSource bindRealtimeMessagesDataSource(
            WebSocketRealtimeMessagesDataSource impl
    );

    @Binds
    PostDataSource bindPostsDataSource(ApiPostsDataSource impl);

    @Binds
    RolesDataSource bindRolesDataSource(ApiRolesDataSource impl);

    @Binds
    EditEmployersDataSource bindEditEmployersDataSource(ApiEditEmployerDataSource impl);

    @Binds
    AdminNotificationsDataSource bindAdminNotificationDataSource(
            ApiAdminNotificationDataSource impl
    );

    @Binds
    ChatDataSource bindChatDataSource(ApiChatDataSource impl);

    @Binds
    ChatMuteStateDataSource bindChatMuteStateDataSource(
            ApiChatMuteStateDataSource impl);

    @Binds
    MarkedMessageDataSource bindMarkedMessageDataSource(
            RoomMarkedMessageDataSource impl
    );

}
