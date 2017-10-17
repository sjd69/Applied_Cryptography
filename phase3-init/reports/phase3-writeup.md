# Cryptographic Mechanisms
After considering the types of threat models our file-sharing program will be faced with, we've decided to implement new security enhancements which are primarily based on two concepts: ease of use and coverage. Our mechanisms are designed to ensure that legitimate users may interact with the servers without any additional frustration due to increased security. Our user-facing password system will be simple and easily-accessible for a user while still being robust enough to protect against unauthorized token issuance, due to salting and hashing. We decided to utilize RSA encryption for many of our protection mechanisms. This is both to provide economy of mechanism for the codebase and also because RSA provides coverage for three of our four threat models. 


## T1: Unauthorized Token Issuance
The token stores all of a user's data. We do not want anybody that is not the owner of the token to be able to request the token since it could possibly reveal private or sensitive information about said user. On top of that, if somebody obtains a user's token they would be able to impersonate that user which would compromise groups, or even the server if it is an admin token.  

Example: Bob is an administrator on our file sharing service. Mallory requests Bob's token and subsequently wipes the service of users, groups, and files.  

### Mechanism
We will implement a password authentication protocol to authenticate users when requesting a token. The password will be a secret that only the user knows. Upon user creation, a user will be asked to create a password. Then the password will have a salt that is generated using java.security.SecureRandom appended to it. The password will then be hashed using sha256. The hash as well as the salt will be stored along with the user in UserList.bin. When a user requests a token they be prompted to enter a password. The password will be sent to the server and have the salt appended to it, then hashed, then checked against the stored hash. If the hashes match the token will be issued.   

### Justification  
We chose to use SHA256 instead of SHA1 due to SHA1 being considered broken. Since this is just a file sharing service, SHA256 seems more than adequate rather than going with a more heavy-handed SHA512. 

Using passwords gives the user a secret that only he knows to log in. Since only the hash and salt are stored on the server, thanks to pre-image resistance even if the database (UserList.bin in this case) compromised the password will still be sufficiently secure. To avoid monitoring of communcations channel compromising passwords since they are passed in plaintext, the security of this implementation relies on the assumption that **T4** is properly addressed, since that threat is out of scope. 


## T2: Token Modification/Forgery
If users can increase their own access rights at will, they can tamper with any file they wish. They could delete all the files on the server, or download files that aren't mean for them. Additionally, users who can counterfeit tokens could distribute them to whomever they wish, which takes away rights from the administrator. 

Once forged tokens come into existence, stopping distribution and use becomes more difficult. If there is no way to dinstiguish between a legitimate token and a forged one, innocent users may end up getting targeted as well.

### Mechanism
We will extend the UserToken interface to utilize RSA to both authenticate and exchange keys. We will generate a key pair, consisting of a public key and a private key, and then copy the public key to the server. Then we will generate a signature using the private key. The server can then verify that signature to validate the user's token. 

### Justification
With public-key authentication, signatures created by the user's private key cannot be forged by anybody who does not have the key. However, a third party who has the public key would be able to verify that a signature is valid. 

RSA in particular was chosen because it can be also be utilized for Threat Models 3 and 4 as well. 

## T3: Unauthorized File Servers

### Mechanism

### Justification

## T4: Information Leakage via Passive Monitoring
Since we must assume that all activity on the servers is being monitored by a passive observer, it is imperative to ensure that the observer cannot glean any useful information from any communication. Although the observer himself cannot act on the knowledge, there is nothing stopping him from brokering it. The act of snooping in and of itself is also a threat of disclosure which violates any users' confidentiality. It is additionally important that this threat model is properly dealt with, as other mechanisms will rely on this threat being neutralized to be effective.

### Mechanism
We will utilize public key cryptography, RSA in particular, to establish and exchange a session key. The client will initiate the connection to the server by sending a message indicating who they are, as well as a random number encrypted with the server's public key and the session key encrypted with the server's public key which is then signed by the user. The server decrypts the messages, and sends the first random number along with a second random number encrypted with the user's public key. The user then responds with the second random number. Now all messages can be exchanged using the secret session key. 

<object data="T4.pdf" type="application/pdf" width="700px" height="700px">
    <embed src="T4.pdf">
        This browser does not support PDFs. Please download the PDF to view it: <a href="http://yoursite.com/the.pdf">Download PDF</a>.</p>
    </embed>
</object>

### Justification
Using RSA to authenticate and exchange the symmetric session key allows us better performance than using just RSA. The session key will be a 128bit AES key since that is the biggest allowed by JavaCrypto. So long as we generate a sufficiently large "probably" number, our key exchange will be secure.