/*
 * MIT License
 *
 * Copyright (c) 2016 Aldrin Clemente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.truebanana.crypto;

import com.truebanana.log.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Includes a comprehensive set of utility methods for your cryptography needs.
 * <br />
 * <br />The default {@link Crypto.Spec} is cross-compatible with the iOS (Swift) Crypto for convenient cross-platform data security.
 * <br />
 * <br />Encrypting or decrypting with a {@link Crypto.Spec} will use the following format:
 * <br />Salt|HMAC Salt|IV|AES256-encrypted Data|HMAC
 */
public class Crypto {
    public enum Mode {
        ENCRYPT, DECRYPT
    }

    // Encryption / Decryption using custom format [Salt|HMAC Salt|IV|AES128-encrypted Data|HMAC]
    // ************************************************************************

    public static class Spec {
        private EncryptionAlgorithm algorithm = EncryptionAlgorithm.AES256;
        private BlockCipherMode blockCipherMode = BlockCipherMode.CBC;
        private Padding padding = Padding.PKCS7;
        private int saltLength = 16;
        private int hmacSaltLength = 16;
        private int hmacKeyLength = 16;
        private int keyDerivationIterations = 10000;
        private PRFAlgorithm prfAlgorithm = PRFAlgorithm.HMAC_SHA1;
        private MACAlgorithm macAlgorithm = MACAlgorithm.HMAC_SHA256;

        public EncryptionAlgorithm getAlgorithm() {
            return algorithm;
        }

        public BlockCipherMode getBlockCipherMode() {
            return blockCipherMode;
        }

        public Padding getPadding() {
            return padding;
        }

        public int getSaltLength() {
            return saltLength;
        }

        public int getHmacSaltLength() {
            return hmacSaltLength;
        }

        public int getHmacKeyLength() {
            return hmacKeyLength;
        }

        public int getKeyDerivationIterations() {
            return keyDerivationIterations;
        }

        public PRFAlgorithm getPrfAlgorithm() {
            return prfAlgorithm;
        }

        public MACAlgorithm getMacAlgorithm() {
            return macAlgorithm;
        }

        public Spec setAlgorithm(EncryptionAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Spec setBlockCipherMode(BlockCipherMode blockCipherMode) {
            this.blockCipherMode = blockCipherMode;
            return this;
        }

        public Spec setPadding(Padding padding) {
            this.padding = padding;
            return this;
        }

        public Spec setSaltLength(int saltLength) {
            this.saltLength = saltLength;
            return this;
        }

        public Spec setHmacSaltLength(int hmacSaltLength) {
            this.hmacSaltLength = hmacSaltLength;
            return this;
        }

        public Spec setHmacKeyLength(int hmacKeyLength) {
            this.hmacKeyLength = hmacKeyLength;
            return this;
        }

        public Spec setKeyDerivationIterations(int keyDerivationIterations) {
            this.keyDerivationIterations = keyDerivationIterations;
            return this;
        }

        public Spec setPrfAlgorithm(PRFAlgorithm prfAlgorithm) {
            this.prfAlgorithm = prfAlgorithm;
            return this;
        }

        public Spec setMacAlgorithm(MACAlgorithm macAlgorithm) {
            this.macAlgorithm = macAlgorithm;
            return this;
        }
    }

    public static byte[] encrypt(byte[] data, String password) {
        return encrypt(data, password, new Crypto.Spec());
    }

    public static byte[] encrypt(byte[] data, String password, Spec spec) {
        byte[] salt = generateSecureRandomBytes(spec.getSaltLength());
        byte[] key = PBKDF2(password, salt, spec.getKeyDerivationIterations(), spec.getAlgorithm().getMinKeySize(), spec.getPrfAlgorithm());
        byte[] iv = generateSecureRandomBytes(spec.getAlgorithm().getBlockSize());
        byte[] encryptedData = encrypt(data, key, iv, spec.getAlgorithm(), spec.getBlockCipherMode(), spec.getPadding());

        byte[] hmacSalt = generateSecureRandomBytes(spec.getHmacSaltLength());
        byte[] hmacKey = PBKDF2(password, hmacSalt, spec.getKeyDerivationIterations(), spec.getHmacKeyLength(), spec.getPrfAlgorithm());
        byte[] hmac = HMAC(hmacKey, encryptedData, spec.getMacAlgorithm());

        return combineByteArrays(salt, hmacSalt, iv, encryptedData, hmac);
    }

