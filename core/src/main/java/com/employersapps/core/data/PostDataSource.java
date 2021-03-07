package com.employersapps.core.data;

import com.employersapps.core.domain.Post;
import com.employersapps.core.utils.Deferrable;

import java.util.List;

public interface PostDataSource {
    Deferrable<List<Post>> getPosts();
}
