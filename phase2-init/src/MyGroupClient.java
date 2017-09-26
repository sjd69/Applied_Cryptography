
public class MyGroupClient
{
	GroupClient gorupclient = new GroupClient();
	
	public boolean startMyGroupClient(String server, int port)
	{
		if (fileclient.connect(server, port))
		{
			// get token
			// create user
			// delete user
			// create group
			// delete group
			// add user to group
			// delete user from group
			// list members of group
		}
		else
		{
			System.out.println("Error connecting to GroupClient");
		}
	}
	
}