    public static byte[] decrypt(byte[] data, String password) {
        return decrypt(data, password, new Crypto.Spec());
    }

    public static byte[] decrypt(byte[] data, String password, Spec spec) {
        int saltLength = spec.getSaltLength();
        int hmacSaltLength = spec.getHmacSaltLength();
        int ivLength = spec.getAlgorithm().getBlockSize();
        int hmacLength = spec.getMacAlgorithm().getMacLength();
        int encryptedDataLength = data.length - saltLength - hmacSaltLength - ivLength - hmacLength;

        int saltIndex = 0;
        int hmacSaltIndex = saltIndex + saltLength;
        int ivIndex = hmacSaltIndex + hmacSaltLength;
        int encryptedDataIndex = ivIndex + ivLength;
        int hmacIndex = encryptedDataIndex + encryptedDataLength;

        byte[] salt = Arrays.copyOfRange(data, saltIndex, saltIndex + saltLength);
        byte[] hmacSalt = Arrays.copyOfRange(data, hmacSaltIndex, hmacSaltIndex + hmacSaltLength);
        byte[] iv = Arrays.copyOfRange(data, ivIndex, ivIndex + ivLength);
        byte[] hmac = Arrays.copyOfRange(data, hmacIndex, hmacIndex + hmacLength);
        byte[] encryptedData = Arrays.copyOfRange(data, encryptedDataIndex, encryptedDataIndex + encryptedDataLength);

        byte[] hmacKey = PBKDF2(password, hmacSalt, spec.getKeyDerivationIterations(), spec.getHmacKeyLength(), spec.getPrfAlgorithm());

        if (!Arrays.equals(hmac, HMAC(hmacKey, encryptedData, spec.getMacAlgorithm()))) { // Verify integrity
            return null;
        }

        byte[] key = PBKDF2(password, salt, spec.getKeyDerivationIterations(), spec.getAlgorithm().getMinKeySize(), spec.getPrfAlgorithm());

        return decrypt(encryptedData, key, iv, spec.getAlgorithm(), spec.getBlockCipherMode(), spec.getPadding());
    }

    // Cipher
    // ************************************************************************

    public enum EncryptionAlgorithm {
        AES128("AES", 16, 16, 16),
        AES192("AES", 16, 24, 24),
        AES256("AES", 16, 32, 32),
        DES("DES", 8, 8, 8),
        TRIPLE_DES("DESede", 8, 24, 24);

        private String name;
        private int blockSize;
        private int minKeySize;
        private int maxKeySize;

        EncryptionAlgorithm(String name, int blockSize, int minKeySize, int maxKeySize) {
            this.name = name;
            this.blockSize = blockSize;
            this.minKeySize = minKeySize;
            this.maxKeySize = maxKeySize;
        }

        public String getName() {
            return name;
        }

        public int getBlockSize() {
            return blockSize;
        }

        public int getMinKeySize() {
            return minKeySize;
        }

        public int getMaxKeySize() {
            return maxKeySize;
        }
    }

    public enum BlockCipherMode {
        ECB("ECB"),
        CBC("CBC");

        private String name;

