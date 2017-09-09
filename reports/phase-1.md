### Group Information
Kadie Clancy (kdc42@pitt.edu, kadieclancy)

Ruth Zuckerman (ruz24@pitt.edu, NohrianScum)

Stephen Dowhy (sjd69@pitt.edu, sjd69)

### Security Requirements (Brainstorming) 
* Only the system administrator or the user himself may delete a user profile.
    * What if a user wants to delete his or her own user profile? Should this be allowed? Or should he or she have to send in a request first?
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
		* should users who have been invited to private groups, but not yet accepted the invitation be able to see files?
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

* Users may only access group content for groups which they are a member of.
* The existence of public groups is known to all users.
* The existence of private groups is only known to members of that group.
* Any user may become a member of a public group.
* Inital group membership of public groups is read/download only.
* The group creator/manager of a public group may not revoke membership to that group.
    * In the case of an abusive user in a public group, what can be done? Is it solely up to the System Administrator?
    	* In a public group, a user can initially only read files. The group creator can take away other permissions given but not the ability to see public groups
* The group creator/manager for private groups may send invitations for memebership to said group to users.
    * Can a user request to join a group without being invited first?
    	* Based on property 8: group classification, a user should not be able to know the existence of groups they are not a member of
* **Property 14: Invitation Selectivity.**
	* Invitation Selectivity states that a user *u* who receives an invitation to a private group *prg* may either accept or reject that invitation. Users may accept or reject invitations to private groups. Accepting the invitation results in *u* becoming a member of *prg*. This requirement prevents users from becoming members of groups without their explicit permission.
* **Property 15: Membership Abandonment.**
	* Membership Abandonment states that a user *u* who is a member of a group *g* may choose to leave *g* at any time. Doing so will abandon the user's permissions within *g* and the user will no longer be a member of that group. This requirement allows users to detach from groups that are no longer necessary to their goals within the system.
* User Rights, Part I: Any user may choose to remove him or herself from a private group.
 
* File Names, Part II: If a member of a group uploads a file and another member of the same group uploads a file with the same name, the second user's file should not overwrite the first user's file.
* File Names, Part III: If a member of a group uploads a file and then later uploads another file with the same name, that user should be able to choose whether or not the original file is overwritten by the newly uploaded one.
* File Removal in Private Group: If a user in a private group wishes to have a file removed, and that file is not his or her own, then he or she must request such a removal from the group creator/manager. The creator/manager can choose to either accept or reject the request.
* File Removal in Public Group: If a user in a public group wishes to have a file removed, and that file is not his or her own, he or she can request such a removel from the System Administrator. 

* **Property 17: User Generality.**
	* User Generality states that an account *a* represents a single user *u* and all roles and responsibilities that *u* may have. This requirement prevents a single user from requiring multiple accounts for each role that he or she has within the system.
* **Property 16: User Memberships.**
	* User Memberships states that any user *u* may be a member of multiple groups at once. This requirement allows for all of the roles of a user to be accessed and utilized succinctly, as stated in Property 17: User Generality. 
* User Rights, Part II: Any user may be allowed to be a member of more than one group at a time.
* **Property 18: Account Deletion.**
	* Account Deletion states that any user *u* who has created no groups may delete his or her account at will. Any user who is the group creator *U* of any group *g* may submit an account deletion request to the System Administrator. Without this requirement, groups may inadvertently be deleted due to the deletion of a user account.