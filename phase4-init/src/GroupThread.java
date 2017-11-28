/* This thread does all the work. It communicates with the client through Envelopes.
 *
 */
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.Thread;
import java.math.BigInteger;
import java.net.Socket;
import java.io.*;
import java.security.Key;
import java.security.PublicKey;
import java.util.*;
import javax.crypto.spec.IvParameterSpec;

public class GroupThread extends Thread
{
	private final Socket socket;
	private GroupServer my_gs;
	private SecretKey sessionKey;
	private BigInteger secondNonce;

	public GroupThread(Socket _socket, GroupServer _gs)
	{
		socket = _socket;
		my_gs = _gs;
	}

	public void run()
	{
		boolean proceed = true;

		try
		{
			//Announces connection and opens object streams
			System.out.println("*** New connection from " + socket.getInetAddress() + ":" + socket.getPort() + "***");
			final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

			do
			{
				Envelope message = (Envelope)input.readObject();
				System.out.println("Request received: " + message.getMessage());
				Envelope response;

				if (message.getMessage().equals("HANDSHAKE"))//Client wants a token
				{
					if (message.getObjContents().size() == 4){	//First part of handshake
						String username = (String)message.getObjContents().get(0); //Get the username
						byte[] nonce = (byte[])message.getObjContents().get(1); //Get the nonce
						byte[] encryptedKey = (byte[])message.getObjContents().get(2); //Get the signed key
						byte[] iv = (byte[])message.getObjContents().get(3); //Get the iv
						BigInteger decryptedNonce;

						if(username == null)
						{
							response = new Envelope("FAIL");
							response.addObject(null);
							output.writeObject(response);
						} else {
							response = new Envelope("OK");
							decryptedNonce = new BigInteger(decrypt(my_gs.privateKey,
									nonce, "RSA", "BC"));

							byte[] signedKey = decrypt(my_gs.privateKey, encryptedKey, "RSA", "BC");
							byte[] byteKey = decrypt(getUserKey(username), signedKey, "RSA", "BC");

							assert byteKey != null;
							sessionKey = new SecretKeySpec(byteKey, 0, 16, "AES");
							KeySet sessionKeySet = new KeySet(sessionKey, new IvParameterSpec(iv));
							secondNonce = new BigInteger(256, new Random());
							response.addObject(decryptedNonce);
							response.addObject(encrypt(sessionKey, secondNonce.toByteArray(), "AES", "BC"));
							output.writeObject(response);
						}
					} else {	//Second part of handshake
						BigInteger nonce = (BigInteger)message.getObjContents().get(0);

						if (nonce.equals(secondNonce)) {
							response = new Envelope("OK");
							output.writeObject(response);
						} else {
							response = new Envelope("FAIL");
							output.writeObject(response);
						}
					}

				} else if (message.getMessage().equals("GET"))//Client wants a token
				{
					String username = (String)message.getObjContents().get(0); //Get the username
					if(username == null)
					{
						response = new Envelope("FAIL");
						response.addObject(null);
						output.writeObject(response);
					}
					else
					{
						UserToken yourToken = createToken(username); //Create a token

						//Respond to the client. On error, the client will receive a null token
						response = new Envelope("OK");
						response.addObject(yourToken);
						output.writeObject(response);
					}
				}
				else if (message.getMessage().equals("PUBKEY"))//Client wants a token
				{
					response = new Envelope("OK");
					response.addObject(my_gs.publicKey);
					output.writeObject(response);
				}
				else if(message.getMessage().equals("CUSER")) //Client wants to create a user
				{
					if(message.getObjContents().size() < 2)
					{
						response = new Envelope("FAIL");
					}
					else
					{
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null)
						{
							if(message.getObjContents().get(1) != null)
							{
								String username = (String)message.getObjContents().get(0); //Extract the username
								UserToken yourToken = (UserToken)message.getObjContents().get(1); //Extract the token
								PublicKey userPublicKey = (PublicKey)message.getObjContents().get(2);
								if(createUser(username, yourToken, userPublicKey))
								{
									response = new Envelope("OK"); //Success
								}
							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("DUSER")) //Client wants to delete a user
				{

					if(message.getObjContents().size() < 2)
					{
						response = new Envelope("FAIL");
					}
					else
					{
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null)
						{
							if(message.getObjContents().get(1) != null)
							{
								String username = (String)message.getObjContents().get(0); //Extract the username
								UserToken yourToken = (UserToken)message.getObjContents().get(1); //Extract the token

								if(deleteUser(username, yourToken))
								{
									response = new Envelope("OK"); //Success
								}
							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("CGROUP")) //Client wants to create a group
				{
				    if (message.getObjContents().size()  < 2) {
				    	response = new Envelope("FAIL");
					} else {
				    	response = new Envelope("FAIL");

				    	if (message.getObjContents().get(0) != null) {
				    		if (message.getObjContents().get(1) != null) {
				    			String groupName = (String)message.getObjContents().get(0); //Extract the group name
								UserToken yourToken = (UserToken)message.getObjContents().get(1); //Extract the token

								if (createGroup(groupName, yourToken)) {
									response = new Envelope("OK");
								}

							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("DGROUP")) //Client wants to delete a group
				{
					if (message.getObjContents().size()  < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if (message.getObjContents().get(0) != null) {
							if (message.getObjContents().get(1) != null) {
								String groupName = (String)message.getObjContents().get(0); //Extract the group name
								UserToken yourToken = (UserToken)message.getObjContents().get(1); //Extract the token

								if (deleteGroup(groupName, yourToken)) {
									response = new Envelope("OK");
								}

							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("LMEMBERS")) //Client wants a list of members in a group
				{
					if (message.getObjContents().size()  < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if (message.getObjContents().get(0) != null) {
							if (message.getObjContents().get(1) != null) {
								String groupName = (String)message.getObjContents().get(0); //Extract the group name
								UserToken yourToken = (UserToken)message.getObjContents().get(1); //Extract the token

								List<String> memberList = listMembers(groupName, yourToken);

								if (memberList != null) {
									response = new Envelope("OK");
									response.addObject(memberList);
								}

							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("AUSERTOGROUP")) //Client wants to add user to a group
				{
					if (message.getObjContents().size()  < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if (message.getObjContents().get(0) != null) {
							if (message.getObjContents().get(1) != null) {
								if (message.getObjContents().get(2) != null) {
									String username = (String)message.getObjContents().get(0); //Extract the group name
									String groupName = (String)message.getObjContents().get(1); //Extract the group name
									UserToken yourToken = (UserToken)message.getObjContents().get(2); //Extract the token

									if (addUserToGroup(username, groupName, yourToken)) {
										response = new Envelope("OK");
									}

								}
							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("RUSERFROMGROUP")) //Client wants to remove user from a group
				{
					if (message.getObjContents().size()  < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if (message.getObjContents().get(0) != null) {
							if (message.getObjContents().get(1) != null) {
								if (message.getObjContents().get(2) != null) {
									String username = (String)message.getObjContents().get(0); //Extract the group name
									String groupName = (String)message.getObjContents().get(1); //Extract the group name
									UserToken yourToken = (UserToken)message.getObjContents().get(2); //Extract the token

									if (removeUserFromGroup(username, groupName, yourToken)) {
										response = new Envelope("OK");
									}

								}
							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("DISCONNECT")) //Client wants to disconnect
				{
					socket.close(); //Close the socket
					proceed = false; //End this communication loop
				}
				else
				{
					response = new Envelope("FAIL"); //Server does not understand client request
					output.writeObject(response);
				}
			}while(proceed);
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	private PublicKey getUserKey(String username) {
		return my_gs.userList.getPublicKey(username);
	}

	// Returns a list containing the members of the specified group. Returns null if error.
	// only shows list if 
	private List<String> listMembers(String groupName, UserToken yourToken) {
		String requester = yourToken.getSubject();
		// Make sure group exists
		if(my_gs.groupList.checkGroup(groupName)) {
			// if requester is owner of group
			ArrayList<String> owner = my_gs.groupList.getOwnership(groupName);
			if (owner.contains(requester)){
				ArrayList<String> groupMembers;
				groupMembers = my_gs.groupList.getUsers(groupName);
				return groupMembers;
			}
			else
			{
				System.out.println("User not owner of group");
				return null;
			}
		}
		// Requester does not have a group to list
		else {
			System.out.println("Group Doesn't Exist");
			return null;
		}
	}

	// Returns true if the user was successfully removed from the group, else false.
	private boolean removeUserFromGroup(String username, String groupName, UserToken yourToken) {
		String requester = yourToken.getSubject();

		// Does requester exist?
		if(my_gs.userList.checkUser(requester)) {
			ArrayList<String> owner = my_gs.groupList.getOwnership(groupName);
			// Requester needs to be an administrator of the group
			if (owner == null) {
				return false;
			}
			if(owner.contains(requester)) {
				// Does user exist in the group?
				ArrayList<String> groupList = my_gs.groupList.getUsers(groupName);
				if(groupList.contains(username)) {
					// User is deleted from the group
					my_gs.groupList.removeUser(username, groupName);
					return true;
				}
				else {
				// User does not exist in the group
				return false;
				}
			}
			else {
			// Requester is not an owner of the group
			return false;
			}
		}
		else {
			// Requester does not exist
			return false;
		}
	}

	// Returns true if the user was successfully added to the group, else false.
	private boolean addUserToGroup(String username, String groupName, UserToken yourToken) {
		String requester = yourToken.getSubject();

		// Check if requester exists
		if(my_gs.userList.checkUser(requester)) {
			ArrayList<String> owner = my_gs.groupList.getOwnership(groupName);
			// Requester needs to be an administrator of the group
			if(owner.contains(requester)) {
				// Does user exist in the group?
				ArrayList<String> groupList = my_gs.groupList.getUsers(groupName);
				if(groupList.contains(username)) {
					// User already exists in the group
					return false;
				}
				else {
					my_gs.groupList.addUser(username, groupName);
					my_gs.userList.addGroup(username, groupName);
					return true;
				}
			}
			else {
				// Requester not an administrator of the group
				return false;
			}
		}
		else {
			// Requester does not exist
			return false;
		}
	}

	// Returns true if the group was successfully deleted, else false.
	private boolean deleteGroup(String groupName, UserToken yourToken) {
		String requester = yourToken.getSubject();

		// Does requester exist?
		if(my_gs.userList.checkUser(requester)) {
			ArrayList<String> owner = my_gs.groupList.getOwnership(groupName);
			// Requester needs to be an owner of the group
			if(owner.contains(requester)) {
				my_gs.groupList.deleteGroup(groupName);
				return true;
			}
			else {
				// Requester is not an owner of the group
				return false;
			}
		}
		else {
			// Requester does not exist
			return false;
		}
	}

	// Returns true if the group was successfully created, else false.
	private boolean createGroup(String groupName, UserToken yourToken) {
		String requester = yourToken.getSubject();
		ArrayList<String> temp = my_gs.userList.getUserGroups(requester);
			// Group needs to not already exist
			if(temp.contains(groupName)) {
				// Group already exists
				System.out.println("Group Already Exists");
				return false;
			}
			else {
				my_gs.groupList.addGroup(groupName);
				my_gs.groupList.addOwnership(requester, groupName);
				// also add to user list of that group
				my_gs.groupList.addUser(requester, groupName);
				my_gs.userList.addGroup(requester, groupName);
				return true;
			}
	}

	//Method to create tokens
	private UserToken createToken(String username)
	{
		//Check that user exists
		if(my_gs.userList.checkUser(username))
		{
			//Issue a new token with server's name, user's name, and user's groups
			return new Token(my_gs.name, username, my_gs.userList.getUserGroups(username));
		}
		else
		{
			return null;
		}
	}


	//Method to create a user
	private boolean createUser(String username, UserToken yourToken, PublicKey userPublicKey)
	{
		String requester = yourToken.getSubject();

		//Check if requester exists
		if(my_gs.userList.checkUser(requester))
		{
			//Get the user's groups
			ArrayList<String> temp = my_gs.userList.getUserGroups(requester);
			//requester needs to be an administrator
			if(temp.contains("ADMIN"))
			{
				//Does user already exist?
				if(my_gs.userList.checkUser(username))
				{
					return false; //User already exists
				}
				else
				{
					my_gs.userList.addUser(username, userPublicKey);
					return true;
				}
			}
			else
			{
				return false; //requester not an administrator
			}
		}
		else
		{
			return false; //requester does not exist
		}
	}

	//Method to delete a user
	private boolean deleteUser(String username, UserToken yourToken)
	{
		String requester = yourToken.getSubject();

		//Does requester exist?
		if(my_gs.userList.checkUser(requester))
		{
			ArrayList<String> temp = my_gs.userList.getUserGroups(requester);
			//requester needs to be an administer
			if(temp.contains("ADMIN"))
			{
				//Does user exist?
				if(my_gs.userList.checkUser(username))
				{
					//User needs deleted from the groups they belong
					ArrayList<String> deleteFromGroups = new ArrayList<String>();

					//This will produce a hard copy of the list of groups this user belongs
					deleteFromGroups.addAll(my_gs.userList.getUserGroups(username));

					//If groups are owned, they must be deleted
					ArrayList<String> deleteOwnedGroup = new ArrayList<String>();

					//Make a hard copy of the user's ownership list
					deleteOwnedGroup.addAll(my_gs.userList.getUserOwnership(username));

					//Delete owned groups
					for(int index = 0; index < deleteOwnedGroup.size(); index++)
					{
						//Use the delete group method. Token must be created for this action
						deleteGroup(deleteOwnedGroup.get(index), new Token(my_gs.name, username, deleteOwnedGroup));
					}

					//Delete the user from the user list
					my_gs.userList.deleteUser(username);

					return true;
				}
				else
				{
					return false; //User does not exist

				}
			}
			else
			{
				return false; //requester is not an administer
			}
		}
		else
		{
			return false; //requester does not exist
		}
	}

	private static byte[] encrypt(Key key, byte[] bytes, String type, String provider) {
		try {

			//Create an cipher using bouncycastle. Set to encrypt using key
			Cipher cipher = Cipher.getInstance(type, provider);
			cipher.init(Cipher.ENCRYPT_MODE, key);

			return cipher.doFinal(bytes);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static byte[] decrypt(Key key, byte[] encryptedText, String type, String provider) {
		try {
			//Create an cipher using bouncycastle. Set to decrypt using key
			Cipher cipher = Cipher.getInstance(type, provider);
			cipher.init(Cipher.DECRYPT_MODE, key);

			//Return string representation of the decrypted byte array.
			return cipher.doFinal(encryptedText);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
