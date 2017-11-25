import javax.crypto.SecretKey;
import java.util.ArrayList;

public class KeyChain {
    private ArrayList<SecretKey> keychain;
	private String groupName;
    
    public KeyChain(String groupName) {
        this.groupName = groupName;
        this.keychain = new ArrayList<SecretKey>();
    }
	
	// Add newly generated key to the group keychain
    public void addNewKey(SecretKey key){
    	keychain.add(key);
    }
    
    // Return the group name of the keychain
    public String getGroup(){
    	return groupName;
    }
    
    // Return the last (freshest) key that will be used for encryption
    public SecretKey getEncryptionKey() {
        return keychain.get(keychain.size() - 1);
    }

	// Returns the key for encryption, based on the version number
    public SecretKey getDecryptionKey(int version) {
        return keychain.get(version);
    }
}