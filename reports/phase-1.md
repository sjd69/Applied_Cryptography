### Group Information
Kadie Clancy (kdc42@pitt.edu, kadieclancy)

Ruth Zuckerman (ruz24@pitt.edu, NohrianScum)

Stephen Dowhy (sjd69@pitt.edu, )

### Security Requirements (Brainstorming)
* Anyone may create a new user profile. 
* Only the system administrator or the user himself may delete a user profile.
    * What if a user wants to delete his or her own user profile? Should this be allowed? Or should he or she have to send in a        request first?
* Users may create public and private groups.
* **Property 1: Human Verification.**
	* Human Verification states that any user *u* who wishes to register an account *a* to the server *s* shall be required to pass a verification test on registration. This requirement is designed to prevent automated abuse.
* **Property 2: Login Authentication.**
	* Login Authentication states that if any user *u* attempts to log in to the server *s* from ip address *a1* when the user previously logged in from ip address *a2*, then that user shall be required to pass an individual verification test. This requirement is designed to discourage fraudulent logins and to ensure that only the user may log in to that user's account.
* **Property 3: Creation.**
	* Creation states that a user *u* may create a group *g* and user *u* will be the only user who can read, modify, delete, or see files within group *g*. Without this requirement, any user could access files within the group upon creation, thereby undermining the notion of group-based file sharing.
* **Property 4: Deletion.**
	* Deletion states that deletion of any group *g* may only be performed by either the creator of that group *U* or the System Administrator *A*. Without this requirement, any user in a group could delete the group potentially on a whim. It also allows the System Administrator to take action in circumstances where the group creator cannot do so.
* **Property 5: Selected Privacy.**
	* Selected Privacy states that files within group *g* can only be read, modified, deleted, or existance be known of by users who have been invited to group *g*. This requirement specifies the way in which groups are expanded and shared. Without it, any user would be able to access files within a group which is contrary to group-based file sharing.
* **Property 6: Selected Permissiveness.**
	* Permissive states that a user *u* may not read, modify, delete, or see a file *f* in group *g* if the user *u* is not given explicit permission to read, modify, delete, or see files within the group *g*. Without this requirement, a user who has been invited to a group would have complete access to any file within the group. This requirement allows more granularity for group-based file sharing.
* **Property 7: Revoked Permissiveness.**
	* Revoked Permissiveness states that the group creator *U* may revoke any and all permissions of individual users *u* within said creator's group *g* up to and including the potential removal of the user from the group entirely. This requirement allows the group leader some degree of control over potential abusive users in his group. 
* **Property 8: Group Classification.**
	* Group Classification states that any group *g* shall be classified as either a public group *pug* or a private group *prg*. The group creator, *U* shall determine the classification of individual group *G* upon its creation.  Any user *u* within the server *s* shall be able to see all of server *s*' public groups *pug* and may request an invitation from any such group from the group's individual creator *U*. Any user *u* will be unable to see any private groups *prg* unless that user is a member of that private group *prg* and may only be invited if the private group *prg*'s creatore *U* sends an invitation. With this requirement, some measure of privacy is guaranteed to users who wish to create and/or belong to more exclusive groups.
* **Property 9: Request Limitation.**
	* Request Limitation states that any user *u* shall be allowed to send at most 1 request to join a public group *g* at a time. This requirement is designed to discourage users from spamming the server with excess requests.
* **Property 10: Group Uniqueness.**
	* Group Uniqueness states that any group *g* must have an individual group name *n* that may not be shared by any other group. This requirement allows groups to be easily distinguishable from one another for ease of use.
* **Property 11: File Size Requirements.**
	* File Size Requirements state that an uploaded file *f* must be of size 10gb or less. This requirement discourages the sending of overly large files. 
* **Property 12: File Uniqueness.**
	* File Uniqueness states that if a user *u1* of group *g1* uploads a file *f1*, and a user *u2* of group *g2* uploads a file *f2* with the same name, that *f2* should not overwrite *f1*. Likewise, if a user *u1* of group *g* uploads a file *f1*, and a user *u2* of the same group uploads a file *f2* with the same name, *f2* should not overwrite *f1*. this requirement ensures that users cannot accidentally save over each other's files by accident.
* **Property 13: Server Maintenance.**
	* Server Maintenance states that a routine cleanup shall be performed on the server *s* once per month. At this time, any group *g* that has been inactive for 31 days shall be automatically deleted. This requirement is designed to avoid an overabundance of abandoned groups cluttering the server. 
* Each public or private group has a group creator/manager who is the user who created said group.
* The group creator/manager has full permissions to that group (upload/overwrite/delete, read/download).
* Users may only access group content for groups which they are a member of.
* The existence of public groups is known to all users.
* The existence of private groups is only known to members of that group.
* Any user may become a member of a public group.
* Inital group membership of public groups is read/download only.
* Group creator/manager may elevate permissions.
* The group creator/manager may revoke write/modify permissions of memebers.
* The group creator/manager of a public group may not revoke membership to that group.
    * In the case of an abusive user in a public group, what can be done? Is it solely up to the System Administrator?
* The group creator/manager for private groups may send invitations for memebership to said group to users.
    * Can a user request to join a group without being invited first?
* Users may accept or reject invitations to private groups.
* The group creator/manager of a private group may revoke membership of that group.
* Write/Modify/Delete are a package permission.
* Read/Download are a package permission.
 
* File Names, Part II: If a member of a group uploads a file and another member of the same group uploads a file with the same name, the second user's file should not overwrite the first user's file.
* File Names, Part III: If a member of a group uploads a file and then later uploads another file with the same name, that user should be able to choose whether or not the original file is overwritten by the newly uploaded one.
* File Removal in Private Group: If a user in a private group wishes to have a file removed, and that file is not his or her own, then he or she must request such a removal from the group creator/manager. The creator/manager can choose to either accept or reject the request.
* File Removal in Public Group: If a user in a public group wishes to have a file removed, and that file is not his or her own, he or she can request such a removel from the System Administrator. 
* User Rights, Part I: Any user may choose to remove him or herself from a private group.
* User Rights, Part II: Any user may be allowed to be a member of more than one group at a time.
