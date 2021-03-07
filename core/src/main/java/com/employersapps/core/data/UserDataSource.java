package com.employersapps.core.data;

import com.employersapps.core.domain.network.RequestResult;
import com.employersapps.core.domain.User;
import com.employersapps.core.domain.UserLogin;
import com.employersapps.core.utils.Deferrable;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import io.reactivex.rxjava3.core.Observable;

public interface UserDataSource {
    @NotNull
    Observable<List<User>> getUserListObservable();

    @NotNull
    User getCurrentUser();

    boolean isLogged();

    @NotNull
    Deferrable<RequestResult<User>> login(UserLogin user);
}
