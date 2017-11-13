# Cryptographic Mechanisms


# T5: Message Reorder, Replay or Modification
After connecting to and properly authenticating a group or file server, the messages passed between the client and server are subject to being reordered, saved for a replay attack or modified by an active adversary. Modifying messages can affect the integrity and avaliability of data stored on the server or recieved by the user. An adversary that may insert communications, for example delete a file from a server, can disrupt data avaliablility. Inserting fake communications can also copromise data integrity as malicious files may be placed onto the server or sent to the user. Reordering communications can also compromise data avaliablility and integrity. For example, a user wishes to download a file and then delete the file from a server. Reordering these messages deletes the file before the user can access it. 

To protect against **T4** in the previous phase of this project, our file sharing system uses a unique secret session key to encrypt communications between a server and a client. This mechanism offers protection against passive adversary, and protection from replay attacks between sessions. Our current implementation does not protect against an active attacker that can insert, reorder, replay or modify messages within a session. To offer protection against this stronger attack, we utilize timestamping in addition to the previously implemented session key.


### Mechanism
Our file sharing system utilizes a 128 bit AES (CBC mode) session key from the previous phase. The use of a session key makes replay attacks outside of that session useless, as a key is only valid for a single session between one user and one server for one connection. The use of CBC mode, as opposed to ECB mode, prevents an adversary from generating a code book to use for a replay attack within a session as CBC mode creates message dependence of cipher text.

The addition of timestamps to messages will further protect against replay and reordering attacks. Each communication will be timestamped with the time that the message is sent before encryption with the session key. When a party recieves a communication, the message will be decrypted and the timestamp will be validataed against some reasonable threshold of three minutes. If the timestamp does not fall within this threshold, we consider the message a replay attack and will terminate the connection. Time stamps also protect against reordering. If the timestamp of messages is recieved out of chronological order, we assume a reordering attack and terminate the connection. 

Encryption with our session key alerts of message modification. Since all messages are encrypted with a session key, a modification of this message will render the decrypted message useless. When a message is unable to be decrypted (or fully decrypted), or is not decrypted to a valid command, message modification is assumed and the connection will be terminated. 

### Justification
We utilize CBC as the mode of operation as CBC provides message dependence for generating cipher text unlike ECB mode, which is subject to code book attacks. Timestamps will be unique to each message and will be easily verifiable by both the client and the server. We consider the threshold of three minutes to be sufficient to prevent replay attacks without accidentally dirsupting normal useage of the file sharing system.

### Diagram(s)


# T6: File Leakage
## Ideas
* Threshold crypto: files on the server are encrypted with a unique threshold key for the group that it belongs to
* Make the threshold just 1.
* This way, everytime someone leaves a group, we can make their key become invalid, providing security as group memberships change
* Key can be managed by the group server and generated at the time someone is added to a group or removed from a group
* Can request key from group server when they want to decrypt a file and decryption of files on the client side can take place in a seperate applciation?
* even though individual keys change, the secret remains the same so new keys will be able to decrypt the old files?

Although we authenticate file servers in the previous phase of the project, we still assume that these servers may be malicious. Since we assume file servers to be untrusted, we assume that they may leak files stored on the server to unauthorized individuals at will. This disrupts data confidentiality of group files since users are under the assumption that only valid group members may have access to those files. 

### Mechanism

### Justification

### Diagram(s)

# T7: Token Theft
In T2 we dealt with the possible counterfeit of tokens. However, there is still an issue of tokens being stolen by the assumed untrusted file server and subsequently passed to other invalid users to be used in other servers. A single token should *only* belong to a single user. However, since we must accept that stolen tokens may be a possibility, these stolen tokens should *only* be operable within the server it was stolen by. If this is not the case, a single token can be stolen and subsequently used across servers to bypass other security mechanisms and allow users permissions that they are not supposed to have by posing as the owner of that token. 

### Mechanism
When a user requests a token from the group server, they will also be prompted to tell the group server which file server they intend to use that token on. We will include an extra String field in the token object for the file server's name. When a token's data is hashed and signed by the group server (as in the mechanisms for T2), that will include the file server's name. Note that the consequence of this security mechanism necessitates a user connect to the group server and requent a new token each time that he or she wishes to connect to a different file server. When the user communicates with the file server, the server will verify both the correctness of the file server name and the signature from the appropriate group server before accepting the token as valid. If the file server sees a different server name, we will terminate the connection because we know this token was not intended to be used on this server by the user and we can assume that the token has been stolen. This way, stolen tokens may only be used on the server where the theft took place.

### Justification
Requiring the file server to validate token usage intention information prevents inter-server use of a single token, as each token will be generated for one and only one specific file server. This file server name information can be easily added to the token data that is to be streamed into a byte array and hashed as part of our T2 mechanism, requiring a minimum of additions to the codebase. The process of T2 ensures that the token has not been tampered with since it was issued originally by the group server. 

### Diagram(s)


# Final Thoughts
