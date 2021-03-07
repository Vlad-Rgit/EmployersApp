package com.employersapps.employersapp.presentation.image_viewer_fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.employersapps.employersapp.databinding.FragmentImageViewerBinding;

public class ImageViewerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentImageViewerBinding binding = FragmentImageViewerBinding.inflate(
                inflater,
                container,
                false
        );


        return binding.getRoot();
    }
}
