package com.example.throughpass.Main.fragments.ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.throughpass.R;

public class TicketFragment extends Fragment {



    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*
        * 티켓 정보 등록
        * 티켓 등록 상태 표시!
        *
        *
         */

        view = inflater.inflate(R.layout.fragment_ticket, container, false);
        return view;


    }
}