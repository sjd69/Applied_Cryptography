# Cryptographic Mechanisms
We have added new mechanisms in order to deal with three additional threat models while keeping our main ideas of ease of use and coverage at the forefront. As economy of mechanism is an integral aspect of our design philosophy, many of our previous mechanisms could be modified to provide more protection where otherwise it might have been necessary to design or integrate entirely new mechanisms. 

Our implementation utilizes the following mechanisms:

* **SHA256:** We utilize SHA256 for hashing within our system. We chose to use SHA256 instead of SHA1 due to SHA1 being considered broken. Since this is just a file sharing service, SHA256 seems more than adequate rather than going with the more heavy-handed SHA512.
* **RSA:** Our implementation uses 2048 bit RSA keys for public key encryption. 1024 RSA keys are considered dead. 2048 bit keys are still considered secure and provide us with performance and storage benefits over 4096 bit keys, while still supplying us with sufficient security. In addition, 4096 bit RSA keys have potential compatibility concerns with older hardware and we would like to reach the widest audience possible while still providing adequate security.
* **Random Number Challenge (Nonce):** The size of all random challenges utilized are 256 bits. This size is sufficently large to protect against brute force random guessing of the challenge by an adversary. Random challenges will not be reused.
* **AES:** For symmetric key encryption within our system (for session keys), we will utilize AES with a 128 bit key size. We chose 128 bit keys over 256 bit keys as 128 bit keys are significantly faster but still sufficent security-wise and 128-bit is the largest allowed by JavaCrypto. We utilize CBC as the mode of operation as CBC provides message dependence for generating cipher text unlike ECB mode, which is subject to code book attacks. This is particularly relevant to **T5** below.
* **MD5:** We utilize MD5 within our system for generating public key fingerprints. MD5 produces human readible fingerprints, which is necessary for our purposes. MD5 is also used for SSH host verification, which is why we chose it compared to SHA1.
* **Timestamp:** Timestamps will be unique to each message and will be verifiable by both the client and the server. We consider a threshold of three minutes be sufficient to prevent replay attacks without accidentally disrupting normal usage of the file sharing system. See **T5** below for more details.
* **Counter:** Counters will be an integer count of the next expected message number. This number will be incremented each time a message is recieved. Initial number count for each party is elaborated upon in **T5**

Mechanisms T1-T4 can be found here: https://github.com/NohrianScum/cs1635-2017fa-kdc42-ruz24-sjd69/blob/master/phase3-init/reports/phase3-writeup.md


# T5: Message Reorder, Replay or Modification
**Assumption 1:** The initial handshake and authentification between either the group or file server and the client is as specified in the digrams below.

**Assumption 2:** Clients and Servers have synchronized clocks.

After connecting to and properly authenticating a group or file server, the messages passed between the client and server are subject to being reordered, saved for a replay attack, or modified by an active adversary. Modifying messages can affect the integrity and avaliability of data stored on the server or received by the user. An adversary that may insert communications, such as deleting a file from a server, can disrupt data avaliablility. Inserting fake communications can also compromise data integrity as malicious files may be placed onto the server or sent to the user. Reordering communications can also compromise data avaliablility and integrity. For example, a user wishes to download a file and then delete the file from a server. Reordering these messages deletes the file before the user can access it. 

To protect against **T4** in the previous phase of this project, our file sharing system uses a unique secret session key to encrypt communications between a server and a client. This mechanism offers protection against passive adversaries, and protection from replay attacks between sessions. Our current implementation does not protect against an active attacker that can insert, reorder, replay or modify messages within a session. To offer protection against this stronger attack, we utilize timestamping, message counters and CBC residue in addition to the previously implemented session key.


### Mechanism
Our file sharing system utilizes a 128 bit AES (CBC mode) session key from the previous phase. The use of CBC mode, as opposed to ECB mode, prevents an adversary from generating a code book to use for a replay attack within a session, as CBC mode creates message dependence of cipher text.

##### Replay
The addition of timestamps to messages will protect against replay attacks. Each communication will be timestamped with the time that the message is sent before encryption with the session key. When a party receives a communication, the message will be decrypted and the timestamp will be validated against a threshold of three minutes. If the timestamp does not fall within this threshold, we consider the message a replay attack and will terminate the connection. 

##### Reordering
The addition of a message counter will protect against reordering attacks. Each communication will include a message number before encryption with the session key. At the time of connection, both the server and client will begin keeping track of the count of messages recieved from the other party. Each message will be sent with the count number of the message. The party receiving the message will verify that the message number received is the next message expected based on the count. If the message number is not as expected, we assume a reordering attack and terminate the connection. The random number used for authentification will be the initial message count for both the user and group server. The file server will default to begin message count at 0. This way the opposite party will know be able to verify the message count number without the exchange of further messages.

##### Modification
The addition of CBC residue from our AES encryption scheme will serve as a MAC to protect against message modification. During communication, the CBC residue will be saved and transmitted to the recipient. The receiving party can then recompute and verify the CBC residue of the message. However, any attacker who would attempt to intercept and modify the message would be unable to recompute the CBC residue to correctly match the modification, since the adversary does not have access to the symmetric key. If the CBC residue sent does not match the residue computed, we assume a reordering attack and terminate the connection. 

