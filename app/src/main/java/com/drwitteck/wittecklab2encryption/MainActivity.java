package com.drwitteck.wittecklab2encryption;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editText;
    Button encryptButton, decryptButton, requestKeyPair;
    Cursor cursor;
    String userEnteredText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cursor = getContentResolver()
                .query(Uri.parse("content://com.drwitteck.wittecklab2encryption.encryptionprovider")
                , null, null, null, null);

//        assert cursor != null;
//        cursor.moveToNext();

//        Log.d("key", cursor.getString(0));

        editText = findViewById(R.id.editTextToEncrypt);
        userEnteredText = editText.toString();

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
                try {
                    EncryptionProvider ep = new EncryptionProvider();
                    ep.generateKeyPair();
                    Toast.makeText(this, "RSA keys have been saved.", Toast.LENGTH_SHORT).show();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.buttonEncrypt:
                RSA.encrypt(userEnteredText, cursor.getString(0));
                break;

            case R.id.buttonDecrypt:
                RSA.decrypt(userEnteredText, cursor.getString(1));
                break;

            default:
                break;
        }
    }
}
