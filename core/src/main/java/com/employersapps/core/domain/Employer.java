package com.employersapps.core.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Employer implements ListItem<Employer>, Serializable {

    private final long id;
    private final int postId;
    private final int roleId;
    private final int statusId;
    private final String lastName;
    private final String firstName;
    private final String login;
    private final String photoPath;
    private final boolean isLocationPublic;
    private final Post post;
    private final Status status;
    private final LocalDate startVacationDate;
    private final LocalDate endVacationDate;
    private final String vacationComment;

    public Employer(long id,
                    int postId,
                    int roleId,
                    int statusId,
                    String lastName,
                    String firstName,
                    String login,
                    String photoPath,
                    boolean isLocationPublic,
                    Post post,
                    Status status,
                    LocalDate startVacationDate,
                    LocalDate endVacationDate,
                    String vacationComment) {
        this.id = id;
        this.postId = postId;
        this.roleId = roleId;
        this.statusId = statusId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.login = login;
        this.post = post;
        this.status = status;
        this.photoPath = photoPath;
        this.isLocationPublic = isLocationPublic;
        this.startVacationDate = startVacationDate;
        this.endVacationDate = endVacationDate;
        this.vacationComment = vacationComment;
    }

    public Post getPost() {
        return post;
    }

    public long getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getStatusId() {
        return statusId;
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

    public String getPhotoPath() {
        return photoPath;
    }

    public boolean isLocationPublic() {
        return isLocationPublic;
    }

    public Status getStatus() {
        return status;
    }

    public String getFullName() {
        return String.format("%s %s",
                firstName, lastName);
    }

    public String getVacationComment() {
        return vacationComment;
    }

    public LocalDate getStartVacationDate() {
        return startVacationDate;
    }

    public LocalDate getEndVacationDate() {
        return endVacationDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employer employer = (Employer) o;
        return id == employer.id &&
                postId == employer.postId &&
                roleId == employer.roleId &&
                statusId == employer.statusId &&
                isLocationPublic == employer.isLocationPublic &&
                Objects.equals(lastName, employer.lastName) &&
                Objects.equals(firstName, employer.firstName) &&
                Objects.equals(login, employer.login) &&
                Objects.equals(photoPath, employer.photoPath) &&
                Objects.equals(post, employer.post) &&
                Objects.equals(status, employer.status) &&
                Objects.equals(startVacationDate, employer.startVacationDate) &&
                Objects.equals(endVacationDate, employer.endVacationDate) &&
                Objects.equals(vacationComment, employer.vacationComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postId, roleId, statusId, lastName, firstName, login, photoPath, isLocationPublic, post, status, startVacationDate, endVacationDate, vacationComment);
    }

    public String getShortFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean areItemsTheSame(Employer other) {
        return id == other.id;
    }

    @Override
    public boolean areContentsTheSame(Employer other) {
        return equals(other);
    }
}
