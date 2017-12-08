/* Group server. Server loads the users from UserList.bin.
 * If user list does not exists, it creates a new list and makes the user the server administrator.
 * On exit, the server saves the user list to file. 
 */

/*
 * TODO: This file will need to be modified to save state related to
 *       groups that are created in the system
 *
 */

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class GroupServer extends Server {

	public static final int SERVER_PORT = 8765;
	public UserList userList;
	public GroupList groupList;
	public KeyChainList keychainList;
	protected PrivateKey privateKey;
	protected  PublicKey publicKey;
	protected KeySet puzzleKeySet;
    
	public GroupServer() {
		super(SERVER_PORT, "ALPHA");
	}
	
	public GroupServer(int _port) {
		super(_port, "ALPHA");
	}
	
	public void start() throws InvalidKeyException {
		// Overwrote server.start() because if no user file exists, initial admin account needs to be created
		Security.addProvider(new BouncyCastleProvider());

		String userFile = "UserList.bin";
		String groupFile = "GroupList.bin";
		String keyFile = "rsa.bin";
		String keychainFile = "KeyChainList.bin";
		String puzzleFile = "PuzzleKey.bin";
		Scanner console = new Scanner(System.in);
		ObjectInputStream userStream;
		ObjectInputStream groupStream;
		ObjectInputStream keyChainStream;
		ObjectInputStream puzzleStream;
		
		//This runs a thread that saves the lists on program exit
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ShutDownListener(this));
		
		//Open user file to get user list
		try
		{
			//Going to close the userstream before we open the groupstream just in case.
			FileInputStream ufis = new FileInputStream(userFile);
			userStream = new ObjectInputStream(ufis);
			userList = (UserList)userStream.readObject();
			userStream.close();

			FileInputStream gfis = new FileInputStream(groupFile);
			groupStream = new ObjectInputStream(gfis);
			groupList = (GroupList)groupStream.readObject();
			groupStream.close();
			
			FileInputStream kcfis = new FileInputStream(keychainFile);
			keyChainStream = new ObjectInputStream(kcfis);
			keychainList = (KeyChainList)keyChainStream.readObject();
			keyChainStream.close();

			FileInputStream kfis = new FileInputStream(keyFile);
			ObjectInputStream keyStream = new ObjectInputStream(kfis);
			KeyPair keyPair = (KeyPair)keyStream.readObject();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
			keyStream.close();

			FileInputStream pkfis = new FileInputStream(puzzleFile);
			puzzleStream = new ObjectInputStream(pkfis);
			puzzleKeySet = (KeySet)puzzleStream.readObject();
			puzzleStream.close();

		}
		catch(FileNotFoundException e)
		{
			//Assume that if one file doesn't exist the other doesn't either.
			//Must also generate rsa keypair
			System.out.println("UserList File Does Not Exist. Creating UserList...");
			System.out.println("No users currently exist. Your account will be the administrator.");
			System.out.print("Enter your username: ");
			String username = console.nextLine();

			PublicKey adminKey = null;

			try {
				System.out.print("What is your public key?: ");
				String stringKey = console.nextLine();
				byte[] byteKey = Base64.getDecoder().decode(stringKey);
				adminKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(byteKey));
			} catch (InvalidKeySpecException | NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}

			if (adminKey == null) {
				throw new InvalidKeyException();
			}
			//Create a new list, add current user to the ADMIN group. They now own the ADMIN group.
			userList = new UserList();
			userList.addUser(username, adminKey);
			userList.addGroup(username, "ADMIN");
			userList.addOwnership(username, "ADMIN");

			//Create new group list. Defaults to ADMIN group.
			groupList = new GroupList();
			groupList.addGroup("ADMIN");
			groupList.addOwnership(username, "ADMIN");
			groupList.addUser(username, "ADMIN");
			
			Crypto crypto = new Crypto();
			// Create new KeyChainList
			keychainList = new KeyChainList();
			// generate a new group key for file crypto
			KeySet groupKey = crypto.getKeySet();
			// create a new keychain
			KeyChain kchain = new KeyChain("ADMIN");
			// add new group key to keychain
			kchain.addNewKey(groupKey);
			// update keychainList
			keychainList.addKeyChain("ADMIN", kchain);
			System.out.println(keychainList.getKeyChain("ADMIN"));
			

			ObjectOutputStream outStream;
			try
			{
				outStream = new ObjectOutputStream(new FileOutputStream("UserList.bin"));
				outStream.writeObject(userList);
				outStream.close();

				outStream = new ObjectOutputStream(new FileOutputStream("GroupList.bin"));
				outStream.writeObject(groupList);
				outStream.close();
				
				outStream = new ObjectOutputStream(new FileOutputStream("KeyChainList.bin"));
				outStream.writeObject(keychainList);
				outStream.close();

				System.out.println("Created lists.");
			}
			catch(Exception ex)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
			}

			//Generate server keypair
			try {
				puzzleKeySet = crypto.getKeySet();

				outStream = new ObjectOutputStream(new FileOutputStream("PuzzleKey.bin"));
				outStream.writeObject(puzzleKeySet);
				outStream.close();

				KeyPairGenerator rsaGenerator = KeyPairGenerator.getInstance("RSA", "BC");
				rsaGenerator.initialize(2048);
				KeyPair rsaKeys = rsaGenerator.generateKeyPair();
				privateKey = rsaKeys.getPrivate();
				publicKey = rsaKeys.getPublic();

				outStream = new ObjectOutputStream(new FileOutputStream("rsa.bin"));
				outStream.writeObject(rsaKeys);
				outStream.close();

				System.out.println("Keys generated");

			} catch(Exception ex) {
				System.err.println("Error: " + ex.getMessage());
				ex.printStackTrace(System.err);
			}
			System.out.println("Server running.");
		}
		catch(IOException e)
		{
			System.out.println("Error reading from files");
			System.exit(-1);
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("Error reading from files");
			System.exit(-1);
		}
		
		//Autosave Daemon. Saves lists every 5 minutes
		AutoSave aSave = new AutoSave(this);
		aSave.setDaemon(true);
		aSave.start();
		
		//This block listens for connections and creates threads on new connections
		try
		{
			
			final ServerSocket serverSock = new ServerSocket(port);
			
			Socket sock = null;
			GroupThread thread = null;
			
			while(true)
			{
				sock = serverSock.accept();
				thread = new GroupThread(sock, this);
				thread.run();
			}
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}

	}
	
}

