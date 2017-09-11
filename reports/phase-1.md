### Group Information
Kadie Clancy (kdc42@pitt.edu, kadieclancy)

Ruth Zuckerman (ruz24@pitt.edu, NohrianScum)

Stephen Dowhy (sjd69@pitt.edu, sjd69)

### Section 1: Security Properties 
* **Property 1: Human Verification.**
	* Human Verification states that any user *u* who wishes to register an account *a* to the server *s* shall be required to pass a verification test on registration. This requirement is designed to prevent automated abuse.
* **Property 2: Login Authentication.**
	* Login Authentication states that if any user *u* attempts to log in to the server *s* from ip address *a1* when the user previously logged in from ip address *a2*, then that user shall be required to pass an individual verification test. This requirement is designed to discourage fraudulent logins and to ensure that only the user may log in to that user's account.
* **Property 3: User Generality.**
	* User Generality states that an account *a* represents a single user *u* and all roles and responsibilities that *u* may have. This requirement prevents a single user from requiring multiple accounts for each role that he or she has within the system.
* **Property 4: Account Deletion.**
	* Account Deletion states that any user *u* who has created no groups may delete his or her account at will. Any user who is the group creator *U* of any group *g* may submit an account deletion request to the System Administrator. Without this requirement, groups may inadvertently be deleted due to the deletion of a user account.
* **Property 5: Private Group Creation.**
	* Private Group Creation states that a user *u* may create a private group *g* and user *u* will be the only user who can read, modify, delete, or see files within group *g*. Without this requirement, any user could access files within the group upon creation, thereby undermining the notion of group-based file sharing.
* **Property 6: Public Group Creation.**
	* Public Group Creation states that a user *u* may create a public group *g* and user *u* will be the only user who can modify or delete files within group *g*. All other users will be able to see the existance of and read files within group *g*. Without this requirement, any user could access files within the group upon creation, thereby undermining the notion of group-based file sharing.
* **Property 7: Group Management.**
	* Group Management states that the user *u* who created the group *g* takes on the responsibility of group creator *U*, along with full permissions to the contents group. This requirement allows for the moderation and management of file content and user membership within individual groups.
* **Property 8: Group Deletion.**
	* Group Deletion states that deletion of any group *g* may only be performed by either the creator of that group *U* or the System Administrator *A*. Without this requirement, any user in a group could delete the group potentially on a whim. It also allows the System Administrator to take action in circumstances where the group creator cannot do so.
* **Property 9: Selected Privacy.**
	* Selected Privacy states that files within group *g* can only be read, modified, deleted, or existence be known of by users who have been invited and accepted the invitation to group *g*. This requirement specifies the way in which groups are expanded and shared. Without it, any user would be able to access files within a group which is contrary to group-based file sharing.
* **Property 10: Selected Permissiveness.**
	* Permissive states that a user *u* may not read, modify, delete, or see a file *f* in group *g* if the user *u* is not given explicit permission to read, modify, delete, or see files within the group *g*. Without this requirement, a user who has been invited to a group would have complete access to any file within the group. This requirement allows more granularity for group-based file sharing.
* **Property 11: Revoked Permissiveness.**
	* Revoked Permissiveness states that the group creator *U* may revoke any and all permissions of individual users *u* within said creator's group *g* up to and including the potential removal of the user from the group entirely. This requirement allows the group leader some degree of control over potential abusive users in his group. 
* **Property 12: Group Classification.**
	* Group Classification states that any group *g* shall be classified as either a public group *pug* or a private group *prg*. The group creator, *U* shall determine the classification of individual group *G* upon its creation.  Any user *u* within the server *s* shall be able to see all of server *s*' public groups *pug* and may join that group at will. Any user *u* will be unable to see any private groups *prg* unless that user is a member of that private group *prg* and may only be invited if the private group *prg*'s creatore *U* sends an invitation. With this requirement, some measure of privacy is guaranteed to users who wish to create and/or belong to more exclusive groups.
