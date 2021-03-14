package com.employersapps.core.domain.network;

import java.time.LocalDate;

public class PostEmployer {
    public final long id;
    public final String lastName;
    public final String firstName;
    public final String login;
    public final String password;
    public final int postId;
    public final int roleId;
    public final LocalDate startVacationDate;
    public final LocalDate endVacationDate;
    public final String vacationComment;
    public final boolean enablePrivateChatNotification;
    public final boolean enableGroupChatNotification;

    public PostEmployer(long id,
                        String lastName,
                        String firstName,
                        String login,
                        String password,
                        int postId,
                        int roleId,
                        LocalDate startVacationDate,
                        LocalDate endVacationDate,
                        String vacationComment,
                        boolean enablePrivateChatNotification,
                        boolean enableGroupChatNotification) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.postId = postId;
        this.roleId = roleId;
        this.startVacationDate = startVacationDate;
        this.endVacationDate = endVacationDate;
        this.vacationComment = vacationComment;
        this.enableGroupChatNotification = enableGroupChatNotification;
        this.enablePrivateChatNotification = enablePrivateChatNotification;
    }

    public PostEmployer(String lastName,
                        String firstName,
                        String login,
                        String password,
                        int postId,
                        int roleId,
                        LocalDate startVacationDate,
                        LocalDate endVacationDate,
                        String vacationComment) {
        this.id = 0;
        this.lastName = lastName;
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.postId = postId;
        this.roleId = roleId;
        this.startVacationDate = startVacationDate;
        this.endVacationDate = endVacationDate;
        this.vacationComment = vacationComment;
        this.enableGroupChatNotification = true;
        this.enablePrivateChatNotification = true;
    }
}
