import java.util.*;

public class MyFileClient
{
	FileClient fileclient = new FileClient();
	
	public boolean startMyFileClient(String server, int port, Token token)
	{
		boolean flag = true;
		boolean connected = fileclient.connect(server, port);
		if (connected)
		{
			System.out.println("Using MyFileClient - " + token.getSubject() + " Connected to File Server");
			while (flag){
			
				Scanner sc = new Scanner(System.in);
				System.out.println("Enter command number:");
				System.out.println("(1) - List files for " + token.getSubject());
				System.out.println("(2) - Upload file");
				System.out.println("(3) - Download file");
				System.out.println("(4) - Delete file");
				System.out.println("(5) - Disconnect from File Server");
			
				int commandNum = sc.nextInt();
				sc.nextLine();
				System.out.println();
				
				// list files
				if (commandNum == 1) {
					List<String> filelist;
					filelist = fileclient.listFiles(token);
					System.out.println(" -- File List -- ");
					if (filelist != null){
						for (String f:filelist){
							System.out.println(f);
						}
					}
					else {
						System.out.println("No files for " + token.getSubject());
					}
				}
				// upload file
				else if (commandNum == 2) {
					System.out.println("-- Upload File --");
					System.out.println("Enter source file: ");
					String sourcefile = sc.nextLine();
				
					System.out.println("Enter destination file: ");
					String destfile = sc.nextLine();
				
					System.out.println("Enter group that file will belong to: ");
					String grp = sc.nextLine();
					fileclient.upload(sourcefile, destfile, grp, token);
				}
				// download file
				else if (commandNum == 3) {
					System.out.println("-- Download File --");
					System.out.println("Enter source file: ");
					String sFile = sc.nextLine();
				
					System.out.println("Enter destination file: ");
					String dFile = sc.nextLine();

					fileclient.download(sFile, dFile, token);
				}
				// delete file
				else if (commandNum == 4) {
					System.out.println("-- Delete File --");
					System.out.println("Enter name of file to be deleted: ");
					String filename = sc.nextLine();
					fileclient.delete(filename, token);
				}
				// disconnect from server
				else if (commandNum == 5) {
					System.out.println("-- Disconnecting From File Server --");
					fileclient.disconnect();
					connected = false;
					flag = false;
				}
				else {
					System.out.println("Invalid Command Number");
				}
				System.out.println();
			
			}
		}
		else
		{
			System.out.println("Error connecting to FileClient");
			return false;
		}
		return true;
	}	
}