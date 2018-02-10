package com.drwitteck.wittecklab2encryption;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editText;
    Button encryptButton, decryptButton, requestKeyPair;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cursor = getContentResolver().query(Uri.parse("content://com.drwitteck.wittecklab2encryption.encryptionprovider")
                , null, null, null, null);

        editText = findViewById(R.id.editTextToEncrypt);

        requestKeyPair = findViewById(R.id.buttonRequestKeyPair);
        requestKeyPair.setOnClickListener(this);

        encryptButton = findViewById(R.id.buttonEncrypt);
        encryptButton.setOnClickListener(this);

        decryptButton = findViewById(R.id.buttonDecrypt);
        decryptButton.setOnClickListener(this);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.buttonRequestKeyPair:
                //request keys returned in cursor
                Toast.makeText(this, "RSA keys have been saved", Toast.LENGTH_SHORT).show();
                break;

            case R.id.buttonEncrypt:
                //encrypt text
                break;

            case R.id.buttonDecrypt:
                //decrypt text
                break;

            default:
                break;
        }
    }
}