* **Property 13: Public Group Membership.**
	* Public Group Membership states that any user *u* may become a member of any public group *pug* without explicit permission from group creator *U*. The initial permission of members of *pug* consist of reads and downloads of any file *f* in *pug*. This requirement allows for file sharing of non-private content without intervention of *U*.
* **Property 14: Invitation Selectivity.**
	* Invitation Selectivity states that a user *u* who receives an invitation to a private group *prg* may either accept or reject that invitation. Accepting the invitation results in *u* becoming a member of *prg*. This requirement prevents users from becoming members of groups without their explicit permission.
* **Property 15: Group Uniqueness.**
	* Group Uniqueness states that any group *g* must have an individual group name *n* that may not be shared by any other group. This requirement allows groups to be easily distinguishable from one another for ease of use.
* **Property 16: User Memberships.**
	* User Memberships states that any user *u* may be a member of multiple groups at once. This requirement allows for all of the roles of a user to be accessed and utilized succinctly, as stated in Property 3: User Generality. 
* **Property 17: Membership Abandonment.**
	* Membership Abandonment states that a user *u* who is a member of a group *g* may choose to leave *g* at any time. Doing so will abandon the user's permissions within *g* and the user will no longer be a member of that group. Abandoning a group *g* will not erase a user's contributions to file content in *g*. This requirement allows users to detach from groups that are no longer necessary to their goals within the system.
* **Property 18: File Name Requirements.**
	* File Name Requirements state that all files *f* uploaded publically to the server *s* should not contain vulgar or offensive names. Files that do not follow this requirement are subject to removal by the System Administrator *A*. This requirement is meant to help maintain a more professional image of the server in general.
* **Property 19: File Size Requirements.**
	* File Size Requirements state that an uploaded file *f* must be of size 10gb or less. This requirement discourages the sending of overly large files. 
* **Property 20: Server File Uniqueness.**
	* File Uniqueness states that if a user *u1* of group *g1* uploads a file *f1*, and a user *u2* of group *g2* uploads a file *f2* with the same name, that *f2* should not overwrite *f1*. Likewise, if a user *u1* of group *g* uploads a file *f1*, and a user *u2* of the same group uploads a file *f2* with the same name, *f2* should not overwrite *f1*. this requirement ensures that users cannot accidentally save over each other's files by accident.
* **Property 21: Group File Uniqueness**
	* Group File Uniqueness states that if user *u1* of group *g* uploads a file *f1* and user *u2* of the same group uploads a file *f2* with the same name, *f2* should not overwrite *f1*. This requirement is similar to Property 12, but pertaining to two files within the same group.
* **Property 22: Individual File Uniqueness.** 
	* Individual File Uniqueness states that if a member of a group uploads a file and then later uploads another file with the same name, that user should be able to choose whether or not the original file is overwritten by the newly uploaded one. A user should be able to decide whether he would like to overwrite his own file.
* **Property 23: Private File Removal.**
	* Private File Removal states that if a user *u* in a private group *prg* wishes to have a file *f* removed, and if file *f* was not uploaded by that user, then that user must request such a removal from the group creator *U*. The creator *U* can choose to either accept or reject the request. This requirement is here in case of a user finding something potentially wrong with a file and would like to report it for removal. If it would break any file rules or seem otherwise suspicious, that user should report it.
* **Property 24: Public File Removal.**
	* Public File Removal states that if a user *u* in a public group *pug* wishes to have a file *f* removed, and if file *f* was not uploaded by that user, then that user must request such a removal from the System Administrator *A*. This requirement is similar to Property 24. However, because the removal of files in question is left in the review of the System Administrator and not a private group leader, any file will typically only be removed if it is violating file rules quite heavily.
* **Property 25: False-Positive Reporting.**
	* False-Positive Reporting states that if a user *u* in a group *g* consistently reports safe files as unsafe, that user may be subject to disciplinary action by the Group Creator *U* in case of a private group *prg* or temporarily being banned from the server by the System Administrator. This requirement is meant to discourage an overabundance of unnecessary file reporting.
