import javax.crypto.SecretKey;
import java.util.ArrayList;

public class KeyChain implements java.io.Serializable {
	private static final long serialVersionUID = 4L;
    private ArrayList<KeySet> keychain;
	private String groupName;
    
    public KeyChain(String groupName) {
        this.groupName = groupName;
        this.keychain = new ArrayList<KeySet>();
    }
	
	// Add newly generated key to the group keychain
    public void addNewKey(KeySet key){
    	this.keychain.add(key);
    }
    
    // Return the group name of the keychain
    public String getGroup(){
    	return groupName;
    }
    
    // Return the last (freshest) key that will be used for encryption
    public KeySet getEncryptionKey() {
        return keychain.get(keychain.size() - 1);
    }
    
    // Return the index of last (freshest) key that will be used for encryption
    public int getEncryptionKeyInd() {
        return (keychain.size() - 1);
    }

	// Returns the key for encryption, based on the version number
    public KeySet getDecryptionKey(int version) {
        return keychain.get(version);
    }
}
