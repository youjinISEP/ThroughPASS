package com.example.throughpass.Main.fragments.ticket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.throughpass.Main.popup.WriteTicketCodePopup;
import com.example.throughpass.R;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.Prop;

import java.math.BigInteger;
import java.util.Date;

public class TicketFragment extends Fragment {
    final static int TICKET_POPUP_CODE = 111;
    ImageButton registBtn;
    TextView ticketStatus, name, registTime;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
              /*
         * 티켓 정보 등록
         *
         *
         *
         *
         * 티켓 등록 상태 표시!
         *
         *
         */

        view = inflater.inflate(R.layout.fragment_ticket, container, false);
        registBtn = (ImageButton) view.findViewById(R.id.btn_tRegist);
        ticketStatus = view.findViewById(R.id.txt_tStatus);
        name = view.findViewById(R.id.txt_rName);
        registTime = view.findViewById(R.id.txt_rRegistTime);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 티켓 정보 바 변경
        if(Func.INSTANCE.checkRegistTicket()) {  // 티켓 정보가 등록되어있다면
            ticketStatus.setBackgroundResource(R.color.colorTrueTicketInfoBox);
            ticketStatus.setText(R.string.trueTicketInfoBoxTxt);
            name.setText(Prop.INSTANCE.getTicketCode());
            registTime.setText(Prop.INSTANCE.getRegistDateStr());
        }
        else {  // 티켓 정보가 등록되어있지 않다면
            ticketStatus.setBackgroundResource(R.color.colorFalseTicketInfoBox);
            ticketStatus.setText(R.string.falseTicketInfoBoxTxt);
            name.setText(" - ");
            registTime.setText(" - ");
        }

        // 티켓 등록 버튼 클릭 이벤트
        registBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Func.INSTANCE.checkRegistTicket()) {
                    Toast.makeText(getActivity(), "현재 티켓이 등록되어 있습니다. \n 최대 등록 횟수 : 1회", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), WriteTicketCodePopup.class);
                    startActivityForResult(intent, TICKET_POPUP_CODE);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TICKET_POPUP_CODE) {
            if(resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                Toast.makeText(getActivity(), "ticket : " + Prop.INSTANCE.getTicketCode() + " \n " + Prop.INSTANCE.getRegistDateStr(), Toast.LENGTH_LONG).show();
                Func.INSTANCE.refreshFragment(this, getFragmentManager());
            }
        }
    }
}