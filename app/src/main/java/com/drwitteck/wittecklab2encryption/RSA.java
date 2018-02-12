package com.drwitteck.wittecklab2encryption;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
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
    public byte[] encrypt(PublicKey key, byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(METHOD);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(text);
    }

    /*Referenced from StackOverflow*/
    public String decrypt(PrivateKey privateKey, byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        Cipher cipher = Cipher.getInstance(METHOD);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return bytesToString(cipher.doFinal(text));
    }

    public byte[] stringToBytes(String text){
        return text.getBytes();
    }

    public String bytesToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }
}
