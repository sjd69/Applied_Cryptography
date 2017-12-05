# Phase 5

## Threat Model
**T8 DDOS of Group Server** Users are not limited or restricted on the number of login requests that can be sent to the group server.

## Attacks
### Description
Users of the file sharing system are considered untrustworthy. In the current implementation, there is no limit to the number of times in which a user can be logged in to the group server or request to be logged in. Without intervention, a malicious user can send an unlimited number of login requests to the group server. An adversary has the ability to continuiously send requests to the group server causing it to slow or even crash and shut down. A flood of such requests has the potential to cause distributed denial-of-service to legitimate users who wish to interact with the file system.

To carry out this attack, an adversay will send a continuious stream of login requests to the group server. With enough requests, service to legitimate users will be disrupted.

### Evidence and Justification of Attack
This attack is possible due to 1) lack of restrictions on login requests, and 2) the fact that users may be logged in to the same account on the group server via any number of different clients. This attack compromises data avalibility as authorized users trying to access group files, for example, will be unable to. If an attack of this type is successful, then the entire file sharing system is disabled, as users must retrieve their user token from the gorup server to interact with any file server.

## Counter Measure
### Description
There is the possibility of two types of login requests that an adversary can flood the group server with: login requests for a legitimate user with the correct private key, and garbage login requests and garbage private keys.

**Restriction on Active User Accounts:**
A restriction on the number of different clients that may be logged in to the same user account will be restricted to one. When a user successfully logs in to the group server, no other client may also log in to that user account. Once a user is logged in, subsequent requests to login to that account will be blocked. This protects against the flood of legitimate user login requests.

**Computational Puzzle:**
When users attempt to log in, they will first be presented with a computational puzzle sent from the group server. The puzzle will be easy for a user to solve quickly and since legitimate users will be logging in infrequently, this will be minimally disruptive. 

Such a puzzle will limit the rate at which automated requests can be sent.

### Justification


### Diagrams
