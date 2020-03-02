package com.example.throughpass.Main.fragments.selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.throughpass.R;

public class SelectionFragment extends Fragment {

    private SelectionViewModel selectionViewModel;

    /*
     *
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectionViewModel =
                ViewModelProviders.of(this).get(SelectionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_selection, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        selectionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}