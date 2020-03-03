package com.example.throughpass.Main.fragments.ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.throughpass.Main.popup.WriteTicketCodePopup;
import com.example.throughpass.R;
import com.example.throughpass.obj.Prop;

public class TicketFragment extends Fragment {
    final static int TICKET_POPUP_CODE = 111;
    ImageButton registBtn;

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
        registBtn = (ImageButton) view.findViewById(R.id.btn_tRegist);
        return view;
//        btn_tRegist
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteTicketCodePopup.class);
                startActivityForResult(intent, TICKET_POPUP_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TICKET_POPUP_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Prop.INSTANCE.setTicketCode(data.getStringExtra("ticketCode"));
                Toast.makeText(getActivity(), Prop.INSTANCE.getTicketCode(), Toast.LENGTH_LONG).show();

            }
        }
    }
}