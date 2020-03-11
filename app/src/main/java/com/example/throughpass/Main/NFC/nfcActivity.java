package com.example.throughpass.Main.NFC;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.throughpass.R;

public class nfcActivity extends AppCompatActivity {

    private Toolbar nfcToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        nfcToolbar = findViewById(R.id.toolbar);
        nfcToolbar.setTitle("NFC 태그 활성화");
    }
}
