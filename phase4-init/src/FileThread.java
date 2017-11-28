/* File worker thread handles the business of uploading, downloading, and removing files for clients with valid tokens */

import java.lang.Thread;
import java.net.Socket;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class FileThread extends Thread
{
	private final Socket socket;
	private FileServer my_fs;
	private SecretKey sessionKey;
	private BigInteger secondNonce;

	public FileThread(Socket _socket)
	{
		socket = _socket;
	}

	public void run()
	{
		boolean proceed = true;
		try
		{
			System.out.println("*** New connection from " + socket.getInetAddress() + ":" + socket.getPort() + "***");
			final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			Envelope response;
			

			do
			{
				Envelope e = (Envelope)input.readObject();
				System.out.println("Request received: " + e.getMessage());
				Envelope response;
				
				if (e.getMessage().equals("HANDSHAKE")) // Client wants a token
				{
					if (e.getObjContents().size() == 4) { // First part of handshake
						Crypto crypto = new Crypto();
						String username = (String)e.getObjContents().get(0); //Get the username
						byte[] nonce = (byte[])e.getObjContents().get(1); //Get the nonce
						byte[] encryptedKey = (byte[])e.getObjContents().get(2); //Get the signed key
						byte[] iv = (byte[])e.getObjContents().get(3); //Get the iv
						BigInteger decryptedNonce;
						
						if (username == null)
						{
							response = new Envelope("FAIL");
							response.addObject(null);
							output.writeObject(response);
						} else {
							response = new Evelope("OK");
							decryptedNonce = new BigInteger(crypto.rsaDecrypt(my_gs.privateKey, nonce));
							
							byte[] signedKey = byte[] signedKey = crypto.rsaDecrypt(my_gs.privateKey, encryptedKey);
							// byte[] byteKey = decrypt(getUserKey(username), signedKey, "RSA", "BC");
							
							// assert byteKey != null
							sessionKey = new SecretKeySpec(signedKey, 0, 16, "AES");
							Keyset sessionKeySet = new KeySet(sessionKey, new IvParameterSpec(iv));
							secondNonce = new BigInteger(256, new Random());
							response.addObject(decryptedNonce);
							//response.addObject(encrypt(sessionKey, secondNonce.toByteArray(), "AES", "BC"));
							response.addObject(crypto.aesEncrypt(sessionKeySet, secondNonce.toByteArray()));
							output.writeObject(response);
						}
					} else { // Second part of handshake
						BigInteger nonce = (BigInteger)e.getObjContents().get(0);
						
						if (nonce.equals(secondNonce)) {
							response = new Envelope("OK");
							output.writeObject(response);
						} else {
							response = new Envelope("FAIL");
							output.writeObject(response);
						}
					}
				} else if (e.getMessage().equals("GET")) { // Client wants a token
					String username = (String)e.getObjContents().get(0); // Get the username
					if (username == null) 
					{
						response = new Envelope("FAIL");
						response.addObject(null);
						output.writeObject(response);
					}
					else {
						UserToken yourToken = createToken(username); // Create a token
						
						// Respond to the Client. On error, the client will receive a null token
						response = new Envelope("OK");
						response.addObject(yourToken);
						output.writeObject(reponse);
					}
				} else if (e.getMessage().equals("PUBKEY")) { // Client wants a token
					response = new Envelope("OK");
					response.addObject(my_fs.publicKey);
					output.writeObject(response);
				}

				// Handler to list files that this user is allowed to see
				else if(e.getMessage().equals("LFILES"))
				{
				    /* First shot at trying to implement this 
				    */
				    UserToken token = (UserToken)e.getObjContents().get(0);
				    ArrayList<ShareFile> files = FileServer.fileList.getFiles();
					if (files == null) {
						System.out.printf("No Files Exist For User");
					}
					else
					{
						e = new Envelope("OK");
						
						// uFiles to hold list of user file names
						ArrayList<String> uFiles = new ArrayList<String>();
						// list of user groups
						List<String> userGroups = token.getGroups();
						for (ShareFile x:files)
						{
							String groupname = x.getGroup();
							// check users groups and if theres a match
							for (String y:userGroups)
							{
								if (y.equals(groupname)){
									// add it to uFiles if there is a match
									System.out.println(x.getPath());
									uFiles.add(x.getPath());
								}
							}
						}
						//return the list of user files to the client
						e.addObject(uFiles);
						output.writeObject(e);
					}
				}
				if(e.getMessage().equals("UPLOADF"))
				{

					if(e.getObjContents().size() < 3)
					{
						response = new Envelope("FAIL-BADCONTENTS");
					}
					else
					{
						if(e.getObjContents().get(0) == null) {
							response = new Envelope("FAIL-BADPATH");
						}
						if(e.getObjContents().get(1) == null) {
							response = new Envelope("FAIL-BADGROUP");
						}
						if(e.getObjContents().get(2) == null) {
							response = new Envelope("FAIL-BADTOKEN");
						}
						else {
							String remotePath = (String)e.getObjContents().get(0);
							String group = (String)e.getObjContents().get(1);
							UserToken yourToken = (UserToken)e.getObjContents().get(2); //Extract token

							if (FileServer.fileList.checkFile(remotePath)) {
								System.out.printf("Error: file already exists at %s\n", remotePath);
								response = new Envelope("FAIL-FILEEXISTS"); //Success
							}
							else if (!yourToken.getGroups().contains(group)) {
								System.out.printf("Error: user missing valid token for group %s\n", group);
								response = new Envelope("FAIL-UNAUTHORIZED"); //Success
							}
							else  {
								File file = new File("shared_files/"+remotePath.replace('/', '_'));
								file.createNewFile();
								FileOutputStream fos = new FileOutputStream(file);
								System.out.printf("Successfully created file %s\n", remotePath.replace('/', '_'));

								response = new Envelope("READY"); //Success
								output.writeObject(response);

								e = (Envelope)input.readObject();
								while (e.getMessage().compareTo("CHUNK")==0) {
									fos.write((byte[])e.getObjContents().get(0), 0, (Integer)e.getObjContents().get(1));
									response = new Envelope("READY"); //Success
									output.writeObject(response);
									e = (Envelope)input.readObject();
								}

								if(e.getMessage().compareTo("EOF")==0) {
									System.out.printf("Transfer successful file %s\n", remotePath);
									FileServer.fileList.addFile(yourToken.getSubject(), group, remotePath);
									response = new Envelope("OK"); //Success
								}
								else {
									System.out.printf("Error reading file %s from client\n", remotePath);
									response = new Envelope("ERROR-TRANSFER"); //Success
								}
								fos.close();
							}
						}
					}

					output.writeObject(response);
				}
				else if (e.getMessage().compareTo("DOWNLOADF")==0) {

					String remotePath = (String)e.getObjContents().get(0);
					Token t = (Token)e.getObjContents().get(1);
					ShareFile sf = FileServer.fileList.getFile("/"+remotePath);
					if (sf == null) {
						System.out.printf("Error: File %s doesn't exist\n", remotePath);
						e = new Envelope("ERROR_FILEMISSING");
						output.writeObject(e);

					}
					else if (!t.getGroups().contains(sf.getGroup())){
						System.out.printf("Error user %s doesn't have permission\n", t.getSubject());
						e = new Envelope("ERROR_PERMISSION");
						output.writeObject(e);
					}
					else {

						try
						{
							File f = new File("shared_files/_"+remotePath.replace('/', '_'));
						if (!f.exists()) {
							System.out.printf("Error file %s missing from disk\n", "_"+remotePath.replace('/', '_'));
							e = new Envelope("ERROR_NOTONDISK");
							output.writeObject(e);

						}
						else {
							FileInputStream fis = new FileInputStream(f);

							do {
								byte[] buf = new byte[4096];
								if (e.getMessage().compareTo("DOWNLOADF")!=0) {
									System.out.printf("Server error: %s\n", e.getMessage());
									break;
								}
								e = new Envelope("CHUNK");
								int n = fis.read(buf); //can throw an IOException
								if (n > 0) {
									System.out.printf(".");
								} else if (n < 0) {
									System.out.println("Read error");

								}


								e.addObject(buf);
								e.addObject(new Integer(n));

								output.writeObject(e);

								e = (Envelope)input.readObject();


							}
							while (fis.available()>0);

							//If server indicates success, return the member list
							if(e.getMessage().compareTo("DOWNLOADF")==0)
							{

								e = new Envelope("EOF");
								output.writeObject(e);

								e = (Envelope)input.readObject();
								if(e.getMessage().compareTo("OK")==0) {
									System.out.printf("File data upload successful\n");
								}
								else {

									System.out.printf("Upload failed: %s\n", e.getMessage());

								}

							}
							else {

								System.out.printf("Upload failed: %s\n", e.getMessage());

							}
						}
						}
						catch(Exception e1)
						{
							System.err.println("Error: " + e.getMessage());
							e1.printStackTrace(System.err);

						}
					}
				}
				else if (e.getMessage().compareTo("DELETEF")==0) {

					String remotePath = (String)e.getObjContents().get(0);
					Token t = (Token)e.getObjContents().get(1);
					ShareFile sf = FileServer.fileList.getFile("/"+remotePath);
					if (sf == null) {
						System.out.printf("Error: File %s doesn't exist\n", remotePath);
						e = new Envelope("ERROR_DOESNTEXIST");
					}
					else if (!t.getGroups().contains(sf.getGroup())){
						System.out.printf("Error user %s doesn't have permission\n", t.getSubject());
						e = new Envelope("ERROR_PERMISSION");
					}
					else {

						try
						{


							File f = new File("shared_files/"+"_"+remotePath.replace('/', '_'));

							if (!f.exists()) {
								System.out.printf("Error file %s missing from disk\n", "_"+remotePath.replace('/', '_'));
								e = new Envelope("ERROR_FILEMISSING");
							}
							else if (f.delete()) {
								System.out.printf("File %s deleted from disk\n", "_"+remotePath.replace('/', '_'));
								FileServer.fileList.removeFile("/"+remotePath);
								e = new Envelope("OK");
							}
							else {
								System.out.printf("Error deleting file %s from disk\n", "_"+remotePath.replace('/', '_'));
								e = new Envelope("ERROR_DELETE");
							}


						}
						catch(Exception e1)
						{
							System.err.println("Error: " + e1.getMessage());
							e1.printStackTrace(System.err);
							e = new Envelope(e1.getMessage());
						}
					}
					output.writeObject(e);

				}
				else if(e.getMessage().equals("DISCONNECT"))
				{
					socket.close();
					proceed = false;
				}
			} while(proceed);
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

}