* **Property 26: Invalid File Sharing.**
	* Invalid File Sharing states that if a user *u* consistently uploads files that are flagged for removal by other users *u* of server *s*, and those files are then removed by either the Group Creator *U* of a private group *prg* or the System Administrator, then that user *u* may be subject to removal from the server by the Administrator. This requirement is meant to remove anybody who consistently uploads invalid files.
* **Property 27: Initial Storage Size.**
	* Initial Storage Size states that each group *g* has its own individual storage size for uploaded files. This size may be chosen by the Group Creator *U* when first creating the group. This requirement allows each group creator some flexibility in choosing the right amount of storage size for his or her group.
* **Property 28: Increased Storage Size.**
	* increased Storage Size states that a Group Creator *U* may request additional storage space from the System Administrator. This requirement allows the Group Creator increased flexibility, in case more space was required than expected. 
* **Property 29: Server Size.**
	* Server Size states that there may only exist up to 1000 active, logged-in users on the server *s* at a time. Should an individual user *u* wish to log in after that number has been met, that user will have to wait until another user logs out. This requirement is meant to avoid overloading the server with activity. 
		* Is this number too specific? Too big or too small? Is it worth it to have a limit?
		* would this number be too small? especially for the educational threat model
* **Property 30: Server Maintenance.**
	* Server Maintenance states that a routine cleanup shall be performed on the server *s* once per month. At this time, any group *g* that has been inactive for 31 days shall be automatically deleted. This requirement is designed to avoid an overabundance of abandoned groups cluttering the server. 

### Section 2: Threat Models (WIP. To Build Upon)
* ~~**Company Wide Intranet (Not sure this fits well with the properties but seems like a logical threat model)**~~
	~~* The system will be deployed within an organization. All servers will only be accessible by connecting directly to the organizations network or through the organizations VPN. Each employee will have a single user account and will be part of a group that represents their team within the organization. Files that are pertinant to a team's project will be shared within that team's respective group on the file sharing service.~~
	