### Justification
We utilize CBC as the mode of operation as CBC provides message dependence for generating cipher text unlike ECB mode, which is subject to code book attacks. The CBC residue will allow a recieving party to verify that the message has not been modified, since an adversary will not be able recompute the residue after modification since they do not have access to the session key. Timestamps will be unique to each message and will be easily verifiable by both the client and the server. We consider the threshold of three minutes to be sufficient to account for clock differences without accidentally dirsupting normal usage of the file sharing system. Keeping a message count and numbering messages ensures that messages are being received in the correct order. 

### Diagram
![alt text](T5.png)
![alt text](T5Handshake.png)
![alt text](T5HandshakeF.png)

# T6: File Leakage
Although we authenticate file servers in the previous phase of the project, we still assume that these servers may be malicious. Since we assume file servers to be untrusted, we assume that they may leak files stored on the server to unauthorized individuals at will. This disrupts data confidentiality of group files since users are under the assumption that only valid group members may have access to those files. We also need a way to adapt the secure storage of files with dynamic group membership. When a user is removed from a group, he or she should not have a mechanism to uncover leaked files. When a new user is added to a group, he or she should be able to uncover all files within that group.

### Mechanism
**Policy:** A user who is deleted from a group is not able to directly access a file through our system. However, at the time a user was a member of a group, he or she had full access to these files and we assume that all files were downloaded. We assume that a removed group member keeps old group keys so that he or she will be able to decrypt a leaked file from that group if it was avaliable. An old group member will not be able to decrypt any leaked file created or modified since that user was removed from the group. A member added to the group at a later time is able to access all files from the inception of that group.

**Assumption 1:** There exists a seperate client side application that encrypts and decrypts files using the group keys. 
**Assumption 2:** This exchange is happening after authentification and the establishment of an encrypted session between a client and a server.

Upon group creation, a group key will be generated. As with the session key already utilized within our file system, the group key will be a 128-bit AES key using CBC mode. For each group, the Group Server will store this key. When a member is deleted from a group, an new key will be generated and stored. The keys will be stored in a list that is part of a Hashtable. Specifically, a Hashtable<groupName, ArrayList<Key> keyList>. The index of the key corresponds to it's freshness (i.e. it is monotonically increasing).

When a user logs in to the system, the current and past keys for each group that user is a member (that user's keychain) of will be avaliable. Before uploading a file to a file server, the user must encrypt the file using the current group key via the seperate application and send the encrypted file along with the version number of that key to the file server. Encrypted files will be wrapped in an object consisting of the index number of the key used (its version number), and the encrypted file.  When a user downloads a file from a file server, the client will decrypt the file (via the seperate application) using a version of the group key that was provided by the group server at login. 

### Justification
Files stored on file servers will now be encrypted with unique group keys. This way, if a file server leaks a file only members of the group will be able to decrypt it. Unauthorized parties will not be able to glean any information from the file without the key. Assuming that they save keys, previous members of the group will be able to decrypt leaked files that were created at the time that user was a member, but will not be able to decrypt any files created or modified after that user was removed. This security will not prevent new users joining the group from accessing old files as they have access to all versions of keys in their keychain. The Group Server will manage the group keys, but encryption and decryption will take place on the client side to keep the seperation of the group and file servers.

### Diagram
![alt text](T6.png)

# T7: Token Theft
In **T2** we dealt with the possible counterfeit of tokens. However, there is still an issue of tokens being stolen by the assumed untrusted file server and subsequently passed to other invalid users to be used in other servers. A single token should *only* belong to a single user. However, since we must accept that stolen tokens may be a possibility, these stolen tokens should *only* be operable within the server it was stolen by. If this is not the case, a single token can be stolen and subsequently used across servers to bypass other security mechanisms and allow users permissions that they are not supposed to have by posing as the owner of that token. 

### Mechanism
**Assumption:** Token objects will now accept a new parameter: intended file server IP address.

When a user requests a token from the group server, they will also be prompted to tell the group server which file server they intend to use that token on. We will include an extra String field in the token object for the file server's IP address. When a token's data is hashed and signed by the group server (as in the mechanisms for **T2**), that will include the file server's name. Note that the consequence of this security mechanism necessitates a user both connects to the group server and requests a new token each time that he or she wishes to connect to a different file server. When the user communicates with the file server, the server will verify both the correctness of the file server name and the signature from the appropriate group server before accepting the token as valid. If the file server sees a different IP address, we will terminate the connection because we know this token was not intended to be used on this server by the user and we can assume that the token has been stolen. This way, stolen tokens may only be used on the server where the theft took place.

### Justification
Requiring the file server to validate token usage intention information prevents inter-server use of a single token, as each token will be generated for one and only one specific file server. This file server IP address information can be easily added to the token data that is to be streamed into a byte array and hashed as part of our **T2** mechanism, requiring minimal additions to the codebase. The process of **T2** ensures that the token has not been tampered with since it was issued originally by the group server. 

### Diagrams
![alt text](T7.png)
![alt text](T7Token.png)

# Final Thoughts
We were able to make use of our previous protocols and extend then by adding extra information like timestamps, counters, CBC residue, and intended server to protect against the new threats mentioned above. The combination of timestamping, counters, and residue provides protection against replay attacks, reordering attacks, and modification attacks furthering the protection of a session key for each connection between a server a client. Specifying a file name where the token will be used prevents stolen tokens from being used at any server other than where the threat occured. Finally, files stored on file servers will now be encrypted with unique group keys to prevent unauthorized file leakage.
