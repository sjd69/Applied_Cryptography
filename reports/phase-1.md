### Group Information
Kadie Clancy (kdc42@pitt.edu, kadieclancy)

Ruth Zuckerman (ruz24@pitt.edu, NohrianScum)

Stephen Dowhy (sjd69@pitt.edu, )

### Security Requirements (Brainstorming)
* Anyone may create a new user profile. 
* Only the system administrator or the user himself may delete a user profile.
    * What if a user wants to delete his or her own user profile? Should this be allowed? Or should he or she have to send in a        request first?
* Users may create public and private groups.
* **Property 1: Creation.**
	* Creation states that a user *u* may create a group *g* and user *u* will be the only user who can read, modify, delete, or see files within group *g*. Without this requirement, any user could access files within the group upon creation, thereby undermining the notion of group-based file sharing.
* **Property 2: Selected Privacy.**
	* Selected Privacy states that files within group *g* can only be read, modified, deleted, or existance be known of by users who have been invited to group *g*. This requirement specifies the way in which groups are expanded and shared. Without it, any user would be able to access files within a group which is contrary to group-based file sharing.
* **Property 3: Selected Permissiveness.**
	* Permissive states that a user *u* may not read, modify, delete, or see a file *f* in group *g* if the user *u* is not given explicit permission to read, modify, delete, or see files within the group *g*. Without this requirement, a user who has been invited to a group would have complete access to any file within the group. This requirement allows more granularity for group-based file sharing.
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

* Server Signup: Registering a user profile requires a human verification. This is to try to avoid bots.
* Group Naming: Each group, regardless of private/public status, should have a unique name. This is to avoid confusion from users who probably don't want to join the wrong group by mistake. 
* Limit Invites: If users are allowed to send invitations to join group, allow only a limited number ( > 5) of invite applications per week per IP. A single user should not be able to spam the server with invites.
* File Size: Any file uploaded to the server should be less than... some prohibitive file size (I need to look up what this actually might be...). This is to stop malicious users from trying to upload something like a billion terabyte file.
* File Names, Part I: If a member of group g uploads a file and a member group h uploads a file with the same name, group h's file should not overwrite group g's file.
* File Names, Part II: If a member of a group uploads a file and another member of the same group uploads a file with the same name, the second user's file should not overwrite the first user's file.
* File Names, Part III: If a member of a group uploads a file and then later uploads another file with the same name, that user should be able to choose whether or not the original file is overwritten by the newly uploaded one.
* File Removal in Private Group: If a user in a private group wishes to have a file removed, and that file is not his or her own, then he or she must request such a removal from the group creator/manager. The creator/manager can choose to either accept or reject the request.
* File Removal in Public Group: If a user in a public group wishes to have a file removed, and that file is not his or her own, he or she can request such a removel from the System Administrator. 
* User Rights, Part I: Any user may choose to remove him or herself from a private group.
* User Rights, Part II: Any user may be allowed to be a member of more than one group at a time.
