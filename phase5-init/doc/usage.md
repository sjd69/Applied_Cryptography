
# Usage Instructions

## Running the Group Server

To start the Group Server:
 - Enter the src directory containing `RunGroupServer.class`
 - Type `java RunGroupServer`

When the group server is first started, there are no users or groups. Since there must be an ADMIN of the system, the user is prompted via the console to enter a username. This name becomes the first user and is a member of the *ADMIN* group.  No groups other than *ADMIN* will exist.

## Running the File Server

To start the File Server:
 - Enter the src directory containing `RunFileServer.class`
 - Type `java RunFileServer`

The file server will create a shared_files inside the working directory if one does not exist. The file server is now online.

## Generating RSA key pair
To run KeyGen:
 - Enter the src directory containing `KeyGen.class`
 - Type `java KeyGen`
 
The application will output a public and private key pair. The public key will need copied to the group server (on first usgae) and private key will need copied to the client for login.

## Running MyClient
First, run both the GroupServer and the FileServer in seperate terminals as stated above.
 - Enter the src directory containing `MyClient.class`
 - Type `java MyClient`
 - Login when prompted for your username
 - Use the menu prompts to interact with either the GroupServer, FileServer, or FileCrypto System

## Resetting the Group or File Server

To reset the Group Server, delete the file `UserList.bin` and the file 'GroupList.bin'

To reset the File Server, delete the `FileList.bin` file and the `shared_files/` directory.
