import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This list represents the groups on the server
 */
public class GroupList implements java.io.Serializable {
    private static final long serialVersionUID = -3640596306597345734L;
    private Hashtable<String, Group> list = new Hashtable<String, Group>();

    public synchronized boolean addGroup(String groupName) {
        Group newGroup = new Group();
        return (this.list.put(groupName, newGroup) != null);
    }

    public synchronized boolean deleteGroup(String groupName) {
        return (this.list.remove(groupName) != null);
    }

    public synchronized boolean checkGroup(String username) {
        return (this.list.containsKey(username));
    }

    public synchronized ArrayList<String> getUsers(String groupName) {
        return (this.list.get(groupName).getUsers());
    }

    public synchronized ArrayList<String> getOwnership(String groupName) {
        return (this.list.get(groupName).getOwnership());
    }

    class Group implements java.io.Serializable {
        private static final long serialVersionUID = 5533721546234191063L;

        private ArrayList<String> users;
        private ArrayList<String> ownership;

        public Group() {
            this.users = new ArrayList<String>();
            this.ownership = new ArrayList<String>();
        }

        public ArrayList<String> getUsers() {
            return this.users;
        }

        public ArrayList<String> getOwnership() {
            return this.ownership;
        }

        public boolean addUser(String username) {
            return this.users.add(username);
        }

        public boolean removeUser(String username) {
            return this.users.remove(username);
        }

        public boolean addOwnership(String username) {
            return this.ownership.add(username);
        }

        public boolean removeOwnership(String username) {
            return this.ownership.remove(username);
        }
    }
}
