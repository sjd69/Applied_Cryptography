import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.io.ObjectInputStream;
>>>>>>> 42ad126ec29f7b7f9a194de96785311cd72248cb

public class MyGroupClient
{
	GroupClient groupclient = new GroupClient();
	UserToken utoken;

	public boolean startMyGroupClient(String server, int port)
	{
		boolean flag = true;
		boolean connected = groupclient.connect(server, port);
		System.out.println("Using MyGroupClient - Connected to Group Server");

		// Get user token
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter username:");
		String username = sc.nextLine();
		utoken = groupclient.getToken(username);
		if (utoken != null)
		{
			System.out.println("Token - " + utoken.getSubject());
			System.out.println("Issued by" + utoken.getIssuer());
			System.out.println("Groups: ");
			List<String> groupnames = utoken.getGroups();
			for (String f:groupnames){
				System.out.println(f);
			}
			// get token
			try {
				Token userToken = null;
				Envelope message = null, response = null;

				// Tell the server to return a token
				message = new Envelope("GET");
				message.addObject(username); // add username String
				output.writeObject(message);

				// Get the response from the server
				response = (Envelope)input.readObject();

				// Successful response
				if(response.getMessage().equals("OK")) {
					// If there is a token in the envelope, return it
					ArrayList<Object> temp = null;
					temp = response.getObjContents();

					if (temp.size() == 1) {
						userToken = (Token)temp.get(0);
						return userToken;
					}
				}
				return null;
			}
			catch(Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return null;
			}

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
			System.out.println("Error getting token");
			return false;
		}

		while (flag){
			if (connected)
			{
				System.out.println("Enter command number:");
				System.out.println("(1) - Get token");
				System.out.println("(2) - Create user");
				System.out.println("(3) - Delete user");
				System.out.println("(4) - Create group");
				System.out.println("(5) - Delete group");
				System.out.println("(6) - Add user to group");
				System.out.println("(7) - Delete user from group");
				System.out.println("(8) - List members of group");
				System.out.println("(9) - Disconnect from Group Server");

				int commandNum = sc.nextInt();

				switch (commandNum)
				{
					// get token
					// UserToken getToken(String username)
					case 1:
						System.out.println("Enter username:");
						String usern = sc.nextLine();
						utoken = groupclient.getToken(usern);
						if (utoken != null)
						{
							System.out.println("Token - " + utoken.getSubject());
							System.out.println("Issued by" + utoken.getIssuer());
							System.out.println("Groups: ");
							List<String> groups = utoken.getGroups();
							for (String f:groups){
								System.out.println(f);
							}
						}
						else
						{
							System.out.println("Error getting token");
						}

					// create user
					// boolean createUser(String username, UserToken token)
					case 2:
						System.out.println("(2) - Create user");
						System.out.println("Enter username to be created:");
						username = sc.nextLine();

						utoken = groupclient.getToken(username);
						if(groupclient.createUser(username, utoken)){
							System.out.println("User " + username + " created");
						}
						else{
							System.out.println("Error creating user");
						}

					// delete user
					// boolean deleteUser(String username, UserToken token)
					case 3:
						System.out.println("(3) - Delete user");
						System.out.println("Enter username to be deleted:");
						String uname = sc.nextLine();

						utoken = groupclient.getToken(uname);
						if(groupclient.deleteUser(uname, utoken)){
							System.out.println("User " + uname + " deleted");
						}
						else{
							System.out.println("Error deleting user");
						}

					// create group
					// boolean createGroup(String groupname, UserToken token)
					case 4:
						System.out.println("(4) - Create group");
						System.out.println("Enter group to be created:");
						String g = sc.nextLine();

						if(groupclient.createGroup(g, utoken)){
							System.out.println("Group " + g + " created");
						}
						else{
							System.out.println("Error creating group");
						}
					// delete group
					case 5:
						System.out.println("(5) - Delete group");
						System.out.println("Enter group to be deleted:");
						String groupName = sc.nextLine();

						if(groupclient.deleteGroup(groupName, utoken)){
							System.out.println("Group " + groupName + " deleted");
						}
						else{
							System.out.println("Error deleting group");
						}
					// add user to group
					// boolean addUserToGroup(String username, String groupname, UserToken token)
					case 6:
						System.out.println("(6) - Add user to group");
						System.out.println("Enter username to be added:");
						String name = sc.nextLine();
						System.out.println("Enter group:");
						String grpname = sc.nextLine();
						if (groupclient.addUserToGroup(name, grpname, utoken)){
							System.out.println("User " + name + " added to " + grpname);
						}
						else
						{
							System.out.println("Error adding user to group");
						}
					// delete user from group
					// boolean deleteUserFromGroup(String username, String groupname, UserToken token)
					case 7:
						System.out.println("(7) - Delete user from group");
						System.out.println("Enter username to be deleted:");
						String usename = sc.nextLine();
						System.out.println("Enter group:");
						String grp = sc.nextLine();
						if (groupclient.addUserToGroup(usename, grp, utoken)){
							System.out.println("User " + usename + " deleted from " + grp);
						}
						else
						{
							System.out.println("Error deleting user from group");
						}
					// list members of group
					// List<String> listMembers(String group, UserToken token)
					case 8:
						System.out.println("(8) - List members of group");
						System.out.println("Enter group:");
						String gname = sc.nextLine();
						List<String> members = groupclient.listMembers(gname, utoken);
						for (String f:members){
							System.out.println(f);
						}
					// disconnect from server
					case 9:
						System.out.println("(8) - Disconnect from Group Server");
						groupclient.disconnect();
						flag = false;
						break;
					default: System.out.println("Invalid Command Number");
						break;
			}
			}
			else
			{
				System.out.println("Error connecting to GroupClient");
				flag = false;
				return false;
			}
		}
		return true;
	}
}
	}

}
