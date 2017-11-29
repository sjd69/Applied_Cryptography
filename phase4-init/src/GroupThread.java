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
	private SecretKey hmacKey;
	private BigInteger secondNonce;
	private KeySet sessionKeySet;
	private int messageNumber = -1;
	private String username;
	private Crypto crypto = new Crypto();

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
				Envelope message;
				Object obj = input.readObject();
				if (obj.getClass() != Envelope.class) {
					message = parseMessage((byte[]) obj);
				} else {
					message = (Envelope) obj;
				}
				System.out.println("Request received: " + (message != null ? message.getMessage() : null));
				Envelope response;

				if (message.getMessage().equals("HANDSHAKE"))//Client wants a token
				{
					if (message.getObjContents().size() == 6){	//First part of handshake
						username = (String)message.getObjContents().get(0); //Get the username
						byte[] nonce = (byte[])message.getObjContents().get(1); //Get the nonce
						byte[] encryptedKey = (byte[])message.getObjContents().get(2); //Get the signed key
						byte[] iv = (byte[])message.getObjContents().get(3); //Get the iv
						byte[] hmac = (byte[])message.getObjContents().get(4); //Get the iv
						BigInteger decryptedNonce;

						if (messageNumber == -1) {
							setMessageNumber(message);
						}

						if(username == null || !validateMessageNumber(message))
						{
							response = new Envelope("FAIL");
							response.addObject(null);
							finalizeMessage(response, output, true);
						} else {
							response = new Envelope("OK");
							decryptedNonce = new BigInteger(crypto.rsaDecrypt(my_gs.privateKey, nonce));

							byte[] signedKey = crypto.rsaDecrypt(my_gs.privateKey, encryptedKey);
							//byte[] byteKey = decrypt(getUserKey(username), signedKey, "RSA", "BC");

							//assert byteKey != null;
							SecretKey sessionKey = new SecretKeySpec(signedKey, 0, 16, "AES");
							sessionKeySet = new KeySet(sessionKey, new IvParameterSpec(iv));

							hmacKey = new SecretKeySpec(hmac, "HmacMD5");
							secondNonce = new BigInteger(256, new Random());
							response.addObject(decryptedNonce);
							//response.addObject(encrypt(sessionKey, secondNonce.toByteArray(), "AES", "BC"));
							response.addObject(crypto.aesEncrypt(sessionKeySet, secondNonce.toByteArray()));
							finalizeMessage(response, output, true);
						}
					} else {	//Second part of handshake
						BigInteger nonce = (BigInteger)message.getObjContents().get(0);

						if (nonce.equals(secondNonce) && validateMessageNumber(message)) {
							response = new Envelope("OK");
							finalizeMessage(response, output, true);
						} else {
							response = new Envelope("FAIL");
							finalizeMessage(response, output, true);
						}
					}

				} else if (message.getMessage().equals("GET"))//Client wants a token
				{
					String username = (String)message.getObjContents().get(0); //Get the username
					if(username == null)
					{
						response = new Envelope("FAIL");
						response.addObject(null);
						finalizeMessage(response, output, false);
					}
					else
					{
						UserToken yourToken = createToken(username); //Create a token

						//Respond to the client. On error, the client will receive a null token
						response = new Envelope("OK");
						response.addObject(yourToken);
						finalizeMessage(response, output, false);
					}
				}
				else if (message.getMessage().equals("PUBKEY"))//Client wants a token
				{
					response = new Envelope("OK");
					response.addObject(my_gs.publicKey);
					output.writeObject(response);
				}
				else if (message.getMessage().equals("KCHAIN"))
				{
					response = new Envelope("OK");
					if(message.getObjContents().get(0) != null)
					{
						String g_name = (String)message.getObjContents().get(0);
						response.addObject(getGroupKeyChain(g_name));
						finalizeMessage(response, output, false);
					}
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

					finalizeMessage(response, output, false);
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

					finalizeMessage(response, output, false);
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

					finalizeMessage(response, output, false);
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

					finalizeMessage(response, output, false);
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

					finalizeMessage(response, output, false);
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

					finalizeMessage(response, output, false);
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

					finalizeMessage(response, output, false);
				}
				else if(message.getMessage().equals("DISCONNECT")) //Client wants to disconnect
				{
					socket.close(); //Close the socket
					proceed = false; //End this communication loop
				}
				else
				{
					response = new Envelope("FAIL"); //Server does not understand client request
					finalizeMessage(response, output, false);
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
	
	private KeyChain getGroupKeyChain(String grp_name){
		if (my_gs.keychainList != null)
		{
			return my_gs.keychainList.getKeyChain(grp_name);
		}
		else
		{
			return new KeyChain("TEMP");
		}
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
			Crypto crypto = new Crypto();
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
					
					// make a new group key
					KeySet newGroupKey = crypto.getKeySet();
					// get group new keychain
					KeyChain kc = my_gs.keychainList.getKeyChain(groupName);
					// add new group key to keychain
					kc.addNewKey(newGroupKey);
					// update keychainList
					my_gs.keychainList.addKeyChain(groupName, kc);
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
		Crypto crypto = new Crypto();
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
				
				// generate a new group key for file crypto
				KeySet groupKey = crypto.getKeySet();
				// create a new keychain
				KeyChain kchain = new KeyChain(groupName);
				// add new group key to keychain
				kchain.addNewKey(groupKey);
				// update keychainList
				my_gs.keychainList.addKeyChain(groupName, kchain);
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

	private void finalizeMessage(Envelope message, ObjectOutputStream output, boolean isHandshake) {
		updateMessageNumber(this.username);
		message.addObject(messageNumber);

		if (isHandshake) {
			try {
				output.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			byte[] encryptedBytes = null;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(message);
				byte[] bytes = baos.toByteArray();
				encryptedBytes = crypto.aesEncrypt(sessionKeySet, bytes);

				output.writeObject(encryptedBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private Envelope parseMessage(byte[] response) {
		ObjectInputStream ois;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(crypto.aesDecrypt(sessionKeySet, response));
			ois = new ObjectInputStream(bais);
			return (Envelope) ois.readObject();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void setMessageNumber(Envelope message) {
		this.messageNumber = (int) message.getObjContents().get(message.getObjContents().size() - 1);
	}

	private boolean validateMessageNumber(Envelope response) {
		int respMsgNumber = (int) response.getObjContents().get(response.getObjContents().size() - 1);
		return messageNumber == respMsgNumber;
	}

	private void updateMessageNumber(String username) {
		messageNumber++;
		my_gs.userList.updateMessageNumber(username, messageNumber);

	}
}
