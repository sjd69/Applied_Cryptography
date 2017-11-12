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

### Mechanism

### Justification

### Diagram(s)


# T7: Token Theft
In T2 we dealt with the possible counterfeit of tokens. However, there is still an issue of tokens being stolen and subsequently used in other servers. A single token should *only* belong to a single user. However, since we must accept that stolen tokens may be a possibility, these tokens should *only* be operable within the server it was created inside. If this is not the case, a single token can be passed from hand to hand across servers to bypass other security mechanisms and allow users permissions that they are not supposed to have. 

### Mechanism
Individual Server authentication? 

### Justification

### Diagram(s)


# Final Thoughts
