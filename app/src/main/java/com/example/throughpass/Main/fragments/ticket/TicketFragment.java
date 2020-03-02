package com.example.throughpass.Main.fragments.ticket;

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

public class TicketFragment extends Fragment {

    private TicketViewModel ticketViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*
        * 티켓 정보 등록
        * 티켓 등록 상태 표시!
        *
        *
         */
        ticketViewModel =
                ViewModelProviders.of(this).get(TicketViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ticket, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        ticketViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}