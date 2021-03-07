package com.employersapps.employersapp.presentation.notifications_fragment.state;

import com.employersapps.core.domain.UserNotification;

import java.util.List;

public class ListState {

    public static class Success implements NotificationsFragmentState {

        private final List<UserNotification> list;

        public Success(List<UserNotification> list) {
            this.list = list;
        }

        public List<UserNotification> getList() {
            return list;
        }
    }

    public static class Error implements NotificationsFragmentState {
        private final Throwable throwable;

        public Error(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}
