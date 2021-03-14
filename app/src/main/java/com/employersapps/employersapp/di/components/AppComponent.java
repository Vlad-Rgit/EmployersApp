package com.employersapps.employersapp.di.components;

import android.app.Application;
import android.content.Context;

import com.employersapps.core.data.FmsTokenDataSource;
import com.employersapps.core.data.SaveAuthHelper;
import com.employersapps.core.data.UserCoordsDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.employersapp.di.modules.DataModule;
import com.employersapps.employersapp.di.modules.JsonModule;
import com.employersapps.employersapp.di.modules.RetrofitModule;
import com.employersapps.employersapp.di.modules.RoomModule;
import com.employersapps.employersapp.di.modules.UtilModule;
import com.employersapps.employersapp.framework.database.AppDatabase;
import com.employersapps.employersapp.framework.mappers.NotificationMapper;
import com.employersapps.employersapp.framework.services.LocationLoggerService;
import com.employersapps.employersapp.presentation.chat_fragment.viewmodel.ChatViewModel;
import com.employersapps.employersapp.presentation.employer_details_fragment.viewmodel.EmployerDetailsViewModel;
import com.employersapps.employersapp.presentation.employer_edit_fragment.viewmodel.EmployerEditViewModel;
import com.employersapps.employersapp.presentation.employers_choice_list.viewmodel.EmployersChoiceListViewModel;
import com.employersapps.employersapp.presentation.employers_fragment.viewmodel.EmployersViewModel;
import com.employersapps.employersapp.presentation.group_chat_details.viewmodel.GroupChatDetailsViewModel;
import com.employersapps.employersapp.presentation.login_fragment.viewmodel.LoginFragmentViewModel;
import com.employersapps.employersapp.presentation.marked_messages_fragment.viewmodel.MarkedMessagesViewModel;
import com.employersapps.employersapp.presentation.messages_fragment.viewmodel.MessagesViewModel;
import com.employersapps.employersapp.presentation.news_details_fragment.viewmodel.NewsDetailsViewModel;
import com.employersapps.employersapp.presentation.news_fragment.viewmodel.NewsFragmentViewModel;
import com.employersapps.employersapp.presentation.notification_details_fragment.viewmodel.NotificationDetailsViewModel;
import com.employersapps.employersapp.presentation.notifications_fragment.viewmodel.NotificationsViewModel;
import com.employersapps.employersapp.presentation.profile_fragment.ProfileViewModel;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = {
        DataModule.class,
        RetrofitModule.class,
        JsonModule.class,
        UtilModule.class,
        RoomModule.class})
public interface AppComponent {

    void inject(LoginFragmentViewModel viewModel);
    void inject(NewsFragmentViewModel viewModel);
    void inject(NewsDetailsViewModel viewModel);
    void inject(NotificationsViewModel viewModel);
    void inject(NotificationDetailsViewModel viewModel);
    void inject(EmployersViewModel viewModel);
    void inject(EmployerDetailsViewModel viewModel);
    void inject(LocationLoggerService service);
    void inject(ProfileViewModel viewModel);
    void inject(MessagesViewModel viewModel);
    void inject(ChatViewModel viewModel);
    void inject(EmployerEditViewModel viewModel);
    void inject(EmployersChoiceListViewModel viewModel);
    void inject(GroupChatDetailsViewModel viewModel);
    void inject(MarkedMessagesViewModel viewModel);


    Gson getGson();
    SaveAuthHelper getSaveAuthHelper();
    UserDataSource getUserDataSource();
    AppDatabase getDatabase();
    NotificationMapper getNotificationMapper();
    OkHttpClient getOkHttpClient();
    UserCoordsDataSource getUserCordsDataSource();
    FmsTokenDataSource getFmsTokenDataSource();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);

        @BindsInstance
        Builder app(Application application);

        AppComponent build();
    }
}
