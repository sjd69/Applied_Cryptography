import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.io.ObjectInputStream;

public class MyGroupClient
{
	GroupClient groupclient = new GroupClient();
	UserToken utoken;
	public boolean startMyGroupClient(String server, int port, UserToken token, int messageNumber)
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
			System.out.println("Issued by - " + utoken.getIssuer());
			System.out.println("For server - " + utoken.getServerId());
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
				System.out.println("(9) - Get Keychain");
                System.out.println("(10) - Add File Server IP to token");
				System.out.println("(11) - Disconnect from Group Server");
		
				int commandNum = sc.nextInt();
				sc.nextLine();
				
				switch (commandNum) 
				{
					// get token
					// UserToken getToken(String username)
					case 1: 
						System.out.println("-- Get token --");
						if (utoken != null)
						{
							System.out.println("Token - " + utoken.getSubject());
							System.out.println("Issued by - " + utoken.getIssuer());
							//System.out.println("For server - " + utoken.getServerId());
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

						if (token.getGroups().contains("ADMIN")) {
							System.out.println("-- Create user --");
							System.out.println("Enter username to be created:");
							String username = sc.nextLine();

							System.out.println("Enter the user's public key");
							String stringKey = sc.nextLine();
							byte[] byteKey = Base64.getDecoder().decode(stringKey);
							PublicKey publicKey = null;
							try {
								publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(byteKey));
							} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
								e.printStackTrace();
							}

							if(groupclient.createUser(username, token, publicKey)){
								System.out.println("User " + username + " created");
							}
							else{
								System.out.println("Error creating user");
							}
						} else {
							System.out.println("Insufficient privileges.");
						}
						break;
						
					// delete user
					// boolean deleteUser(String username, UserToken token)
					case 3:
						if (token.getGroups().contains("ADMIN")) {
							System.out.println("(3) - Delete user");
							System.out.println("Enter username to be deleted:");
							String uname = sc.nextLine();
					
							if(groupclient.deleteUser(uname, utoken)){
								System.out.println("User " + uname + " deleted");
							}
							else{
								System.out.println("Error deleting user");
							}
						}
						else{
							System.out.println("Insufficient privileges.");
						}
						break;
					
					// create group
					// boolean createGroup(String groupname, UserToken token)
					case 4:
						System.out.println("-- Create group --");
						System.out.println("Enter group to be created:");
						String g = sc.nextLine();
					
						if(groupclient.createGroup(g, utoken)){
							System.out.println("Group " + g + " created");
						}
						else{
							System.out.println("Error creating group");
						}
						break;

					// delete group
					case 5:
						System.out.println("-- Delete group --");
						System.out.println("Enter group to be deleted:");
						String groupName = sc.nextLine();
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
						System.out.println("-- Add user to group --");
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
						break;
						
					// delete user from group
					// boolean deleteUserFromGroup(String username, String groupname, UserToken token) 
					case 7:
						System.out.println("-- Delete user from group --");
						System.out.println("Enter username to be deleted:");
						String usename = sc.nextLine();
						
						System.out.println("Enter group:");
						String grp = sc.nextLine();
						
						if (groupclient.deleteUserFromGroup(usename, grp, utoken)){
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
						System.out.println("--  List members of group --");
						System.out.println("Enter group:");
						String gname = sc.nextLine();
						List<String> members = groupclient.listMembers(gname, utoken);
						if (members != null) {
							for (String f:members){
								System.out.println(f);
							}
						} else {
							System.out.println("Group does not exist.");
						}

						break;
						
					// disconnect from server 
					case 9:
						System.out.println("--  Get group keychain --");
						System.out.println("Enter group:");
						String group_name = sc.nextLine();
                        groupclient.getKeyChain(group_name);
						break;
                    case 10:
                        System.out.println("--  Add File Server IP address to token --");
                        System.out.println("Enter File Server IP:");
                        String fileIP = sc.nextLine();
                        token.setServerID(fileIP);
			break;
                    case 11:
						System.out.println("--  Disconnect from Group Server --");
						groupclient.disconnect();
						flag = false;
						break;
					default: System.out.println("Invalid Command Number");
						break;
			}
			System.out.println();
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
