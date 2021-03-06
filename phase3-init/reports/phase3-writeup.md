# Cryptographic Mechanisms
After considering the types of threat models our file-sharing program will be faced with, we've decided to implement new security enhancements which are primarily based on two concepts: ease of use and coverage. Our mechanisms are designed to ensure that legitimate users may interact with the servers without any additional frustration due to increased security. Our user-facing public key system will be simple and easily-accessible for a user while still being robust enough to protect against unauthorized token issuance. We decided to utilize RSA encryption for many of our protection mechanisms. This is both to provide economy of mechanism for the codebase and also because RSA provides coverage for three of our four threat models. 

Our implementation utilizes the following mechanisms:
* SHA256: We utilize SHA256 for hashing within our system. We chose to use SHA256 instead of SHA1 due to SHA1 being considered broken. Since this is just a file sharing service, SHA256 seems more than adequate rather than going with a more heavy-handed SHA512.
* RSA: Our implementation uses 2048 bit RSA keys for public key encryption. 1024 RSA keys are considered dead. 2048 bit keys are still considered secure and provide us with performance and storage benefits over 4096 bit keys, while still supplying us with sufficient security. In addition, 4096 bit RSA keys have potential compatibility concerns with older hardware, we would like to reach the widest audience possible while still providing adequate security.
* Random Number Challenge (Nonce): The size of all random challenges utilized are 256 bits. This size is sufficently large to protect against brute force random guessing of the challenge by an adversary. Random challenges will not be reused.
* AES: For symmetric key encryption within our system (for session keys), we will utilize AES with a 128 bit key size. We chose 128 bit keys over 256 keys as 128 bit keys are significantly faster but still sufficent security-wise and a 128-bit is the largest allowed by JavaCrypto. We utilize CBC as the mode of operation as CBC provides message dependence for generating cipher text unlike ECB mode, which is subject to code book attacks. 
* MD5: We utilize MD5 within our system for generating public key fingerprints. MD5 produces human readible fingerprints, which is necessary for our purposes. MD5 is also used for SSH host verification, which is why we chose it compared to SHA1.


## T1: Unauthorized Token Issuance
NOTE: **T1** and **T4** are heavily intertwined. If something may be unclear here it is may be expound upon in **T4**.

The token stores all of a user's data and since clients are assumed to be untrusted, our system must need to protect against illegitimate clients requesting tokens. We do not want a user who is not the owner of the token to be able to request the token since it could possibly reveal private or sensitive information about said user. Further, if someone obtains a user's token they would be able to impersonate that user which would compromise groups, or even the entire server if an Admin token is obtained.  

Example: Bob is an administrator on our file sharing service. Mallory requests Bob's token and subsequently wipes the service of users, groups, and files.  

### Mechanism
ASSUMPTION: **T3** is properly implemented and public keys are exchanged prior to the start of this exchange. We also assume that the session key exchange is implemented properly according to **T4**.

We utilize public key cryptography, RSA in particular, to establish and exchange a session key. Upon account creation, the user's public key is stored as part of the serialized object in userlist.bin. The client will initiate the connection to the group server by sending a message indicating who they are, as well as a nonce encrypted with the server's public key (and an encrypted session key, protocol in **T4**). The server decrypts the message and sends the first nonce along with a second nonce encrypted with the user's public key. The user then responds with the second nonce as well as a session key that is signed by the user. Both the server and the user are now mutually authenticated and can communicate with the shared session key. The token may now be encrypted using the shared session key and sent to the user.

![alt text](T4diagram2.png)

### Justification
NOTE: Our system depends on the assumption that a seperate KeyGeneration application is built solely to generate key pairs. This application allows users to generate the key pair and give the public key to the administrator for account creation outside the system. Justification for the session key and using RSA over Diffie-Hellman are expound upon in **T4.** 

Using RSA to authenticate provides us better, more robust security compared to the alternative standard password-based system. In addition, this protocol was already being utilized in T4 to establish and securely exchange a session key. Since this protocol provides mutual authentication, it makes sense to simplify user interaction and not require a redundant password login.

## T2: Token Modification/Forgery
Assumption: Assume that **T4** is correctly implemented, and that this message will be encrypted.

If users can increase their own access rights at will, they can tamper with any file they wish. They could delete all the files on the server, or download files that aren't meant for them. Additionally, users who can counterfeit tokens could distribute them to whomever they wish, which takes away rights from the administrator. Once forged tokens come into existence, stopping distribution and usage becomes more difficult. If there is no way to dinstiguish between a legitimate token and a forged one, innocent users may end up getting targeted as well.

### Mechanism
We utilize RSA to both authenticate and exchange keys. The group server will generate a key pair, consisting of a public key and a private key, for the itself. Key generation for user accounts will occur at the time of account creation by use of a seperate KeyGeneration application. This key pair generation occurs prior to the creation an account by an Admin. The user "hands" the Admin their public key for account creation, again outside the file sharing system.

When the server creates a token, it will first stream the (comma-delimited) issuer, subject, and group information into a byte array. The standard order is illustrated in the diagram below. We will then utilize SHA256 to make a hash of the byte array. The result of that hash will be signed by the server using its private key. The server will then send the  hash of token information signed with the server's private key to the user, along with the token itself. The user may verify the signature and the hash of the token data to validate the user's token. Again, this message will be encrypted with the session key under the assumption that **T4** is correctly implemented.  

