package com.drwitteck.wittecklab2encryption;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editText;
    TextView result;
    Button encryptButton, decryptButton, requestKeyPair;
    String userEnteredText, publicKeyString, privateKeyString;
    Cursor cursor;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte[] bytes;

    RSA rsa = new RSA();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextToEncrypt);
        userEnteredText = editText.getText().toString();

        result = findViewById(R.id.textViewResult);

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
                try {
                    requestKeys();
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.buttonEncrypt:
                try {
                    encryptText();
                } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.buttonDecrypt:
                try {
                    decryptText();
                } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    public void requestKeys() throws InvalidKeySpecException, NoSuchAlgorithmException {
        cursor = getContentResolver()
                .query(Uri.parse("content://com.drwitteck.wittecklab2encryption.encryptionprovider")
                        , null, null, null, null);

        assert cursor != null;
        cursor.moveToNext();
        cursor.close();

        publicKeyString = cursor.getString(0);
        Log.e("**********PUBLIC KEY", cursor.getString(0));
        privateKeyString = cursor.getString(1);
        Log.e("**********PRIVATE KEY", cursor.getString(1));

        publicKey = rsa.publicKeyFromString(publicKeyString);
        privateKey = rsa.privateKeyFromString(privateKeyString);

        decryptButton.setEnabled(false);

        Toast.makeText(this, "Keys requested and saved.", Toast.LENGTH_SHORT).show();
    }

    public void encryptText() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        if(TextUtils.isEmpty(editText.getText().toString())){
            editText.setError("Please enter text to encrypt");
        } else {
            bytes = rsa.encrypt(publicKey, rsa.stringToBytes(editText.getText().toString()));
            editText.setText(rsa.bytesToString(bytes));

            decryptButton.setEnabled(true);
            encryptButton.setEnabled(false);
        }
    }

    public void decryptText() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            editText.setText(rsa.decrypt(privateKey, bytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        decryptButton.setEnabled(false);
    }



}
