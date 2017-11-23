import java.util.*;


public class MyFileCrypto
{
	UserToken utoken;
	
	// TODO take in keychain as argument
	public void startMyFileCrypto(Token token)
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
				
					// TODO add encryption here
				
					break;
			
				// decrypt a file
				case 3:
					System.out.println("-- Decrypt File --");
					System.out.println("Enter source file: ");
					String sFile = sc.nextLine();
		
					System.out.println("Enter destination file: ");
					String dFile = sc.nextLine();

					// TODO add decryption here
					break;
				
			// disconnect from server
			case 5:
				connected = false;
				break;
			
			default: System.out.println("Invalid Command Number");
					break;
			}
			System.out.println();
		}
	}
}