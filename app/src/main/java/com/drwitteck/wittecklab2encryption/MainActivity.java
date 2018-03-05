package com.drwitteck.wittecklab2encryption;

import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener { //, NfcAdapter.CreateNdefMessageCallback

    EditText editText;
    Button encryptButton, decryptButton, requestKeyPair;
    String userEnteredText;
    boolean requested;
    RSA rsa;

    private final static String METHOD = "RSA";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rsa = new RSA(MainActivity.this);

        editText = findViewById(R.id.editTextToEncrypt);

//        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (mAdapter == null) {
//            editText.setText("Sorry this device does not have NFC.");
//            return;
//        }
//
//        if (!mAdapter.isEnabled()) {
//            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_LONG).show();
//        }
//
//        mAdapter.setNdefPushMessageCallback(this, MainActivity.this);

        editText = findViewById(R.id.editTextToEncrypt);
        userEnteredText = editText.getText().toString();

        requestKeyPair = findViewById(R.id.buttonRequestKeyPair);
        requestKeyPair.setOnClickListener(MainActivity.this);

        encryptButton = findViewById(R.id.buttonEncrypt);
        encryptButton.setOnClickListener(MainActivity.this);

        decryptButton = findViewById(R.id.buttonDecrypt);
        decryptButton.setOnClickListener(MainActivity.this);

        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
    }


    public void onClick(View v){
        switch (v.getId()){
            case R.id.buttonRequestKeyPair:
                requested = true;
                encryptButton.setEnabled(true);
                try {
                    Cursor cursor = getContentResolver()
                            .query(Uri.parse("content://com.drwitteck.wittecklab2encryption.encryptionprovider")
                                    , null, null, null, null);

                    assert cursor != null;
                    cursor.moveToNext();
                    cursor.close();

                    rsa.requestKeys(cursor);
                    Toast.makeText(this, "Public and private key generated.", Toast.LENGTH_SHORT).show();
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.buttonEncrypt:
                if(!requested){
                    Toast.makeText(this, "Please request keys!", Toast.LENGTH_SHORT).show();
                    break;
                }
                requestKeyPair.setEnabled(false);
                decryptButton.setEnabled(true);
                encryptButton.setEnabled(false);
                try {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        editText.setError("Please enter text to encrypt.");
                    } else {
                        byte[] encryptedTextReturned = rsa.encryptText(editText.getText().toString());
                        editText.setText(rsa.bytesToString(encryptedTextReturned));
                    }
                } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException |
                        NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.buttonDecrypt:
                requestKeyPair.setEnabled(true);
                encryptButton.setEnabled(true);
                try {
                   String decryptedTextReturned =  rsa.decryptText();
                   editText.setText(decryptedTextReturned);
                } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException |
                        BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

//    @Override
//    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
//        String message = editText.getText().toString();
//        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
//        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
//
//        return ndefMessage;
//    }
}
