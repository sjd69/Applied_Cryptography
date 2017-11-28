import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.security.Security;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class MyClient {
    private static String serverFile = "ServerList.bin";
    private static ServerList serverList = null;
    private static Crypto crypto;
    private static GroupClient groupClient = new GroupClient();

    public static void main(String[] args) {
	 Security.addProvider(new BouncyCastleProvider());
        //FileClient fileClient = new FileClient();

        GroupServer groupServer = new GroupServer();
        String fs_ip = null;
        UserToken userToken = null;
        String username;
        PrivateKey privateKey = null;
        PublicKey serverPublicKey = null;
        KeySet sessionKey;
		KeyChain keychain = null;

        MyFileClient myFileClient = new MyFileClient();
        MyGroupClient myGroupClient = new MyGroupClient();
		MyFileCrypto myFileCrypto = new MyFileCrypto();

        Scanner scanIn = new Scanner(System.in);

        boolean exit = true;
        boolean auth = false;
        boolean validated = false;
        int nav = -1;

        do {
            while (!auth) {
                do {
                    System.out.println("MENU\n " +
                            "1: LOGIN\n " +
                            "2: EXIT");

                    while (!scanIn.hasNextInt()) {
                        System.out.println("MENU\n " +
                                "1: LOGIN\n " +
                                "2: EXIT");
                        scanIn.next();
                    }
                    nav = scanIn.nextInt();
                } while ((nav != 1 && nav != 2));

                if (nav == 1) {
                    exit = false;

                    System.out.println("Enter IP Address of Group Server.");
                    scanIn.nextLine();
                    String gs_ip = scanIn.nextLine();

                    System.out.println("Enter the port number for the Group Server");
                    int gs_port = scanIn.nextInt();
                    scanIn.nextLine();

                    System.out.println("Enter username");
                    username = scanIn.nextLine();

                    System.out.println("Enter your private key");
					String stringKey = scanIn.nextLine();
					byte[] byteKey = Base64.getDecoder().decode(stringKey);
					
                    try {
                        privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(byteKey));
                    	System.out.println("Key Successfully Generated ... ");
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        System.out.println(" --- Invalid Public Key! ---");
                        e.printStackTrace();
                    }

                    // connect to group client to authenticate
                    try {

                        groupClient.connect(gs_ip, gs_port);
                        serverPublicKey = groupClient.getPublicKey();
                        validated = validateServer(gs_ip, serverPublicKey);
                        String userResp;
                        if (!validated) {
                            System.out.println("\n\n********WARNING********");
                            System.out.println("Server " + gs_ip + " could not be validated");
                            System.out.println("\n\nThe server's fingerprint is "
                                    + crypto.get_MD5(serverPublicKey.getEncoded()));
                            System.out.println("Would you like to connect and store this server's information?");
                            System.out.print("(Yes/No): ");
                            userResp = scanIn.nextLine();

                            if (userResp.equalsIgnoreCase("yes")) {
                                serverList.addServer(gs_ip, serverPublicKey);
                                validated=true;
                            }
                        }
                        if (validated) {
                        	// - - - - Handshake with group server - - - -
                            sessionKey = handshake(username, serverPublicKey, privateKey);

                            if (sessionKey != null) {
                                userToken = groupClient.getToken(username);
                                if (userToken == null) {
                                    System.out.println("Authentication error.");
                                } else {
                                    auth = true;
                                    System.out.println("Authentication successful.");
                                }
                            }

                        } else {
                            System.out.println("Disconnecting...");
                        }

                        groupClient.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        System.out.println("Connection error.");
                        exit = true;
                        break;
                    }

                } else {
                    exit = true;
                    break;
                }
            }

            while (auth) {
                do {
                    System.out.println("MENU:\n" +
                            "1: Connect to File Server\n" +
                            "2: Connect to Group Server\n" +
                            "3: Encrypt/Decrypt File\n" +
                            "4: Logout\n" +
                            "5: Exit\n");

                    while (!scanIn.hasNextInt()) {
                        System.out.println("MENU:\n" +
                                "1: Connect to File Server\n" +
                                "2: Connect to Group Server\n" +
                                "3: Encrypt/Decrypt File\n" +
                            	"4: Logout\n" +
                            	"5: Exit\n");
                        scanIn.next();
                    }
                    nav = scanIn.nextInt();
                } while (!(nav > 0 && nav < 5));

                switch(nav) {
                    case 1:
                        System.out.println("Enter IP Address of File Server.");
                        scanIn.nextLine();
                        fs_ip = scanIn.nextLine();

                        System.out.println("Enter the port number for the File Server");
                        int fs_port = scanIn.nextInt();
                        myFileClient.startMyFileClient(fs_ip, fs_port, (Token)userToken);
                        scanIn.nextLine();
                        break;

                    case 2:
						System.out.println("Enter IP Address of Group Server.");
                        scanIn.nextLine();
                        String gc_ip = scanIn.nextLine();

                        System.out.println("Enter the port number for the Group Server");
                        int gc_port = scanIn.nextInt();
                        
                        System.out.println("Enter the Group Server's public key:");
                        String stringKey = scanIn.nextLine();
						byte[] byteKey = Base64.getDecoder().decode(stringKey);
						PublicKey gspublicKey = null;
						try {
							gspublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(byteKey));
						} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						
						System.out.println("Enter your private key:");
						String stringPKey = scanIn.nextLine();
						byte[] bytePKey = Base64.getDecoder().decode(stringPKey);
						privateKey = null;
						try {
							privateKey = KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(byteKey));
						} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						
						/*// Random Challenge
						SecureRandom sr = new SecureRandom();
						byte[] randbytes = new byte[20];
						sr.nextBytes(randbytes);
						
						byte[] byteEncSK = null;
						try {
							// Random Challenge Encrypted with Server's Public Key
							Cipher rsaCipherSig = Cipher.getInstance("RSA", "BC");
							rsaCipherSig.init(Cipher.ENCRYPT_MODE, gspublicKey);
							byte[] byteSignedRC = rsaCipherSig.doFinal(randbytes);
							
							// Session Key Encrypted with Server's Public Key 
							byte[] bsessionKey = sessionKey.getEncoded();
							byteEncSK = rsaCipherSig.doFinal(bsessionKey);
						} catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchProviderException| NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						
						try {
							// Encrypted Session Key Signed with User Private Key
							Cipher Signature = Cipher.getInstance("RSA", "BC");
							Signature.init(Cipher.ENCRYPT_MODE, privateKey);
							byte[] byteSignedSK = Signature.doFinal(byteEncSK);
						} catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchProviderException | InvalidKeyException | NoSuchAlgorithmException e) {
							e.printStackTrace();
						}*/
						
                        myGroupClient.startMyGroupClient(gc_ip, gc_port, userToken); 
                        scanIn.nextLine();
                        break;
                    
			
		     		// *** Connecting to MyFileCrypto ***
                    case 3:
						System.out.println("Enter group:");
						scanIn.nextLine();
						String group_name = scanIn.nextLine();
						keychain = groupClient.getKeyChain(group_name);
						System.out.print(keychain);
						if (keychain != null)
						{
							System.out.println("KeyChain for " + keychain.getGroup());
							myFileCrypto.startMyFileCrypto(userToken, keychain);
						}
						else
						{
							System.out.print("Problem getting keychain");
							myFileCrypto.startMyFileCrypto(userToken, keychain);
						}
						scanIn.nextLine();
                    	break; 
		    
		    		// *** Logging Out ***
		    		case 4:
                        System.out.println("Logging out");
                        auth = false;
                        break;
                    
					// *** Exit ***
                    case 5:
                        System.out.println("Exiting");
                        auth = false;
                        exit = true;
                        break;

                    default:
                        System.out.println("Invalid input.");
                        break;
                }

                if (exit) {
                    break;
                }
            }

        } while (!exit);

    }

    private static boolean validateServer(String ip, PublicKey serverKey) {
        try {
            if (serverList == null) {
                FileInputStream sfis = new FileInputStream(serverFile);
                ObjectInputStream serverStream = new ObjectInputStream(sfis);
                serverList = (ServerList)serverStream.readObject();
                serverStream.close();
            }
            PublicKey storedKey = serverList.getPublicKey(ip);
            if (Arrays.equals(crypto.get_MD5(serverKey.getEncoded()), crypto.get_MD5(storedKey.getEncoded()))) {
                return true;
            }
        } catch(Exception ex) {
            if (serverList == null) {
                serverList = new ServerList();
            }
            return false;
        }

        return false;
    }

    private static void updateServerList() {

        try {
            ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream("ServerList.bin"));
            outStream.writeObject(serverList);
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static KeySet handshake(String username, PublicKey serverPublicKey, PrivateKey privateKey) {
        try {

            // generate session keys for encryption and hashing
            KeySet sessionKeySet = crypto.getKeySet();
            SecretKey sessionKey = sessionKeySet.getKey();
           	IvParameterSpec iv = sessionKeySet.getIv();
			SecretKey hmackey = crypto.getHMACKey();

            //Signature rsaSignature = Signature.getInstance("RSA", "BC");
            byte[] signedSessionKey = crypto.rsaEncrypt(privateKey, sessionKey.getEncoded());
            System.out.println(signedSessionKey.length);
            byte[] encryptedKey = crypto.rsaEncrypt(serverPublicKey, sessionKey.getEncoded());
			

            BigInteger nonce1 = new BigInteger(256, new Random());
			byte[] encryptedNonce1 = crypto.rsaEncrypt(serverPublicKey, nonce1.toByteArray());
			
			// Server Handshake Response
            Envelope serverResponse = groupClient.firstHandshake(username, encryptedNonce1, encryptedKey, iv.getIV());

            BigInteger respNonce = (BigInteger)serverResponse.getObjContents().get(0);
            byte[] secondNonce = (byte[])serverResponse.getObjContents().get(1);

            if (respNonce.equals(nonce1)) {
            	System.out.println("Random Number Correct -  Group Server Authenticated");
                //byte[] decrypted = decrypt(sessionKey, secondNonce, "AES", "BC");
                byte[] decrypted = crypto.aesDecrypt(sessionKeySet, secondNonce);
                BigInteger decryptedNonce = new BigInteger(decrypted);

                if (groupClient.secondHandshake(decryptedNonce)) {
                    return sessionKeySet;
                }


            } else {
                return null;
            }

            return null;
        } catch (Exception ex) {
            System.out.println("Authentication error.");
            ex.printStackTrace();
            return null;
        }

    }

}