//This thread saves the user list
class ShutDownListener extends Thread
{
	public GroupServer my_gs;
	
	public ShutDownListener (GroupServer _gs) {
		my_gs = _gs;
	}
	
	public void run()
	{
		System.out.println("Shutting down server");
		ObjectOutputStream outStream;
		try
		{
			outStream = new ObjectOutputStream(new FileOutputStream("UserList.bin"));
			outStream.writeObject(my_gs.userList);
			outStream.close();

			outStream = new ObjectOutputStream(new FileOutputStream("GroupList.bin"));
			outStream.writeObject(my_gs.groupList);
			outStream.close();
			
			outStream = new ObjectOutputStream(new FileOutputStream("KeyChainList.bin"));
			outStream.writeObject(my_gs.keychainList);
			outStream.close();
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

class AutoSave extends Thread
{
	public GroupServer my_gs;
	
	public AutoSave (GroupServer _gs) {
		my_gs = _gs;
	}
	
	public void run()
	{
		do
		{
			try
			{
				Thread.sleep(150000); //Save group and user lists every 5 minutes
				System.out.println("Autosave group, keychain, and user lists...");
				ObjectOutputStream outStream;
				try
				{
					outStream = new ObjectOutputStream(new FileOutputStream("UserList.bin"));
					outStream.writeObject(my_gs.userList);
					outStream.close();

					outStream = new ObjectOutputStream(new FileOutputStream("GroupList.bin"));
					outStream.writeObject(my_gs.groupList);
					outStream.close();
					
					outStream = new ObjectOutputStream(new FileOutputStream("KeyChainList.bin"));
					outStream.writeObject(my_gs.keychainList);
					outStream.close();

					outStream = new ObjectOutputStream(new FileOutputStream("PuzzleKey.bin"));
					outStream.writeObject(my_gs.puzzleKeySet);
					outStream.close();
				}
				catch(Exception e)
				{
					System.err.println("Error: " + e.getMessage());
					e.printStackTrace(System.err);
				}
			}
			catch(Exception e)
			{
				System.out.println("Autosave Interrupted");
			}
		}while(true);
	}
}
