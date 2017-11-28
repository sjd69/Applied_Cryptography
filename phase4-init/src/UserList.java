/* This list represents the users on the server */

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Hashtable;


public class UserList implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7600343803563417992L;
    private Hashtable<String, User> list = new Hashtable<String, User>();

    public synchronized void addUser(String username, PublicKey publicKey) {
        User newUser = new User(publicKey);
        list.put(username, newUser);
    }

    public synchronized void deleteUser(String username) {
        list.remove(username);
    }

    public synchronized boolean checkUser(String username) {
        if (list.containsKey(username)) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized PublicKey getPublicKey(String username) {
        return list.get(username).getPublicKey();
    }

    public synchronized ArrayList<String> getUserGroups(String username) {
        return list.get(username).getGroups();
    }

    public synchronized ArrayList<String> getUserOwnership(String username) {
        return list.get(username).getOwnership();
    }

    public synchronized void addGroup(String user, String groupname) {
        list.get(user).addGroup(groupname);
    }

    public synchronized void removeGroup(String user, String groupname) {
        list.get(user).removeGroup(groupname);
    }

    public synchronized void addOwnership(String user, String groupname) {
        list.get(user).addOwnership(groupname);
    }

    public synchronized void removeOwnership(String user, String groupname) {
        list.get(user).removeOwnership(groupname);
    }


    class User implements java.io.Serializable {

        /**
         *
         */
        private static final long serialVersionUID = -6699986336399821598L;
        private ArrayList<String> groups;
        private ArrayList<String> ownership;
        private PublicKey publicKey;
        private int messageNumber;

        public User(PublicKey publicKey) {
            this.publicKey = publicKey;
            groups = new ArrayList<String>();
            ownership = new ArrayList<String>();
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(PublicKey newPublicKey) {
            this.publicKey = newPublicKey;
        }

        public ArrayList<String> getGroups() {
            return groups;
        }

        public ArrayList<String> getOwnership() {
            return ownership;
        }

        public void addGroup(String group) {
            groups.add(group);
        }

        public void removeGroup(String group) {
            if (!groups.isEmpty()) {
                if (groups.contains(group)) {
                    groups.remove(groups.indexOf(group));
                }
            }
        }

        public void addOwnership(String group) {
            ownership.add(group);
        }

        public void removeOwnership(String group) {
            if (!ownership.isEmpty()) {
                if (ownership.contains(group)) {
                    ownership.remove(ownership.indexOf(group));
                }
            }
        }

        public int getMessageNumber() {
            return messageNumber;
        }

        public void setMessageNumber(int messageNumber) {
            this.messageNumber = messageNumber;
        }

    }

} 
