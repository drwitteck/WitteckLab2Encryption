package com.drwitteck.wittecklab2encryption;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EncryptionProvider extends ContentProvider {
    KeyPairGenerator keyPairGenerator;
    KeyPair keyPair;
    PublicKey publicKey;
    PrivateKey privateKey;
    String encrypted, decrypted;

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
        throw new UnsupportedOperationException("Not yet implemented");
//        MatrixCursor matrixCursor = new MatrixCursor(new String[]{privateKey, publicKey});
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException{
        keyPairGenerator = KeyPairGenerator.getInstance(METHOD);
        keyPairGenerator.initialize(BITS);
//        keyPair = keyPairGenerator.generateKeyPair();
//        publicKey = keyPair.getPublic();
//        privateKey = keyPair.getPrivate();
        return keyPairGenerator.genKeyPair();
    }

}
