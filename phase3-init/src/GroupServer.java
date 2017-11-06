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


public class GroupServer extends Server {

	public static final int SERVER_PORT = 8765;
	public UserList userList;
	public GroupList groupList;
    
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
		Scanner console = new Scanner(System.in);
		ObjectInputStream userStream;
		ObjectInputStream groupStream;
		
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
		}
		catch(FileNotFoundException e)
		{
			//Assume that if one file doesn't exist the other doesn't either.
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

			ObjectOutputStream outStream;
			try
			{
				outStream = new ObjectOutputStream(new FileOutputStream("UserList.bin"));
				outStream.writeObject(userList);
				outStream.close();

				outStream = new ObjectOutputStream(new FileOutputStream("GroupList.bin"));
				outStream.writeObject(groupList);
			}
			catch(Exception ex)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
			}
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
				thread.start();
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
				Thread.sleep(300000); //Save group and user lists every 5 minutes
				System.out.println("Autosave group and user lists...");
				ObjectOutputStream outStream;
				try
				{
					outStream = new ObjectOutputStream(new FileOutputStream("UserList.bin"));
					outStream.writeObject(my_gs.userList);
					outStream.close();

					outStream = new ObjectOutputStream(new FileOutputStream("GroupList.bin"));
					outStream.writeObject(my_gs.groupList);
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
