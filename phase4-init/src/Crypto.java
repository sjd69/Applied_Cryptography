import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class Crypto {
    private final int aesSize = 128;
    private final int rsaSize = 2048;
    private SecureRandom rand;
    private KeyGenerator aesKeyGen;

    public Crypto() {
        Security.addProvider(new BouncyCastleProvider());
        rand = new SecureRandom();

        try {
            aesKeyGen = KeyGenerator.getInstance("AES", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

    }

    public KeySet getKeySet() {

        byte[] ivBytes = new byte[16];
        rand.nextBytes(ivBytes);

        aesKeyGen.init(aesSize);

        return new KeySet(aesKeyGen.generateKey(), new IvParameterSpec(ivBytes));
    }

    public byte[] aesEncrypt(KeySet keySet, byte[] bytes) {
        return aesCrypt(keySet, Cipher.ENCRYPT_MODE, bytes);
    }

    public byte[] aesDecrypt(KeySet keySet, byte[] bytes) {
        return aesCrypt(keySet, Cipher.DECRYPT_MODE, bytes);
    }

    private byte[] aesCrypt(KeySet keySet, int mode, byte[] bytes) {
        Key key = keySet.getKey();
        IvParameterSpec iv = keySet.getIv();

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
            cipher.init(mode, key, iv);
            return cipher.doFinal(bytes);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException |
                InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] rsaEncrypt(Key key, byte[] bytes) {
        try {
            return rsaCrypt(key, Cipher.ENCRYPT_MODE, bytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] rsaDecrypt(Key key, byte[] bytes) {
        try {
            return rsaCrypt(key, Cipher.DECRYPT_MODE, bytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] rsaCrypt(Key key, int mode, byte[] bytes) throws IllegalBlockSizeException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, key);
            return cipher.doFinal(bytes);
        } catch (NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}