* **Educational**
	* System Model: Somewhat similar to the Company Wide Intranet, this system will be deployed within an educational institution, such as a college or university, to facilitate file sharing among students and teachers. All servers will only be accessible by connecting directly to the school's network through wired connection inside institution buildings. Files, typically projects and homeworks assignments, will be shared privately in individual class groups. Students will be able to upload any file from the teacher (Group Creator) but will be unable to see each other's files, to mitigate cheating. 
	* Trust Assumptions (based on the formatting required in the assignment? - let me know if this doesn't seem right): Within this educational system model, it is assumed that each student and instructor within the institution has a valid account. It is also assumed that accounts will expire once a user has left the institution. It is assumed that users will be instructed in some capacity outside the system to request membership to public groups or to send invitations to private groups. It is assumed that users will only request or accept memberships to groups that they should rightfully be apart of and will forfeit membership to groups that they are no longer valid to their purposes. 
	It is assumed that groups within the system represent valid groups within the institution. It is assumed that the System Administrator (along with members of the technical staff) has full permissions to all users, groups, and files within groups. It is assumed that servers in this model can not communicate with the Internet.
	* Related Properties:
		* Login: 
			* Property 1: Human Verification - Every user in the server should be a legitimate member of the school.
			* Property 2: Login Authentication - Every user should only log in through school network.
			* Property 3: User Generality - For example, a TA only requires one account for both the classes they TA for and classes in which they are enrolled as students.
		* Groups:
			* Property 5: Private Group Creation - Teachers will be the Group Creator *U* in this case.
			* Property 6: Public Group Creation - Public groups will include handouts and other class information.
			* Property 7: Group Management - The teacher will have full management rights over the group.
			* Property 8: Group Deletion - Group deletion should be performed after a semester has concluded.
			* Property 9: Selected Privacy - Students should only be able to access information pertinent to their
			class(es).
			* Property 10: Selected Permissiveness - Students may only be able to upload files, and only from the teacher, unless otherwise granted permission as part of projects.
			TAs may be given greater permissions than students.
				 * For example, students may only be able to read and download files from the teacher and uploaded files from the student will only be accessed by the teacher. This way, students may not see one another's submissions on assignments. TAs may have greater permission that students.
			* Property 11: Revoked Permissiveness - A student may be removed from the group if that student drops a course
			and forgets or otherwise does not leave the group.
			* Property 12: Group Classification - Public groups will be used for purposes like general class information, class notes, and handouts. Private 
			groups will be used for purposes like group projects and assignments.
			* Property 15: Group Uniqueness - Group Uniqueness will be vital for not getting groups confused!
			* Property 16: User Memberships - Students may be in multiple classes at once and teachers may instruct several courses at once.
			* Property 17: Membership Abandonment - A student may drop the course and wish to leave the group for it.
		* Files:
		* Other:
	



* **Public Facing**
	* System Model: The system will be deployed on publicly accessible web servers. Any individual regardless of location will be able to access the service and create a single user account for general file sharing purposes. The individual may create public and/or private groups to begin sharing files with that group. The individual will be able to invite other users to their group. The group creator will be able to grant permissions to modify and/or delete files to other users in the group as he sees fit.
	* Trust Assumptions (needs elaborated): Within this public facing model, it is assumed that the System Administrator (along with members of the technical staff) has full permissions to all users, groups, and files within groups. 
	* Related Properties:
		* Users
			* Property 1: Human Verification - Users of the system should be human.
			* Property 2: Login Authentication - Each user should only have access to his own individual account.
			* Property 3: User Generality - Each user should only have one account for all purposes in which he wishes to use the site.
			* Property 4: Account Deletion - Users may be able to delete their account so long as they are not managing any groups.
			* Property 16: User Membersips - Users may belong to multiple groups.
			* Property 17: Membership Abandonment - A user may leave a group at any time.
		* Groups
			* Property 5: Private Group Creation - 
			* Property 6: Public Group Creation - 
			* Property 7: Group Management - 
			* Property 8: Group Deletion - 
			* Property 9: Selected Privacy - 
			* Property 10: Selected Permissiveness - 
			* Property 11: Revoked Permissiveness - 
			* Property 12: Group Classification - 
			* Property 13: Public Group Membership - 
			* Property 14: Invitation Selectivity - 
			* Property 15: Group Uniqueness - 
			* Property 25: False-Positive Reporting - 
			* Property 26: Invalid File Sharing - 
		* Files
			* Property 18: File Name Requirements - 
			* Property 19: File Size Requirements - 
			* Property 20: Server File Uniqueness - 
			* Property 21: Group File Uniqueness - 
			* Property 22: Individual File Uniqueness - 
			* Property 23: Private File Removal - 
			* Property 24: Public File Removal - 
		* Other
			* Property 27: Initial Storage Size - 
			* Property 28: Increased Storage Size - 
			* Property 29: Server Size - 
			* Property 30: Server Maintenance - 

* **Business (initial idea)**
	* System Model: The system will be deployed within a business organization to facilitate file sharing among employees working on projects with varying degrees of confidentiality. Severs will operate on a subnet that can only be accessed through authorized wired connections inside organization buildings or through a VPN connection. Groups, which represent project teams within the organization, will be created by the project leader. Public and private groups with varying degrees of permissions for specific users within each group will be able to model the hierarchy of members of the project team in real 
	life giving them access to only what is needed for their responsibilities within the group. When a project reaches completion, the group for it will be deleted. New groups will be created as new projects are started within the organization. Users with multiple responsibilities within the organization may access all pertinent groups through a single account. 
	* Trust Assumptions: 
		* All users within the organization who work on projects have an account.
		* Individuals not within that organization will not be able to create an account or have access to they system.
	* Related Properties:
		
			