### Justification
With public-key authentication, signatures created by the user's private key cannot be forged by anybody who does not have the key. However, a third party who has the public key would be able to verify that a signature is valid. This ensures that forged tokens will not be accepted, as a third party would be able to verify if the signature is valid or not. RSA in particular was chosen because it can be also be utilized for other mechanisms within our system, providing coverage and economy of mechanism. Signing the hash value ensures that the hash value of the token has not been modified since it was sent directly from the server. 

Rather than sign the token as it is, we've decided to instead send a signed hash of the token's data. We made this choice for a few reasons. First, the size of the token in comparison to the key size could cause issues like loss of data. Second, we are concerned with the token's relevant data rather than the Java object itself. A hash of the streamed byte array allows a check of token based on data rather than object implementation, which may differ among systems. The unmodified hash value can be compared to the hash of the token itself. If the hash values do not match, it is know that the token has been modified and should not be used. 

The token's issuer, subject, and group information will be delimited with a "," prior to hashing to provide collision resistance, as it prevents you from encoding arbitrary data structures within a group. 

![alt text](TokenDiag.png)
![alt text](T2diagram2.png)

## T3: Unauthorized File Servers
Since we may only assume that file servers are entirely trustworthy after they have been properly authenticated, we must ensure that if a user wishes to contact a server, that they actually connect to that server and not some other server that is potentially malicious. Since any user may run a file server, the group server may not be required to know about all file servers and thus may not be used in user authentification of a file server. 

Connecting to a malicious server while the user is under the impression that they are connected to the intended server brings about a number of problematic situations. For example, if Bob connects to a malicious server posing as the server Bob intended to access, the malicious server now has direct access to any file that Bob uploads which is a breach of data confidentiality. The malicious server can also breach data integrity, if Bob requests a file the server can return any information that it so chooses to pose as that file. Finally, this situation threatens data avaliablitity if Bob requests an avaliable file and the malicious server doesn't return the file.

### Mechanism
When a file server is created, the server will generate a public and private RSA key pair. When a user attempts to connect to a server,  the user first requests the server's public key. The server will reply with its public key. The user will compute the public key fingerprint based on the server reply that the user will manually check. The server's public key and IP will be stored on the client, and if there is a mismatch or the server's information does not yet exist on the client (first connection), the user will be prompted to enter the server's name, fingerprint, and IP, and asked if they would like to connect to this server at their own risk. If the user accepts, the server's information is stored (name, IP, public key) on the client. When a user attempts to authenticate a file server, the user will generate and encrypt a random challenge with the server's public key. The file server will be able to decrypt the challenge with its private key. The server then sends back that random challenge to the user, authenticating itself to the user.

### Justification
By encrypting a random challenge with a server's public key, only the owner of that private key will be able to decrypt. Since we are assuming the storage of server public keys is trustworthy due to the assumption that adversaries are only passive, only the intended server will have the matching private key. This authenticates that file server the user interacts with is the intended file server. This uses less client CPU time than the Diffie-Hellman algorithm specified as part of the core SSH protocol (RFC 4432). Since one of the core concepts of our system is ease of use, we allow the user to authenticate servers quickly. RSA is also convenient as it is utilized in other ways by the system. Past random challenges will be recorded in a bin file to prevent against replay attacks to ensure that old challenges are not accepted or used. Our mechanism was inspired by the SSH host verification scheme. 

RFC 4432 - http://www.ietf.org/rfc/rfc4432.txt

SSH Host Key Checking - https://www.ibm.com/support/knowledgecenter/SSLTBW_2.3.0/com.ibm.zos.v2r3.foto100/hostch.htm

![alt text](T3diagramNew.png)

## T4: Information Leakage via Passive Monitoring
Since we must assume that all activity on the servers is being monitored by a passive observer, it is imperative to ensure that the observer cannot glean any useful information from any communication. Although the observer himself cannot act on the knowledge, there is nothing stopping him from brokering it. The act of snooping in and of itself is also a threat of disclosure which violates any users' confidentiality. It is additionally important that this threat model is properly dealt with, as other mechanisms will rely on this threat being neutralized to be effective.

![alt text](T4diagram.png)
### Mechanism
Mutual authentication and setup is identical to that in **T1**. All communication after the setup described in **T1** will take place utilizing an AES session key with a 128 bit key size and CBC mode of operation.

The client will initiate the connection to the file server. After authentication of the file server as described in **T3**, the user will send a shared session key, K, encrypted with the file server's public key. The file server will decrypt K using the server's private key. The user and server now share a secret session key. All further messages will be encrypted with the session key before being exhanged.

### Justification
Using RSA to authenticate and exchange the symmetric session key allows us better performance than using just RSA. The session key will be a 128 bit AES key since that is the biggest allowed by JavaCrypto, and AES is essentially the de facto standard for symmetric key cryptography and provides us with sufficient security. So long as we generate a sufficiently large "probably" prime number, our key exchange will be secure. A Diffie-Hellman exchange would also allow us to exchange a session key, but RSA seems easier to implement overall since it can be used in a variety of use cases in our system. 

## Final Thoughts
To account for ease of use and coverage, our security enhancements rely mainly on an RSA encryption protocol and shared symmetric session keys. The interplay of our mechanisms in response to the specific threats work well together, as they all have an underlying dependence on RSA public key encryption. The security mechanisms specifically address user legitimacy for interaction with servers without an excess of user burder for system interactions. Our mechanisms also ensure that untrusted users may not elevate access rights or attempt to create false tokens. File servers are authenticated by the user before interaction and communications between client and server are protected from passive adversaries.
