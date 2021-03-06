import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Client {

	/* protected keyword is like private but subclasses have access
	 * Socket and input/output streams
	 */
	protected Socket sock;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;
	protected int messageNumber = -1;

	public boolean connect(final String server, final int port) {
		System.out.println("attempting to connect");
		
		try
		{
			// Connect to server
			sock = new Socket(server, port);
			System.out.println("Connected to " + server + " on port " + port);
			
			// Set up I/O streams with the server
			output = new ObjectOutputStream(sock.getOutputStream());
	    	input = new ObjectInputStream(sock.getInputStream());
	    	return true;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return false;
		}
	}

	public boolean connect(final String server, final int port, final int messageNumber) {
		System.out.println("attempting to connect");

		try
		{
			// Connect to server
			sock = new Socket(server, port);
			System.out.println("Connected to " + server + " on port " + port);

			// Set up I/O streams with the server
			output = new ObjectOutputStream(sock.getOutputStream());
			input = new ObjectInputStream(sock.getInputStream());
			this.messageNumber = messageNumber;
			return true;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return false;
		}
	}

	public boolean isConnected() {
		if (sock == null || !sock.isConnected()) {
			return false;
		}
		else {
			return true;
		}
	}

	public int getMessageNumber() {
		return messageNumber;
	}

	public void disconnect()	 {
		if (isConnected()) {
			try
			{
				Envelope message = new Envelope("DISCONNECT");
				output.writeObject(message);
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
	}
}
