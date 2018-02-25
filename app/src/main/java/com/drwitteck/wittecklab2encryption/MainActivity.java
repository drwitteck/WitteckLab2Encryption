package com.drwitteck.wittecklab2encryption;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback{

    NfcAdapter nfcAdapter;
    TextView textToSend;
    private static final int MESSAGE_SENT = 1;


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

        editText = findViewById(R.id.editTextToEncrypt);
        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not available on this device.", Toast.LENGTH_SHORT).show();
        } else {
            nfcAdapter.setNdefPushMessageCallback(this, MainActivity.this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, MainActivity.this);
        }


        rsa = new RSA(MainActivity.this);

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
        String text = editText.getText().toString();
        NdefMessage message = new NdefMessage(NdefRecord.createMime("text/plain", text.getBytes()));

        return message;
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }
    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }
    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        textToSend.setText(new String(msg.getRecords()[0].getPayload()));
    }
}
