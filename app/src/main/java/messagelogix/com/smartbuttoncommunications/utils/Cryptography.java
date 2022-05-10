package messagelogix.com.smartbuttoncommunications.utils;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Vahid on 7/5/2016.
 */
public class Cryptography {

    //Tag
    private static final String TAG = Cryptography.class.getSimpleName();

    //Methods
    public static final String RSA = "RSA";

    public static final String AES = "AES";

    //RSA
    private Key publicKey = null;

    private Key privateKey = null;

    //AES
    SecretKeySpec sks = null;

    //Getters and setters
    public Key getPublicKey() {

        return publicKey;
    }

    public Key getPrivateKey() {

        return privateKey;
    }

    public SecretKeySpec getSks() {

        return sks;
    }

    public void setSks(SecretKeySpec sks) {

        this.sks = sks;
    }

    //General
    public Cryptography(String approach) {

        switch (approach) {
            case RSA:
                initializeRSA();
                break;
            case AES:
                initializeAES();
                break;
        }
    }

    //RSA
    public Cryptography(KeyPair kp) {

        this.publicKey = kp.getPublic();
        this.privateKey = kp.getPrivate();
    }

    public Cryptography(Key publicKey, Key privateKey) {

        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    //AES
    public Cryptography(SecretKeySpec sks) {

        this.publicKey = sks;
    }
    //Procedures

    //AES
    public SecretKeySpec initializeAES() {

        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
            return sks;
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
            return null;
        }
    }

    public byte[] encryptAES(byte[] input, SecretKeySpec key) {

        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            encodedBytes = c.doFinal(input);
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }
        return encodedBytes;
    }

    public byte[] decryptAES(byte[] input, SecretKeySpec key) {

        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            decodedBytes = c.doFinal(input);
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }
        return decodedBytes;
    }

    public byte[] encryptAES(byte[] input) {

        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(input);
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }
        return encodedBytes;
    }

    public byte[] decryptAES(byte[] input) {

        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(input);
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }
        return decodedBytes;
    }

    //RSA
    public boolean initializeRSA() {

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "RSA key pair error");
            return false;
        }
    }

    public byte[] encryptRSA(byte[] input, Key key) {

        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, key);
            encodedBytes = c.doFinal(input);
        } catch (Exception e) {
            Log.e(TAG, "RSA encryption error");
        }
        return encodedBytes;
    }

    public byte[] decryptRSA(byte[] input, Key key) {

        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, key);
            decodedBytes = c.doFinal(input);
        } catch (Exception e) {
            Log.e(TAG, "RSA decryption error");
        }
        return decodedBytes;
    }

    //General
    public String encodeToStringBase64(byte[] input) {

        return Base64.encodeToString(input, Base64.DEFAULT);
    }
}
