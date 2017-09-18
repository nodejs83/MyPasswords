package com.hfad.mypasswords;



/**
 * Created by a602256 on 08/09/2017.
 */

public class EncUtil {


    public static String encrypt(String strNormalText){
        String seedValue = "YourSecKey";
        String normalTextEnc="";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }
    public static String decrypt(String strEncryptedText){
        String seedValue = "YourSecKey";
        String strDecryptedText="";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    public static void main(String[] args){
       String data = "711983sec@@";
      String encrypted = encrypt(data);
       System.out.println(encrypted);
        System.out.println(decrypt(encrypted));
    }

}
