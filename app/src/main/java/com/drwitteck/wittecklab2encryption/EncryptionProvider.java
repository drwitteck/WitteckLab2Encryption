package com.drwitteck.wittecklab2encryption;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EncryptionProvider extends ContentProvider {
    KeyPairGenerator keyPairGenerator;
    KeyPair keyPair;
    String publicKeyString, privateKeyString;

    private final static String METHOD = "RSA";
    private final static int BITS = 1024;

    public EncryptionProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        try {
            keyPair = generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        PublicKey publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        publicKeyString = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);

        PrivateKey privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        privateKeyString = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"Public", "Private"});

        matrixCursor.addRow(new String[]{publicKeyString, privateKeyString});

        return matrixCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPair kp = null;
        try{
            keyPairGenerator = KeyPairGenerator.getInstance(METHOD);
            keyPairGenerator.initialize(BITS);
            kp = keyPairGenerator.generateKeyPair();
        } catch (Exception e){
            e.printStackTrace();
        }

        return kp;
    }
}
