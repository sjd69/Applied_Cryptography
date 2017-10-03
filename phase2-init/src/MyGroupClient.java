import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.io.ObjectInputStream;

public class MyGroupClient
{
	GroupClient groupclient = new GroupClient();
	UserToken utoken;
	public boolean startMyGroupClient(String server, int port, UserToken token)
	{
		boolean flag = true;
		boolean connected = groupclient.connect(server, port);
		utoken = token;
		System.out.println("Using MyGroupClient - Connected to Group Server");
		Scanner sc = new Scanner(System.in);
		// Get user token
		/*
		System.out.println("Enter username:");
		String user = sc.nextLine();
		utoken = groupclient.getToken(user);
		if (utoken != null)
		{

			System.out.println("Token - " + utoken.getSubject());
			System.out.println("Issued by" + utoken.getIssuer());
			System.out.println("Groups: ");
			List<String> groupnames = utoken.getGroups();
			for (String f:groupnames){
				System.out.println(f);
			}
		}
		else
		{
			System.out.println("Error getting token");
			return false;
		}*/
		
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
						System.out.println("(1) - Get token");
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
						break;
					
					// create user
					// boolean createUser(String username, UserToken token)
					case 2:
						Scanner scan = new Scanner(System.in);
						System.out.println("(2) - Create user");
						System.out.println("Enter username to be created:");
						String username = scan.nextLine();
					
						utoken = groupclient.getToken(username);
						if(groupclient.createUser(username, utoken)){
							System.out.println("User " + username + " created");
						}
						else{
							System.out.println("Error creating user");
						}
						break;
						
					// delete user
					// boolean deleteUser(String username, UserToken token)
					case 3:
						Scanner scan3 = new Scanner(System.in);
						System.out.println("(3) - Delete user");
						System.out.println("Enter username to be deleted:");
						String uname = scan3.nextLine();
					
						utoken = groupclient.getToken(uname);
						if(groupclient.deleteUser(uname, utoken)){
							System.out.println("User " + uname + " deleted");
						}
						else{
							System.out.println("Error deleting user");
						}
						break;
					
					// create group
					// boolean createGroup(String groupname, UserToken token)
					case 4:
						Scanner scan4 = new Scanner(System.in);
						System.out.println("(4) - Create group");
						System.out.println("Enter group to be created:");
						String g = scan4.nextLine();
					
						if(groupclient.createGroup(g, utoken)){
							System.out.println("Group " + g + " created");
						}
						else{
							System.out.println("Error creating group");
						}
						break;

					// delete group
					case 5:
						Scanner scan5 = new Scanner(System.in);
						System.out.println("(5) - Delete group");
						System.out.println("Enter group to be deleted:");
						String groupName = scan5.nextLine();
				
						if(groupclient.deleteGroup(groupName, utoken)){
							System.out.println("Group " + groupName + " deleted");
						}
						else{
							System.out.println("Error deleting group");
						}
						break;
						
					// add user to group
					// boolean addUserToGroup(String username, String groupname, UserToken token)
					case 6:
						Scanner scan6 = new Scanner(System.in);
						System.out.println("(6) - Add user to group");
						System.out.println("Enter username to be added:");
						String name = sc.nextLine();
						System.out.println("Enter group:");
						String grpname = scan6.nextLine();
						if (groupclient.addUserToGroup(name, grpname, utoken)){
							System.out.println("User " + name + " added to " + grpname);
						}
						else
						{
							System.out.println("Error adding user to group");
						}
						break;
						
					// delete user from group
					// boolean deleteUserFromGroup(String username, String groupname, UserToken token) 
					case 7:
						Scanner scan7 = new Scanner(System.in);
						System.out.println("(7) - Delete user from group");
						System.out.println("Enter username to be deleted:");
						String usename = sc.nextLine();
						System.out.println("Enter group:");
						String grp = scan7.nextLine();
						if (groupclient.addUserToGroup(usename, grp, utoken)){
							System.out.println("User " + usename + " deleted from " + grp);
						}
						else
						{
							System.out.println("Error deleting user from group");
						}
						break;
						
					// list members of group
					// List<String> listMembers(String group, UserToken token)
					case 8:
						Scanner scan8 = new Scanner(System.in);
						System.out.println("(8) - List members of group");
						System.out.println("Enter group:");
						String gname = scan8.nextLine();
						List<String> members = groupclient.listMembers(gname, utoken);
						for (String f:members){
							System.out.println(f);
						}
						break;
						
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
