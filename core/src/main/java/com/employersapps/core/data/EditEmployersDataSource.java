package com.employersapps.core.data;


import com.employersapps.core.domain.network.PostEmployer;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;

public interface EditEmployersDataSource {
    Deferrable<ServerResponse> addEmployer(PostEmployer employer, String photoUri);
    Deferrable<ServerResponse> editEmployer(PostEmployer employer, String photoUri);
}
