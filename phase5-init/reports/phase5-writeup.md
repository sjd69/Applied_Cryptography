# Phase 5: 

## Threat Model
**T8** Deletion of files and users by the server.

## Attacks
### Description
Without intervention, a malicious or corrupted server can wipe or modify all of its files. While users require special permissions to perform deletion or modification operations, there is no such restriction placed upon the server itself. Similarly, a group server that is compromised could delete all groups and/or users.

### Evidence
### Justification of Attack
### Diagrams

## Counter Measure
### Description
Similarly to our usage of counters for **T5**, we will utilize counters for files, users, and groups on each of their respective servers. These counters will be an integer count of the total number of files, users, and groups on their servers. We will utilize a timer of 2 minutes to periodically check the current count. If a change in the count is detected, we will check to see if a deletion operation was recently ordered by a user with the correct privileges. If such an order is not detected, we will assume that the server has been compromised and shut it down. 

### Justification
Our previous security mechanisms have all dealt with the possibility of malicious users, however, this counter measure also defends against the possibility of malicious servers. With the combination of our timer and file/user/group countrs, we can determine relatively quickly if files are being tampered with and shut down the connection before long-term damage is done to the contents of the servers, providing extra Integrity protection to our servers.

### Diagrams
