# Phase 5

## Threat Model
**T8 File Modification and Deletion** Since file servers are untrusted, files may not only be leaked from the server, files may be modified or deleted. A file server may alter or delete any file that it stores, at will.

## Attacks
### Description
Without intervention, a malicious or corrupted file server can wipe or modify any and all of the files it stores. While users require special permissions to perform deletion or modification operations, there is no such restriction placed upon the file server itself. At any time, the file server can tamper with any of the data it currently has stored. More alarmingly, group members will be ignorant to these malicious changes. This threat poses risk to data integrity as users who are expecting to recieve correct group files from a file server can possibly recieve maliciously modified files unknowingly. This threat also poses a risk to data avalaiablilty. Group members trying to access a valid group file will be denied access if the file server maliciously deleted it.

To carry out this attack, an adversay will first gain access to the file server (possibly just by being the user that started that file server), and navigate to the shared_files folder where the server files are stored. The adversary can then change or delete the files contained in this folder.

### Evidence and Justification of Attack
This attack is possible due to 1) the way in which the file server stores files, and 2) the fact that group servers and file servers are independent of one another. Since any user may start a file server, those users will in turn have access to whatever files are stored on that server. Although these files have been encrypted with a 128-bit AES key using CBC mode via the group keychain in the previous phase of the project, files may still be modified or deleted. Since group servers and file servers are independent of one another, there are no checks or restrictions placed on the actions of the file server. This allows the file server to not only have unrestricted access to whatever data is stored on it, but to also perform this malicious actions unknowingly to the user. 

If an attacker cannot gain the privileges necessary to perform mass deletions and wipes of data, the next step might be to corrupt the server itself to perform these actions automatically. If this can be accomplished, then potentially massive amounts of data can be tampered with or deleted within a very short timeframe, with minimal risk to the attacker.

## Counter Measure
### Description
Assumption 1: There exists a client-side file crypto application that encrypts and decrypts files using the group keys.

**Modification:**
In addition to encrypting files sent to the group server, users will also compute and send the HMAC-MD5 of the encrypted file to protect against file modification. When the user downloads a file from the system, the File Crypto client component will first check the hash value based on the keychain. If this value is incorrect, we assume file modification by the server and terminate the connection. If the hash value is correct, the File Crypto component with proceed with decrypting the file using the keychain.

Upon group creation when a group key is be generated, a second 128-bit AES CBC mode hash key will also be created. For each group, the Group Server will store this key. When a member is deleted from a group, two new keys will be generated and stored (one for encyrption and one for hashing). The keys will be stored in a list that is part of a Hashtable. Specifically, a Hashtable<groupName, ArrayList keyList>. The index of the key corresponds to its freshness (i.e. it is monotonically increasing). These group keychains will be stored, accessed, and updated in the same way as the previous implementation but now with the additon of a second hashing key.

**Deletion:**

### Justification
Our previous security mechanisms have all dealt with the possibility of malicious users, however, this counter measure also defends against the possibility of malicious servers. With the combination of our timer and file/user/group countrs, we can determine relatively quickly if files are being tampered with and shut down the connection before long-term damage is done to the contents of the servers, providing extra Integrity protection to our servers.

### Diagrams
