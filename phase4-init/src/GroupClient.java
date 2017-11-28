/* Implements the GroupClient Interface */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class GroupClient extends Client implements GroupClientInterface {
	private ServerList serverList;
	private String ip;

	public Envelope firstHandshake(String username, byte[] nonce, byte[] key, byte[] iv,
								   ServerList serverList, String ip) {
		try
		{
			this.serverList = serverList;
			this.ip = ip;
			this.messageNumber = serverList.getServer(ip).getMessageNumber();
			Envelope message = null, response = null;
			message = new Envelope("HANDSHAKE");
			message.addObject(username);
			message.addObject(nonce);
			message.addObject(key);
			message.addObject(iv);
			finalizeMessage(message);

			response = (Envelope)input.readObject();

			if (validateMessageNumber(response)) {
				return response;
			} else {
				return null;
			}

		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	public boolean secondHandshake(BigInteger nonce) {
		try {
			Envelope message = null, response = null;
			message = new Envelope("HANDSHAKE");
			message.addObject(nonce);
			finalizeMessage(message);

			response = (Envelope) input.readObject();

			//If server indicates success, return true
			return validateMessageNumber(response) && response.getMessage().equals("OK");


		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return false;
		}
	}
	
	
	public KeyChain getKeyChain(String gname)
	{
		try
		{
			KeyChain kc = null;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return a token.
			message = new Envelope("KCHAIN");
			message.addObject(gname); //Add g name string
			finalizeMessage(message);
		
			//Get the response from the server
			response = (Envelope)input.readObject();

			if (validateMessageNumber(response) && response.getMessage().equals("OK")) {
				System.out.println("Message is OK");
				//If there is a token in the Envelope, return it
				ArrayList<Object> temp = null;
				temp = response.getObjContents();

				if(temp.size() == 2)
				{
					kc = (KeyChain)temp.get(0);
					return kc;
				}
			}
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	public UserToken getToken(String username)
	 {
		try
		{
			UserToken token = null;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return a token.
			message = new Envelope("GET");
			message.addObject(username); //Add user name string
			finalizeMessage(message);
		
			//Get the response from the server
			response = (Envelope)input.readObject();
			
			//Successful response
			if(validateMessageNumber(response) && response.getMessage().equals("OK"))
			{
				//If there is a token in the Envelope, return it 
				ArrayList<Object> temp = null;
				temp = response.getObjContents();
				
				if(temp.size() == 2)
				{
					token = (UserToken)temp.get(0);
					return token;
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
		
	 }

	 public PublicKey getPublicKey() {
		 try
		 {
			 Envelope message = null, response = null;
			 //Tell the server to create a user
			 message = new Envelope("PUBKEY");
			 output.writeObject(message);

			 response = (Envelope)input.readObject();

			 //Successful response
			 if(response.getMessage().equals("OK"))
			 {
				 //If there is a token in the Envelope, return it
				 ArrayList<Object> temp = null;
				 temp = response.getObjContents();

				 if(temp.size() == 1)
				 {
				 	return (PublicKey)temp.get(0);
				 }
			 }

			 return null;

		 }
		 catch(Exception e)
		 {
			 System.err.println("Error: " + e.getMessage());
			 e.printStackTrace(System.err);
			 return null;
		 }
	 }

	 public boolean createUser(String username, UserToken token, PublicKey userPublicKey)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to create a user
				message = new Envelope("CUSER");
				message.addObject(username); //Add user name string
				message.addObject(token); //Add the requester's token
				message.addObject(userPublicKey);
				finalizeMessage(message);
			
				response = (Envelope)input.readObject();
				
				//If server indicates success, return true
				return validateMessageNumber(response) && response.getMessage().equals("OK");

			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteUser(String username, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
			 
				//Tell the server to delete a user
				message = new Envelope("DUSER");
				message.addObject(username); //Add user name
				message.addObject(token);  //Add requester's token
				finalizeMessage(message);
			
				response = (Envelope)input.readObject();
				
				//If server indicates success, return true
				return validateMessageNumber(response) && response.getMessage().equals("OK");
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean createGroup(String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to create a group
				message = new Envelope("CGROUP");
				message.addObject(groupname); //Add the group name string
				message.addObject(token); //Add the requester's token
				finalizeMessage(message);
			
				response = (Envelope)input.readObject();
				
				//If server indicates success, return true
				return validateMessageNumber(response) && response.getMessage().equals("OK");
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteGroup(String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to delete a group
				message = new Envelope("DGROUP");
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				finalizeMessage(message);
			
				response = (Envelope)input.readObject();
				//If server indicates success, return true
				return validateMessageNumber(response) && response.getMessage().equals("OK");
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 @SuppressWarnings("unchecked")
	public List<String> listMembers(String group, UserToken token)
	 {
		 try
		 {
			 Envelope message = null, response = null;
			 //Tell the server to return the member list
			 message = new Envelope("LMEMBERS");
			 message.addObject(group); //Add group name string
			 message.addObject(token); //Add requester's token
			 finalizeMessage(message);
			 
			 response = (Envelope)input.readObject();
			 
			 //If server indicates success, return the member list
			 if(validateMessageNumber(response) && response.getMessage().equals("OK"))
			 { 
				return (List<String>)response.getObjContents().get(0); //This cast creates compiler warnings. Sorry.
			 }
				
			 return null;
			 
		 }
		 catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return null;
			}
	 }
	 
	 public boolean addUserToGroup(String username, String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to add a user to the group
				message = new Envelope("AUSERTOGROUP");
				message.addObject(username); //Add user name string
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				finalizeMessage(message);
			
				response = (Envelope)input.readObject();
				//If server indicates success, return true
				return validateMessageNumber(response) && response.getMessage().equals("OK");
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteUserFromGroup(String username, String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to remove a user from the group
				message = new Envelope("RUSERFROMGROUP");
				message.addObject(username); //Add user name string
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				finalizeMessage(message);
			
				response = (Envelope)input.readObject();
				//If server indicates success, return true
				return validateMessageNumber(response) && response.getMessage().equals("OK");
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }

	 private void finalizeMessage(Envelope message) {
		 message.addObject(messageNumber);
		 updateMessageNumber();
		 try {
			 output.writeObject(message);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	 }

	 private boolean validateMessageNumber(Envelope response) {
		 int respMsgNumber = (int) response.getObjContents().get(response.getObjContents().size() - 1);
		 return messageNumber == respMsgNumber;
	 }

	 private void updateMessageNumber() {
		messageNumber++;
		serverList.updateMessageNumber(ip, messageNumber);

		 ObjectOutputStream oos = null;
		 try {
			 oos = new ObjectOutputStream(new FileOutputStream("ServerList.bin"));
			 oos.writeObject(serverList);
			 oos.close();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }

	 }
}
