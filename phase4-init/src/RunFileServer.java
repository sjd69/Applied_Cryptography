/* Driver program for FileSharing File Server */

import java.security.InvalidKeyException;

public class RunFileServer {
	
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				FileServer server = new FileServer(Integer.parseInt(args[0]));
				server.start();
			}
			catch (NumberFormatException e) {
				System.out.printf("Enter a valid port number or pass no arguments to use the default port (%d)\n", FileServer.SERVER_PORT);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		}
		else {
			FileServer server = new FileServer();
			try {
				server.start();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		}
	}

}
