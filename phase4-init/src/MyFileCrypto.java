import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class MyFileCrypto
{
	UserToken utoken;
	Crypto crypto = new Crypto();
	
	// TODO take in keychain as argument
	public void startMyFileCrypto(UserToken token, KeyChain keychain)
	{
		boolean connected = true;
		System.out.println("Using File Encryption/Decryption System");
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter command number:");
		System.out.println("(1) - Encrypt a file");
		System.out.println("(2) - Decrypt a file");
		System.out.println("(3) - Exit");
	
		int commandNum = sc.nextInt();
		sc.nextLine();
				
		while (connected){
			switch (commandNum) 
			{
				// encrypt a file
				case 1:
					System.out.println("-- Encrypt File --");
					System.out.println("Enter source file: ");
					String sourcefile = sc.nextLine();
		
					System.out.println("Enter destination file: ");
					String destfile = sc.nextLine();
				
					KeySet dkey = keychain.getEncryptionKey();			
					int ind = keychain.getEncryptionKeyInd();
					System.out.println(ind);
					
					Path file_path = Paths.get(".", sourcefile);
					byte[] decFile = null;
					try 
					{
						decFile = Files.readAllBytes(file_path);
					} catch (IOException e) 
					{
						System.out.println("Error Finding File");
						System.out.println(e);
					}
					
					// encryption
					byte[] encryptedBytes = crypto.aesEncrypt(dkey, decFile);
					
					// Add encryption key ind to beginning of encrypted file
					String f = new String(encryptedBytes);
					f = ind + "~" + f;
					
					// back to bytes
					byte[] encryptedFile = f.getBytes();
					
					// write encrypted file
					Path path = Paths.get(".", destfile);
					try {
						Files.write(path, encryptedFile);
					} catch (IOException e) 
					{
						System.out.println(e);
					}
					break;
			
				// decrypt a file
				case 2:
					System.out.println("-- Decrypt File --");
					System.out.println("Enter encrypted file: ");
					String sFile = sc.nextLine();
		
					System.out.println("Enter destination file: ");
					String dFile = sc.nextLine();

					// get file contents
					Path f_path = Paths.get(".", sFile);
					encryptedFile = null;
					try 
					{
						encryptedFile = Files.readAllBytes(f_path);
					} catch (IOException e) 
					{
						System.out.println(e);
					}
					
					// Get decrypt key index 
					f = new String(encryptedFile);
					String index = f.split("\\~")[0];
					
					int i = Integer.parseInt(index);
					System.out.println(i);
					
					System.out.println(f.split("\\~").length);
					String encString = f.split("\\~")[1];
					System.out.println(encString);
					byte[] encBytes = encString.getBytes();
					
					// Get correct key
					KeySet ekey = keychain.getDecryptionKey(i);
					
					// Decryption
					byte[] decryptBytes = crypto.aesDecrypt(ekey, encBytes);
					
					// write decrypted file
					Path p = Paths.get(".", dFile);
					try {
						Files.write(p, decryptBytes);
					} catch (IOException e) 
					{
						System.out.println(e);
					}
					
					break;
				
			// disconnect from server
			case 3:
				connected = false;
				break;
			
			default: System.out.println("Invalid Command Number");
					break;
			}
			System.out.println();
		}
	}
	
	public static void main (String args[]){
		
	}

}
