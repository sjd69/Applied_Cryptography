import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.util.Random;

public class Crypto {
    private final int aesSize = 128;
    private final int rsaSize = 2048;
    private SecureRandom rand;
    private KeyGenerator aesKeyGen, md5KeyGen;

    public Crypto() {
        Security.addProvider(new BouncyCastleProvider());
        rand = new SecureRandom();

        try {
            aesKeyGen = KeyGenerator.getInstance("AES", "BC");
            md5KeyGen = KeyGenerator.getInstance("HmacMD5","BC");
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
    
    public SecretKey getHMACKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }
        String str = salt.toString();
    	return new SecretKeySpec(chars.getBytes(), "HmacMD5");
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
	return null;
    }

    public byte[] rsaSign(byte[] text, PrivateKey privateKey) {
        Signature signature;
        byte[] signedBytes = new byte[0];
        try {
            signature = Signature.getInstance("SHA256withRSA", "BC");
            signature.initSign(privateKey);
            signature.update(text);
            signedBytes = signature.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return signedBytes;
    }

    public boolean rsaVerify(byte[] text, byte[] signedBytes, PublicKey publicKey) {
        Signature signature;
        boolean verified = false;
        try {
            signature = Signature.getInstance("SHA256withRSA", "BC");
            signature.initVerify(publicKey);
            signature.update(text);
            verified = signature.verify(signedBytes);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return verified;
    }
    
    // return HMAC of a byte array message using a SHA256 key
    public byte[] get_HMAC(Key key, byte[] m){
    	byte[] hash = null;
    	try {
    		Mac mac = Mac.getInstance("HmacMD5", "BC");
    		mac.init(key);
    		hash = mac.doFinal(m);
    	}
    	catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return hash;
	}
	
	// Return MD5 of a public key.
	public byte[] get_MD5(byte[] m) {
		byte[] hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5", new BouncyCastleProvider());
			md.update(m);
			hash = md.digest();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}
	
	// --------------------------------- HASH INVERSION PUZZLE -------------------------------------
	// Generate Hash Puzzle Answer of 15 bytes -- for use by server
    public byte[] generatePuzzleAnswer() {
        byte[] b = new byte[2];
        new Random().nextBytes(b);
        return b;
    }
	
    // Generate the hash of the puzzle answer to send to client -- for use by server
    public byte[] generatePuzzle(byte[] answer) {
        byte[] puzzle = get_MD5(answer);
        return puzzle;
    }
    
    // Check that the answer provided by the server is correct -- for use by server and client
    public boolean verifyPuzzle(byte[] correctAns, byte[] clientAns){
        if (Arrays.equals(correctAns, clientAns))
        {
            return true;
        }
        return false;
    }
    
    // brute force check hash values to find match -- for use by client
    public byte[] solvePuzzle(byte[] hash) {
        for (int i = 0; i < 32767; i++){
            BigInteger bi = BigInteger.valueOf(i);
            byte[] bytes = bi.toByteArray();
            byte[] testhash = get_MD5(bytes);
            if (verifyPuzzle(hash, testhash)){
                return testhash;
            }
        }
        return null;
    }
	
}
