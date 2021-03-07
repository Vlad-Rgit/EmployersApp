package com.employersapps.core.data;

import com.employersapps.core.domain.Employer;

import java.io.Closeable;

import io.reactivex.rxjava3.subjects.Subject;

public interface EmployerChangesDataSource extends Closeable {
    void init(Employer employer);
    void init(long employerId);
    Subject<Employer> getChanges();
}
