package com.drwitteck.wittecklab2encryption;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ReadActivity extends AppCompatActivity {

    private TextView textView;
    //private TextView textView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        textView = findViewById(R.id.text_view);
        //textView2 = findViewById(R.id.text_view2);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            //NdefMessage message1 = (NdefMessage) rawMessages[1];
            textView.setText(new String(message.getRecords()[0].getPayload()));
            //textView2.setText(new String(message1.getRecords()[1].getPayload()));

        } else
            textView.setText("Waiting for NDEF Message");

    }
}
