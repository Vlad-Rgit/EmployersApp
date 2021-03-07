package com.employersapps.employersapp.presentation.notification_details_fragment.state;

import com.employersapps.core.domain.UserNotification;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsDetailsState;

public abstract class TransactionState {

    public static class TransactionSuccessState extends SuccessState<UserNotification>
            implements NotificationDetailsState {
        public TransactionSuccessState(UserNotification result) {
            super(result);
        }
    }

    public static class TransactionErrorState extends ErrorState
            implements NotificationDetailsState {

        public TransactionErrorState(Throwable throwable) {
            super(throwable);
        }
    }
}
