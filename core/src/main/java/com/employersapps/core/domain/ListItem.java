package com.employersapps.core.domain;

public interface ListItem<T>{
    boolean areItemsTheSame(T other);
    boolean areContentsTheSame(T other);
}

