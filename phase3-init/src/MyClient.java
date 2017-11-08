import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.security.Security;
import java.security.*;
import javax.crypto.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class MyClient {
    private static String serverFile = "ServerList.bin";
    private static ServerList serverList = null;
    private static MessageDigest md;

    public static void main(String[] args) {
	 Security.addProvider(new BouncyCastleProvider());
        //FileClient fileClient = new FileClient();
        GroupClient groupClient = new GroupClient();
        GroupServer groupServer = new GroupServer();
        UserToken userToken = null;
        String username;
        PrivateKey privateKey = null;
        PublicKey serverPublicKey = null;

        MyFileClient myFileClient = new MyFileClient();
        MyGroupClient myGroupClient = new MyGroupClient();



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

                    System.out.println("Enter your private key key");
                    String stringKey = scanIn.nextLine();
                    byte[] byteKey = Base64.getDecoder().decode(stringKey);
                    try {
                        privateKey = KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(byteKey));
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    //Connect to group client to auth
                    // Used this for the port. I think this should work. 
                    // It's 8765 just in case this is not right.
                    try {

                        groupClient.connect(gs_ip, gs_port);
                        serverPublicKey = groupClient.getPublicKey();
                        validated = validate_server(gs_ip, serverPublicKey);
                        String userResp = null;
                        if (!validated) {
                            System.out.println("\n\n********WARNING********");
                            System.out.println("Server " + gs_ip + "Could not be validated");
                            System.out.println("\n\nThe server's fingerprint is "
                                    + Arrays.toString(md.digest(serverPublicKey.getEncoded())));
                            System.out.println("Would you like to connect and store this server's information?");
                            System.out.print("(Yes/No): ");
                            userResp = scanIn.nextLine();

                            if (userResp.equalsIgnoreCase("yes")) {
                                serverList.addServer(gs_ip, serverPublicKey);
                            }
                        }
                        if (validated) {
                            userToken = groupClient.getToken(username, privateKey);

                            if (userToken == null) {
                                System.out.println("Authentication error.");
                            } else {
                                auth = true;
                                System.out.println("Authentication successful.");
                            }
                        } else {
                            System.out.println("Disconnecting...");
                        }

                        groupClient.disconnect();
                    } catch (Exception e) {
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
                            "3: Logout\n" +
                            "4: Exit\n");

                    while (!scanIn.hasNextInt()) {
                        System.out.println("MENU:\n" +
                                "1: Connect to File Server\n" +
                                "2: Connect to Group Server\n" +
                                "3: Logout\n" +
                                "4: Exit\n");
                        scanIn.next();
                    }
                    nav = scanIn.nextInt();
                } while (!(nav > 0 && nav < 5));

                switch(nav) {
                    case 1:
                    	// generate session key for file server
						System.out.println("AES session key generation file server");
						try {
							KeyGenerator keyGenAES = KeyGenerator.getInstance("AES");
							keyGenAES.init(128);
							SecretKey sessionKey = keyGenAES.generateKey();
							//System.out.println(sessionKey);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
                        System.out.println("Enter IP Address of File Server.");
                        scanIn.nextLine();
                        String fs_ip = scanIn.nextLine();

                        System.out.println("Enter the port number for the File Server");
                        int fs_port = scanIn.nextInt();
                        myFileClient.startMyFileClient(fs_ip, fs_port, (Token)userToken);
                        break;

                    case 2:
                    	// generate session key for group server
                    	                    	// generate session key for group server
                    	SecretKey sessionKey = null;
						System.out.println("AES session key generation group server");
						try {
							KeyGenerator keyGenAES = KeyGenerator.getInstance("AES");
							keyGenAES.init(128);
							sessionKey = keyGenAES.generateKey();
							//System.out.println(sessionKey);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
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
						
						// Random Challenge
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
						} 
						catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchProviderException| NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						
						try {
						// Encrypted Session Key Signed with User Private Key
							Cipher Signature = Cipher.getInstance("RSA", "BC");
							Signature.init(Cipher.ENCRYPT_MODE, privateKey);
							byte[] byteSignedSK = Signature.doFinal(byteEncSK);
						} 
						catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchProviderException | InvalidKeyException | NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
					
						// ------ handshake/auth here ---------------------------
						
                        myGroupClient.startMyGroupClient(gc_ip, gc_port, userToken); 
                        break;
                    case 3:
                        System.out.println("Logging out");
                        auth = false;
                        break;

                    case 4:
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

    private static boolean validate_server(String ip, PublicKey serverKey) {
        try {
            if (serverList == null) {
                FileInputStream sfis = new FileInputStream(serverFile);
                ObjectInputStream serverStream = new ObjectInputStream(sfis);
                serverList = (ServerList)serverStream.readObject();
                serverStream.close();
            }
            md = MessageDigest.getInstance("MD5");
            PublicKey storedKey = serverList.getPublicKey(ip);
            if (Arrays.equals(md.digest(serverKey.getEncoded()), md.digest(storedKey.getEncoded()))) {
                return true;
            }
        } catch(Exception ex) {
            return false;
        }

        return false;
    }
}
