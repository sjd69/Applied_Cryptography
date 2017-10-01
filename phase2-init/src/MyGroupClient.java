import java.util.ArrayList;
import java.util.List;
import java.io.ObjectInputStream;

public class MyGroupClient
{
	GroupClient groupclient = new GroupClient();
	
	public boolean startMyGroupClient(String server, int port)
	{
		if (fileclient.connect(server, port))
		{
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
			System.out.println("Error connecting to GroupClient");
		}
	}
	
}