        BlockCipherMode(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum Padding {
        NONE("NoPadding"),
        PKCS5("PKCS5Padding"),
        PKCS7("PKCS7Padding");

        private String name;

        Padding(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum CipherTransformation {
        AES_CBC_NoPadding("AES", "CBC", "NoPadding"),
        AES_CBC_PKCS5Padding("AES", "CBC", "PKCS5Padding"),
        AES_CBC_PKCS7Padding("AES", "CBC", "PKCS7Padding"),
        AES_ECB_NoPadding("AES", "ECB", "NoPadding"),
        AES_ECB_PKCS5Padding("AES", "ECB", "PKCS5Padding"),
        AES_ECB_PKCS7Padding("AES", "ECB", "PKCS7Padding"),

        DES_CBC_NoPadding("DES", "CBC", "NoPadding"),
        DES_CBC_PKCS5Padding("DES", "CBC", "PKCS5Padding"),
        DES_CBC_PKCS7Padding("DES", "CBC", "PKCS7Padding"),
        DES_ECB_NoPadding("DES", "ECB", "NoPadding"),
        DES_ECB_PKCS5Padding("DES", "ECB", "PKCS5Padding"),
        DES_ECB_PKCS7Padding("DES", "ECB", "PKCS7Padding"),

        DESede_CBC_NoPadding("DESede", "CBC", "NoPadding"),
        DESede_CBC_PKCS5Padding("DESede", "CBC", "PKCS5Padding"),
        DESede_CBC_PKCS7Padding("DESede", "CBC", "PKCS7Padding"),
        DESede_ECB_NoPadding("DESede", "ECB", "NoPadding"),
        DESede_ECB_PKCS5Padding("DESede", "ECB", "PKCS5Padding"),
        DESede_ECB_PKCS7Padding("DESede", "ECB", "PKCS7Padding"),

        RSA_ECB_PKCS1Padding("RSA", "ECB", "PKCS1Padding"),
        RSA_ECB_OAEPWithSHA1AndMGF1Padding("RSA", "ECB", "OAEPWithSHA-1AndMGF1Padding"),
        RSA_ECB_OAEPWithSHA256AndMGF1Padding("RSA", "ECB", "OAEPWithSHA-256AndMGF1Padding");

        private String name, algorithm, blockCipherMode, padding;

        CipherTransformation(String algorithm, String blockCipherMode, String padding) {
            this.algorithm = algorithm;
            this.blockCipherMode = blockCipherMode;
            this.padding = padding;
            this.name = algorithm + "/" + blockCipherMode + "/" + padding;
        }

        public String getName() {
            return this.name;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public String getBlockCipherMode() {
            return blockCipherMode;
        }

        public String getPadding() {
            return padding;
        }

        public static CipherTransformation getCipherTransformation(EncryptionAlgorithm algorithm, BlockCipherMode blockCipherMode, Padding padding) {
            String transformation = algorithm.getName() + "/" + blockCipherMode.getName() + "/" + padding.getName();
            for (CipherTransformation ct : CipherTransformation.values()) {
                if (ct.getName().equals(transformation)) {
                    return ct;
                }
            }
            return null;
        }
    }

    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv, EncryptionAlgorithm algorithm, BlockCipherMode blockCipherMode, Padding padding) {
        return crypt(data, key, iv, CipherTransformation.getCipherTransformation(algorithm, blockCipherMode, padding), Mode.ENCRYPT);
    }

    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv, EncryptionAlgorithm algorithm, BlockCipherMode blockCipherMode, Padding padding) {
        return crypt(data, key, iv, CipherTransformation.getCipherTransformation(algorithm, blockCipherMode, padding), Mode.DECRYPT);
    }

    private static byte[] crypt(byte[] data, byte[] key, byte[] iv, CipherTransformation transformation, Mode mode) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, transformation.getAlgorithm());
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(transformation.getName());
            cipher.init(mode == Mode.ENCRYPT ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            Log.d("crypt", e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.d("crypt", e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Log.d("crypt", e.getMessage());
        } catch (BadPaddingException e) {
            Log.d("crypt", e.getMessage());
        } catch (InvalidKeyException e) {
            Log.d("crypt", e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            Log.d("crypt", e.getMessage());
        }
        return null;
    }

    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv, CipherTransformation transformation) {
        return crypt(data, key, iv, transformation, Mode.ENCRYPT);
    }

    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv, CipherTransformation transformation) {
        return crypt(data, key, iv, transformation, Mode.DECRYPT);
    }

    // PKCS7
    // ************************************************************************

    public static byte[] PKCS7(byte[] data, int length) {
        if (length > 255) {
            throw new IllegalArgumentException("Length must less than 255");
        }

        byte padding = (byte) (length - data.length);

        byte[] paddedData = new byte[length];
        for (int i = 0; i < length; i++) {
            if (i < data.length) {
                paddedData[i] = data[i];
            } else {
                paddedData[i] = padding;
            }
        }

        return paddedData;
    }

    // MAC
    // ************************************************************************

    public enum MACAlgorithm {
        HMAC_MD5("HmacMD5", 32),
        HMAC_SHA1("HmacSHA1", 40),
        HMAC_SHA256("HmacSHA256", 32),
        HMAC_SHA384("HmacSHA384", 48),
        HMAC_SHA512("HmacSHA512", 64),
        PBE_HMAC_MD5("PBEWithHmacMD5", 32),
        PBE_HMAC_SHA1("PBEWithHmacSHA1", 40),
        PBE_HMAC_SHA256("PBEWithHmacSHA256", 32),
        PBE_HMAC_SHA384("PBEWithHmacSHA384", 48),
        PBE_HMAC_SHA512("PBEWithHmacSHA512", 64);

        private String name;
        private int macLength;

        MACAlgorithm(String name, int macLength) {
            this.name = name;
            this.macLength = macLength;
        }

        public String getName() {
            return this.name;
        }

        public int getMacLength() {
            return macLength;
        }
    }

    public static byte[] HMAC(byte[] key, byte[] message, MACAlgorithm algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm.getName());
            SecretKeySpec ks = new SecretKeySpec(key, algorithm.getName());
            mac.init(ks);

            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException e) {
            Log.d("HMAC", e.getMessage());
        } catch (InvalidKeyException e) {
            Log.d("HMAC", e.getMessage());
        }
        return null;
    }

