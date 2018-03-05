package com.drwitteck.wittecklab2encryption;

import android.content.Context;
import android.database.Cursor;
import android.util.Base64;

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
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

class RSA {
    private final static String METHOD = "RSA";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    byte[] bytes;

    RSA(Context context) {

    }

    void requestKeys(Cursor cursor) throws InvalidKeySpecException, NoSuchAlgorithmException {
        /*
        Extract the public key from the cursor, decode the string, then re-encode using X509 standard
        to pass to encrypt function
         */
        String publicKeyString = cursor.getString(0);
        byte[] publicBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
        /*Implementation of this code was reviewed on StackOverflow*/
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
        publicKey = keyFactory.generatePublic(keySpec);

        /*
        Extract the private key from the cursor, decode the string, then re-encode using X509 standard
        to pass to decrypt function
         */
        String privateKeyString = cursor.getString(1);
        byte[] privateBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        /*Implementation of this code was reviewed on StackOverflow*/
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactoryPrivate = KeyFactory.getInstance(METHOD);
        privateKey = keyFactoryPrivate.generatePrivate(keySpecPrivate);
    }

    byte[] encryptText(String s) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
            /*
            Send the user entered string to be converted to bytes. Then send it and the public key
            to the encrypt method.
             */
            bytes = encrypt(publicKey, stringToBytes(s));

            return bytes;
    }

    String decryptText() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        try {
            return decrypt(privateKey, bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*Referenced from StackOverflow*/
    private byte[] encrypt(PublicKey key, byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(METHOD);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(text);
    }

    /*Referenced from StackOverflow*/
    private String decrypt(PrivateKey privateKey, byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        Cipher cipher = Cipher.getInstance(METHOD);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return bytesToString(cipher.doFinal(text));
    }

    private byte[] stringToBytes(String text){
        return text.getBytes();
    }

    String bytesToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }
}
