# Phase 5
We have added new mechanisms to deal with an additional threat model while keeping our main ideas of ease of use and coverage at the forefront.

## Threat Model
**T8 Group Server Denial of Service** Users are not limited or restricted on the number of login requests that can be sent to the group server.

## Attacks
### Description
Users of the file sharing system are considered untrustworthy. In the current implementation, there is no limit to the number of times in which a user can be logged in to the group server or request to be logged in. Without intervention, a malicious user can send an unlimited number of login requests to the group server. An adversary has the ability to continuously send requests to the group server causing it to slow or even crash and shut down. A flood of such requests has the potential to cause denial-of-service to legitimate users who wish to interact with the file system.

To carry out this attack, an adversary will send a continuous stream of login requests to the group server. With enough requests, service to legitimate users will be disrupted.

![alt text](T8Attack.png)

### Evidence and Justification of Attack
This attack is possible due to 1) lack of restrictions on login requests, and 2) the fact that users may be logged in to the same account on the group server via any number of different clients. This attack compromises data availability, as authorized users trying to access group files or other grouo data will be unable to. If an attack of this type is successful, then the entire file sharing system is disabled, as users must retrieve their user token from the group server to interact with any file server.

## Counter Measure
We will utilize challenge-response puzzles to protect against the group server denial of service attack.

### Description
**Hash Inversion Puzzle:**
When users attempt to log in, the group server will generate a cryptographic hash function for a puzzle of length 15. The server will transmit a message consisting of the puzzle state, length, and hash function to the user. The user must then invert the hash function and return a solution to the server, which will decrypt the token and verify the puzzle solution. 

To prevent a DoS attack flood of bad responses to the puzzle, after three failed attempts the requests from that IP address will be blocked by the Group Server for 5 minutes.

### Justification
A hash inversion puzzle will limit the rate at which automated requests can be sent. This countermeasure can be used to mitigate this DoS attack becuase clients are assumed to have approximately similar computational ability, and the puzzles are efficiently generated on the server end. After three failed attempts we are placing a 5 minute lockout which further rate limits automated login requests.

We decided to use a hash inversion puzzle in particular as it is more mathematically difficult to solve than a simple question-and-response puzzle. Puzzle state is offloaded to the user so that the server does not need to maintain vulnerable state in which it can be attacked while the user works on solving the puzzle. 

![alt text](T8RateLimiting.png)
![alt text](T8PuzzleDiagramLegit.png)
### Previous Handshake Diagram
![alt text](T5Handshakeup.png)

### Conclusion
The principles of our file sharing system have been ease of use and coverage since the first phase of implementation. The countermeasures described for this threat provide coverage against data avaliability attacks. The counter measures are also minimally disruptive to legitimate users, which extends our theme of ease of use. 