    // PBKDF2
    // ************************************************************************

    public enum PRFAlgorithm {
        HMAC_SHA1("PBKDF2WithHmacSHA1");

        private String name;

        PRFAlgorithm() {
            this.name = name();
        }

        PRFAlgorithm(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static byte[] PBKDF2(String password, byte[] salt, int iterations, int length, PRFAlgorithm algorithm) {
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm.getName());
            KeySpec ks = new PBEKeySpec(password.toCharArray(), salt, iterations, length * 8);
            return f.generateSecret(ks).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            Log.d("PBKDF2", e.getMessage());
        } catch (InvalidKeySpecException e) {
            Log.d("PBKDF2", e.getMessage());
        }
        return null;
    }

    // MessageDigest
    // ************************************************************************

    public enum MessageDigestAlgorithm {
        MD2,
        MD5,
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512");

        private String name;

        MessageDigestAlgorithm() {
            this.name = name();
        }

        MessageDigestAlgorithm(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static String hash(String text, MessageDigestAlgorithm algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm.getName());
            md.update(text.getBytes("UTF-8"));
            return toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            Log.d(algorithm.getName(), e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.d(algorithm.getName(), e.getMessage());
        }
        return null;
    }

    public static String SHA1(String text) {
        return hash(text, MessageDigestAlgorithm.SHA1);
    }

    public static String MD5(String text) {
        return hash(text, MessageDigestAlgorithm.MD5);
    }

    // SecureRandom
    // ************************************************************************

    public enum SecureRandomAlgorithm {
        SHA1PRNG;

        private String name;

        SecureRandomAlgorithm() {
            this.name = name();
        }

        SecureRandomAlgorithm(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static byte[] generateSecureRandomBytes(int length, SecureRandomAlgorithm algorithm) {
        byte[] bytes = new byte[length];
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(algorithm.getName());
            secureRandom.nextBytes(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] generateSecureRandomBytes(int length) {
        return generateSecureRandomBytes(length, SecureRandomAlgorithm.SHA1PRNG);
    }

    // Utilities
    // ************************************************************************

    public static byte[] combineByteArrays(byte[]... byteArrays) {
        int length = 0;
        for (byte[] data : byteArrays) {
            length += data.length;
        }

        byte[] combinedData = new byte[length];

        int processedBytes = 0;
        for (byte[] data : byteArrays) {
            System.arraycopy(data, 0, combinedData, processedBytes, data.length);
            processedBytes += data.length;
        }

        return combinedData;
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            int h = (b >>> 4) & 0x0F;
            int h2 = 0;
            do {
                sb.append((0 <= h) && (h <= 9) ? (char) ('0' + h) : (char) ('a' + (h - 10)));
                h = b & 0x0F;
            } while (h2++ < 1);
        }
        return sb.toString();
    }
}