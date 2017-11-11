import java.util.*;

public class MyFileClient
{
	FileClient fileclient = new FileClient();
	UserToken utoken;
	
	public boolean startMyFileClient(String server, int port, Token token)
	{
		boolean flag = true;
		boolean connected = fileclient.connect(server, port);
		utoken = token;
		System.out.println("Using MyFileClient - " + token.getSubject() + " Connected to File Server");
		Scanner sc = new Scanner(System.in);
		
		while (flag){
			if (connected)
			{
				System.out.println("Enter command number:");
				System.out.println("(1) - List files for " + token.getSubject());
				System.out.println("(2) - Upload file");
				System.out.println("(3) - Download file");
				System.out.println("(4) - Delete file");
				System.out.println("(5) - Disconnect from File Server");
			
				int commandNum = sc.nextInt();
				sc.nextLine();
				//System.out.println();
				
				// list files
				switch (commandNum) 
				{
				case 1:
					List<String> filelist;
					filelist = fileclient.listFiles(utoken);
					System.out.println(" -- File List -- ");
					if (filelist != null)
					{
						for (String f:filelist)
						{
							System.out.println(f);
						}
						System.out.println("User has files");
					}
					else 
					{
						System.out.println("No files for " + token.getSubject());
					}
					break;
				
				// upload file
				// boolean upload(String sourceFile, String destFile, String group, UserToken token)
				case 2:
					System.out.println("-- Upload File --");
					System.out.println("Enter source file: ");
					String sourcefile = sc.nextLine();
				
					System.out.println("Enter destination file: ");
					String destfile = sc.nextLine();
				
					System.out.println("Enter group that file will belong to: ");
					String grp = sc.nextLine();
					fileclient.upload(sourcefile, destfile, grp, utoken);
					break;
					
				// download file
				case 3:
					System.out.println("-- Download File --");
					System.out.println("Enter source file: ");
					String sFile = sc.nextLine();
				
					System.out.println("Enter destination file: ");
					String dFile = sc.nextLine();

					fileclient.download(sFile, dFile, utoken);
					break;
				
				// delete file
				case 4:
					System.out.println("-- Delete File --");
					System.out.println("Enter name of file to be deleted: ");
					String filename = sc.nextLine();
					fileclient.delete(filename, utoken);
					break;

				// disconnect from server
				case 5:
					System.out.println("-- Disconnecting From File Server --");
					fileclient.disconnect();
					connected = false;
					flag = false;
					break;
					
				default: System.out.println("Invalid Command Number");
						break;
				}
				System.out.println();
			
			}
			else
			{
				System.out.println("Error connecting to FileClient");
				return false;
			}
		}
		return true;
	}	
}
