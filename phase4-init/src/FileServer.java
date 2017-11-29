/* FileServer loads files from FileList.bin.  Stores files in shared_files directory. */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class FileServer extends Server {
	
	public static final int SERVER_PORT = 4321;
	public static FileList fileList;
	public KeyChainList keychainList;
	protected PrivateKey privateKey;
	protected PublicKey publicKey;

	
	public FileServer() {
		super(SERVER_PORT, "FilePile");
	}

	public FileServer(int _port) {
		super(_port, "FilePile");
	}
	
	public void start() throws InvalidKeyException {
		Security.addProvider(new BouncyCastleProvider());
		
		String fileFile = "FileList.bin";
		String keyFile = "rsa.bin";
		Scanner console = new Scanner(System.in);
		ObjectInputStream fileStream;
		
		//This runs a thread that saves the lists on program exit
		Runtime runtime = Runtime.getRuntime();
		Thread catchExit = new Thread(new ShutDownListenerFS());
		runtime.addShutdownHook(catchExit);
		
		ObjectOutputStream outStream;
		
		//Open user file to get user list
		try
		{
			FileInputStream fis = new FileInputStream(fileFile);
			fileStream = new ObjectInputStream(fis);
			fileList = (FileList)fileStream.readObject();
			fileStream.close();
			
			FileInputStream kfis = new FileInputStream(keyFile);
			ObjectInputStream keyStream = new ObjectInputStream(kfis);
			KeyPair keyPair = (KeyPair)keyStream.readObject();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
			keyStream.close();
			
			try {
				outStream = new ObjectOutputStream(new FileOutputStream("FileList.bin"));
				outStream.writeObject(fileList);
				outStream.close();
				
				System.out.println("Created list.");
			}
			catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
			}
			
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("FileList Does Not Exist. Creating FileList...");
			
			fileList = new FileList();
			
			
			// Generate server keypair
			try {
				KeyPairGenerator rsaGenerator = KeyPairGenerator.getInstance("RSA", "BC");
				rsaGenerator.initialize(2048);
				KeyPair rsaKeys = rsaGenerator.generateKeyPair();
				privateKey = rsaKeys.getPrivate();
				publicKey = rsaKeys.getPublic();
				
				outStream = new ObjectOutputStream(new FileOutputStream("rsa.bin"));
				outStream.writeObject(rsaKeys);
				outStream.close();
				
				System.out.println("Keys generated.");
			}
			catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
			}
			System.out.println("Server running.");
		}
		catch(IOException e)
		{
			System.out.println("Error reading from FileList file");
			System.exit(-1);
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("Error reading from FileList file");
			System.exit(-1);
		}
		
		File file = new File("shared_files");
		if (file.mkdir()) {
			System.out.println("Created new shared_files directory");
		}
		else if (file.exists()){
			System.out.println("Found shared_files directory");
		}
		else {
			System.out.println("Error creating shared_files directory");				 
		 }
		
		//Autosave Daemon. Saves lists every 5 minutes
		AutoSaveFS aSave = new AutoSaveFS();
		aSave.setDaemon(true);
		aSave.start();
		
		
		boolean running = true;
		
		try
		{			
			final ServerSocket serverSock = new ServerSocket(port);
			System.out.printf("%s up and running\n", this.getClass().getName());
			
			Socket sock = null;
			Thread thread = null;
			
			while(running)
			{
				sock = serverSock.accept();
				thread = new FileThread(sock);
				thread.start();
			}
			
			System.out.printf("%s shut down\n", this.getClass().getName());
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

//This thread saves file list
class ShutDownListenerFS implements Runnable
{
	public void run()
	{
		System.out.println("Shutting down server");
		ObjectOutputStream outStream;

		try
		{
			outStream = new ObjectOutputStream(new FileOutputStream("FileList.bin"));
			outStream.writeObject(FileServer.fileList);
			outStream.close();
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

class AutoSaveFS extends Thread
{
	public void run()
	{
		do
		{
			try
			{
				Thread.sleep(300000); //Save file list every 5 minutes
				System.out.println("Autosave file list...");
				ObjectOutputStream outStream;
				try
				{
					outStream = new ObjectOutputStream(new FileOutputStream("FileList.bin"));
					outStream.writeObject(FileServer.fileList);
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
