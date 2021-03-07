package com.employersapps.core.data;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.utils.Deferrable;

import java.util.List;

import io.reactivex.rxjava3.subjects.Subject;

public interface EmployersDataSource {
    Subject<List<Employer>> getEmployers();
    Deferrable<Employer> getEmployer(long userId);
    Subject<Throwable> getExceptions();
    void refreshEmployers();
    Deferrable<Void> updateStatus(long user, int statusId);
}
