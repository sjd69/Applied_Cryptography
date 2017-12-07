import java.io.StringWriter;
import java.security.*;
import java.util.Scanner;
import java.math.BigInteger;
import java.security.KeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import java.util.Random;
import java.security.KeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.*;
import java.io.*;

public class Attack 
{
	public static void main(String[] args) 
	{
	 	Security.addProvider(new BouncyCastleProvider());
	 	GroupServer groupServer = new GroupServer();
	 	GroupClient groupClient = new GroupClient();
        String fs_ip = null;
        String gs_ip = null;
        int gs_port = 8765;
        String username;
        ServerList serverList = null;
        Crypto crypto = new Crypto();
    
        
        Scanner scanIn = new Scanner(System.in);
        
        System.out.println("Enter IP Address of Group Server.");
		gs_ip = scanIn.nextLine();
		scanIn.nextLine();

		System.out.println("Enter the port number for the Group Server");
		gs_port = scanIn.nextInt();
		scanIn.nextLine();

		System.out.println("Enter username");
		username = scanIn.nextLine();
		
		while (true)
		{
			System.out.println("New handshake");
			groupClient.connect(gs_ip, gs_port);
		
			try {
				KeyPairGenerator rsaGenerator = KeyPairGenerator.getInstance("RSA", "BC");
				rsaGenerator.initialize(2048);
				KeyPair SrsaKeys = rsaGenerator.generateKeyPair();
				PublicKey serverPublicKey = SrsaKeys.getPublic();
				KeyPair rsaKeys = rsaGenerator.generateKeyPair();
				PrivateKey rsaPrivate = rsaKeys.getPrivate();
				attackHandshake(username, serverPublicKey, rsaPrivate, gs_ip);
			}
			catch (Exception e) {
            	e.printStackTrace();
        	}
			
		}
	 }
	 
	 
	     private static KeySet attackHandshake(String username, PublicKey serverPublicKey, PrivateKey privateKey, String ip) 
	     {
        	try {
				GroupClient groupClient = new GroupClient();
				ServerList serverList = null;
				Crypto crypto = new Crypto();
				// generate session keys for encryption and hashing
				KeySet sessionKeySet = crypto.getKeySet();
				SecretKey sessionKey = sessionKeySet.getKey();
				IvParameterSpec iv = sessionKeySet.getIv();
				SecretKey hmacKey = crypto.getHMACKey();

				byte[] signedSessionKey = crypto.rsaEncrypt(privateKey, sessionKey.getEncoded());

				byte[] encryptedKey = crypto.rsaEncrypt(serverPublicKey, sessionKey.getEncoded());

				byte[] encryptedHmacKey = crypto.rsaEncrypt(serverPublicKey, hmacKey.getEncoded());

				BigInteger nonce1 = new BigInteger(256, new Random());
				byte[] encryptedNonce1 = crypto.rsaEncrypt(serverPublicKey, nonce1.toByteArray());
			
				// Server Handshake Response
				//groupClient.firstHandshake(username, encryptedNonce1, encryptedKey,
						//iv.getIV(), encryptedHmacKey, serverList, ip);
						
				Envelope message = null, response = null;
				message = new Envelope("HANDSHAKE");
				message.addObject(username);
				message.addObject(nonce1);
				message.addObject(encryptedKey);
				message.addObject(iv.getIV());
				message.addObject(encryptedHmacKey);
				//finalizeMessage(message, true);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(message);

				return null;
        } catch (Exception ex) {
            System.out.println("Error.");
            ex.printStackTrace();
            return null;
        }
    }
}