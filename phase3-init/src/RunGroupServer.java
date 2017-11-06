/* Driver program for FileSharing Group Server */

import java.security.InvalidKeyException;

public class RunGroupServer {
	
	public static void main(String[] args) throws InvalidKeyException {
		if (args.length> 0) {
			try {
				GroupServer server = new GroupServer(Integer.parseInt(args[0]));
				server.start();
			}
			catch (NumberFormatException e) {
				System.out.printf("Enter a valid port number or pass no arguments to use the default port (%d)\n", GroupServer.SERVER_PORT);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		}
		else {
			GroupServer server = new GroupServer();
			server.start();
		}
	}
}
