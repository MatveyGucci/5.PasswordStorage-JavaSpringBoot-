package services;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import static sun.security.krb5.internal.crypto.Aes256.encrypt;

public class AEAD {

        static String plainText = "This is a plain text which need to be encrypted by Java AES 256 GCM Encryption Algorithm";
        public static final int AES_KEY_SIZE = 256;
        public static final int GCM_IV_LENGTH = 12;
        public static final int GCM_TAG_LENGTH = 16;


        public static byte[] encrypt(byte[] toEncrypt, SecretKey key, byte[] IV) throws Exception
        {
            // Get Cipher Instance
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }

            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

            // Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Encryption
            byte[] cipherText = cipher.doFinal(toEncrypt);

            return cipherText;
        }

        public static String decrypt(byte[] toDecrypt, SecretKey key, byte[] IV) throws Exception
        {
            // Get Cipher Instance
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

            // Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Decryption
            byte[] decryptedText = cipher.doFinal(toDecrypt);

            return new String(decryptedText);
        }
    }
