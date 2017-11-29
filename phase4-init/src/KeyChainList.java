import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This list represents the keychain for groups on the server
 */
public class KeyChainList implements java.io.Serializable {
    private static final long serialVersionUID = -3640596306597345734L;
    private Hashtable<String, KeyChain> list = new Hashtable<String, KeyChain>();

	// add or update keychain for a group
    public synchronized boolean addKeyChain(String groupName, KeyChain keychain) {
        this.list.put(groupName, keychain);
        return this.list.get(groupName) != null;
    }

	// delete keychain for a group
    public synchronized boolean deleteKeyChain(String groupName) {
        if (this.list.containsKey(groupName)) {
            this.list.remove(groupName);
            return true;
        } else {
            return false;
        }
    }

	// check if a keychain exists for a group
    public synchronized boolean checkKeyChain(String groupName) {
        return (this.list.containsKey(groupName));
    }
    
    // return a keychain for a group
    public synchronized KeyChain getKeyChain(String groupName) {
    	return (this.list.get(groupName));
    }

}
