package com.drwitteck.wittecklab2encryption;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NfcAdapter.CreateNdefMessageCallback{

    EditText editText;
    Button encryptButton, decryptButton, requestKeyPair;
    String userEnteredText, publicKeyString, privateKeyString;
    Cursor cursor;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte[] bytes;
    boolean requested;
    RSA rsa;

    final static String METHOD = "RSA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rsa = new RSA(MainActivity.this);
        editText = findViewById(R.id.editTextToEncrypt);

        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            editText.setText("Sorry this device does not have NFC.");
            return;
        }

        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_LONG).show();
        }

        mAdapter.setNdefPushMessageCallback(this, MainActivity.this);

        editText = findViewById(R.id.editTextToEncrypt);
        userEnteredText = editText.getText().toString();

        requestKeyPair = findViewById(R.id.buttonRequestKeyPair);
        requestKeyPair.setOnClickListener(this);

        encryptButton = findViewById(R.id.buttonEncrypt);
        encryptButton.setOnClickListener(this);

        decryptButton = findViewById(R.id.buttonDecrypt);
        decryptButton.setOnClickListener(this);

        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.buttonRequestKeyPair:
                requested = true;
                try {
                    requestKeys();
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                encryptButton.setEnabled(true);
                break;

            case R.id.buttonEncrypt:
                if(!requested){
                    Toast.makeText(this, "Please request keys!", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    encryptText();
                } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException |
                        NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                requestKeyPair.setEnabled(false);
                decryptButton.setEnabled(true);
                encryptButton.setEnabled(false);
                break;

            case R.id.buttonDecrypt:
                try {
                    decryptText();
                } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException |
                        BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                encryptButton.setEnabled(true);
                requestKeyPair.setEnabled(true);
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

        /*
        Extract the public key from the cursor, decode the string, then re-encode using X509 standard
        to pass to encrypt function
         */
        publicKeyString = cursor.getString(0);
        byte[] publicBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
        /*Implementation of this code was reviewed on StackOverflow*/
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
        publicKey = keyFactory.generatePublic(keySpec);

        /*
        Extract the private key from the cursor, decode the string, then re-encode using X509 standard
        to pass to decrypt function
         */
        privateKeyString = cursor.getString(1);
        byte[] privateBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        /*Implementation of this code was reviewed on StackOverflow*/
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactoryPrivate = KeyFactory.getInstance(METHOD);
        privateKey = keyFactoryPrivate.generatePrivate(keySpecPrivate);

        decryptButton.setEnabled(false);

        Toast.makeText(this, "Keys requested and saved.", Toast.LENGTH_SHORT).show();
    }

    public void encryptText() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        if(TextUtils.isEmpty(editText.getText().toString())){
            editText.setError("Please enter text to encrypt.");
        } else {
            /*
            Send the user entered string to be converted to bytes. Then send it and the public key
            to the encrypt method.
             */
            bytes = rsa.encrypt(publicKey, rsa.stringToBytes(editText.getText().toString()));
            editText.setText(rsa.bytesToString(bytes));

            decryptButton.setEnabled(true);
        }
    }

    public void decryptText() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        try {
            editText.setText(rsa.decrypt(privateKey, bytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        decryptButton.setEnabled(false);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        String message = editText.getText().toString();
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());

        return new NdefMessage(ndefRecord);
    }
}
