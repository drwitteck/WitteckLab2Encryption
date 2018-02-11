package com.drwitteck.wittecklab2encryption;

import android.util.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {
    private final static String METHOD = "RSA";

    public static String encrypt(String text, String publicKey){
        String encryptedBase64 = "";
        try{
            KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFactory.generatePublic(keySpec);

            final Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e){
            e.printStackTrace();
        }

        return encryptedBase64;
    }

    public static String decrypt(String encryptedBase64, String privateKey){
        String decryptedString = "";
        try{
            KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFactory.generatePublic(keySpec);

            final Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e){
            e.printStackTrace();
        }

        return decryptedString;
    }
}
