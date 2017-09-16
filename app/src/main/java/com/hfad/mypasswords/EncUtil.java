package com.hfad.mypasswords;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;


/**
 * Created by a602256 on 08/09/2017.
 */

public class EncUtil {




    public static SecretKeySpec getSecretKeySpec(){
        // Set up secret key spec for 128-bit AES encryption and decryption
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
           e.printStackTrace();
        }
        return sks;
    }

    public static String encrypt(String data, SecretKeySpec  sks){
        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT) ;
    }


    public static String decrypt(String data, SecretKeySpec  sks) {
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String(decodedBytes);
    }

}
