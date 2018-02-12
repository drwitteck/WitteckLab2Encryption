package com.drwitteck.wittecklab2encryption;

import android.util.Base64;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
    private final static String METHOD = "RSA";

    /*Referenced from StackOverflow*/
    public byte[] encrypt(final PublicKey key, byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(text);
    }

    /*Referenced from StackOverflow*/
    public String decrypt(PrivateKey privateKey, byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        return bytesToString(cipher1.doFinal(text));
    }

    /*Referenced from StackOverflow*/
    public  String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    /*Referenced from StackOverflow*/
    public  byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }

    /*Referenced from StackOverflow*/
    public PublicKey publicKeyFromString(String keyAsString)throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(keyAsString, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /*Referenced from StackOverflow*/
    public PrivateKey privateKeyFromString(String keyAsString)throws NoSuchAlgorithmException, InvalidKeySpecException{
        byte[] publicBytes = Base64.decode(keyAsString, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


















//    public String encrypt(String text, String publicKey){
//        String encryptedBase64 = "";
//        try{
//            KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
//            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
//            Key key = keyFactory.generatePublic(keySpec);
//
//            final Cipher cipher = Cipher.getInstance(METHOD);
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//
//            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
//            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return encryptedBase64;
//    }
//
//    public static String decrypt(String encryptedBase64, String privateKey){
//        String decryptedString = "";
//        try{
//            KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
//            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
//            Key key = keyFactory.generatePublic(keySpec);
//
//            final Cipher cipher = Cipher.getInstance(METHOD);
//            cipher.init(Cipher.DECRYPT_MODE, key);
//
//            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
//            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
//            decryptedString = new String(decryptedBytes);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return decryptedString;
//    }
}
