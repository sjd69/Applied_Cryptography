# Cryptographic Mechanisms


# T5: Message Reorder, Replay or Modification

## Ideas
* Using AES with CBC mode: this prevents an adversary from generating a code book. This with the addtion of timestamps will protect from replay and reordering attacks.
* We can add on a timestamp to messages before encryption with symmetric key and not accept messages later than some certain threshold. This will prevent replay and reorder. If the timestamps of messages are not in chronological order, the connection will be terminated. If the timestamp of a message is not within the threshold, the connection will be terminated.
* Session keys make replay attacks beyond one session invalid.
* Modification of an encrypted message will render the decrypted message useless. When a message is unable to be decrypted/not decrypted to a valid command, the connection will be terminated. 

### Mechanism

### Justification

### Diagram(s)


# T6: File Leakage
## Ideas
* Threshold crypto: files on the server are encrypted with a unique threshold key for the group that it belongs to
* Make the threshold just 1.
* This way, everytime someone leaves a group, we can make their key become invalid, providing security as group memberships change
* Key can be managed by the group server and generated at the time someone is added to a group or removed from a group
* Can request key from group server when they want to decrypt a file and decryption of files on the client side can take place in a seperate applciation?
* even though individual keys change, the secret remains the same so new keys will be able to decrypt the old files?
### Mechanism

### Justification

### Diagram(s)
## Ideas
* When a user requests a token from the group server, they also tell the group server which file server they will use that token on
* The name of the file server will be signed along with the token itself by the group server 
* Every time a user wishes to connect to a new server they will need to request a new token
* When a file server recieves a token, it checks for its name. This is signed by the group server so we know it hasn't been tampered with
* If it sees a different name, terminate connection because we know this token was not intended to be used on this server by the user 
* This way, stolen tokens may only be used on the server where the theft took place

# T7: Token Theft
In T2 we dealt with the possible counterfeit of tokens. However, there is still an issue of tokens being stolen and subsequently used in other servers. A single token should *only* belong to a single user. However, since we must accept that stolen tokens may be a possibility, these tokens should *only* be operable within the server it was created inside. If this is not the case, a single token can be passed from hand to hand across servers to bypass other security mechanisms and allow users permissions that they are not supposed to have. 

### Mechanism
Individual Server authentication? 

### Justification

### Diagram(s)


# Final Thoughts
