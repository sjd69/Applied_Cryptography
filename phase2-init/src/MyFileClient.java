import java.util.*;

public class MyFileClient
{
	FileClient fileclient = new FileClient();
	
	public boolean startMyFileClient(String server, int port, Token token)
	{
		boolean flag = true;
		while (flag){
			if (fileclient.connect(server, port))
			{
				Scanner sc = new Scanner(System.in);
			
				System.out.println("Using MyFileClient - " + token.getSubject() + " Connected to File Server");
				System.out.println("Enter command number:");
				System.out.println("(1) - List files for " + token.getSubject());
				System.out.println("(2) - Upload file");
				System.out.println("(3) - Download file");
				System.out.println("(4) - Delete file");
				System.out.println("(5) - Disconnect from File Server");
			
				int commandNum = sc.nextInt();
			
				switch (commandNum) 
				{
				// list files
				case 1: 
					List<String> filelist;
					filelist = fileclient.listFiles(token);
					System.out.println("(1) File List");
					for (String f:filelist){
						System.out.println(f);
					}
					break;
				// upload file
				case 2:  
					System.out.println("(2) Upload File");
					System.out.println("Enter source file: ");
					String sourcefile = sc.nextLine();
				
					System.out.println("Enter destination file: ");
					String destfile = sc.nextLine();
				
					System.out.println("Enter group that file will belong to: ");
					String grp = sc.nextLine();
					fileclient.upload(sourcefile, destfile, grp, token);
					break;
				// download file
				case 3:
					System.out.println("(3) Download File");
					System.out.println("Enter source file: ");
					String sFile = sc.nextLine();
				
					System.out.println("Enter destination file: ");
					String dFile = sc.nextLine();

					fileclient.download(sFile, dFile, token);
					break;
				// delete file
				case 4:
					System.out.println("(4) Delete File");
					System.out.println("Enter name of file to be deleted: ");
					String filename = sc.nextLine();
					fileclient.delete(filename, token);
					break;
				// disconnect from server
				case 5:
					System.out.println("(5) Disconnecting From File Server");
					fileclient.disconnect();
					flag = false;
					break;
				default: System.out.println("Invalid Command Number");
					break;
				}
			
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