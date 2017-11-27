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
					
					Path file_path = Paths.get(".", sourcefile);
					byte[] encryptedFile = null;
					try 
					{
						encryptedFile = Files.readAllBytes(file_path);
					} catch (IOException e) 
					{
						System.out.println(e);
					}
					
					// Add encryption key ind to beginning of encrypted file
					String f = new String(encryptedFile);
					f = ind + "~" + f;
					
					// back to bytes
					encryptedFile = f.getBytes();
					
					// encryption
					byte[] decryptBytes = crypto.aesEncrypt(dkey, encryptedFile);
					
					// write encrypted file
					Path path = Paths.get(".", destfile);
					try {
						Files.write(path, decryptBytes);
					} catch (IOException e) 
					{
						System.out.println(e);
					}
					break;
			
				// decrypt a file
				case 2:
					System.out.println("-- Decrypt File --");
					System.out.println("Enter source file: ");
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
					
					// Get correct key
					KeySet ekey = keychain.getDecryptionKey(i);
					
					// Decryption
					decryptBytes = crypto.aesDecrypt(ekey, encryptedFile);
					
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
