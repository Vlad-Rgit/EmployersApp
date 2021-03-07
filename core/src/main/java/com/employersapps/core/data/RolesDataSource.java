package com.employersapps.core.data;

import com.employersapps.core.domain.Role;
import com.employersapps.core.utils.Deferrable;

import java.util.List;

public interface RolesDataSource {
    Deferrable<List<Role>> getRoles();
}
