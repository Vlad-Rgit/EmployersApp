package com.employersapps.core.domain;

public class User {

    private final long id;
    private final String lastName;
    private final String firstName;
    private final String login;
    private final int postId;
    private final int statusId;
    private final int roleId;


    public User(long id,
                String lastName,
                String firstName,
                String login,
                int postId,
                int statusId,
                int roleId) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.login = login;
        this.postId = postId;
        this.statusId = statusId;
        this.roleId = roleId;
    }


    public long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLogin() {
        return login;
    }

    public int getPostId() {
        return postId;
    }

    public int getStatusId() {
        return statusId;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
