
public class MyFileClient
{
	FileClient fileclient = new FileClient();
	
	public boolean startMyFileClient(String server, int port, Token toke)
	{
		if (fileclient.connect(server, port))
		{
			//list files
			// upload file
			// download file
			// delete file
		}
		else
		{
			System.out.println("Error connecting to FileClient");
		}
	}
	
}