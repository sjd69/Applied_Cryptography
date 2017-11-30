import javax.crypto.SecretKey;
import java.util.ArrayList;
import javax.crypto.spec.IvParameterSpec;

public class KeyChain implements java.io.Serializable {
	private static final long serialVersionUID = 4L;
    private ArrayList<SerKeySet> keychain;
	private String groupName;
    
    
	public KeyChain(String groupName) {
        this.groupName = groupName;
        this.keychain = new ArrayList<SerKeySet>();
    }
	
	// Add newly generated key to the group keychain
    public void addNewKey(KeySet key){
    	SerKeySet sks = new SerKeySet(key.getKey(), key.getIv().getIV());
    	this.keychain.add(sks);
    }
    
    // Return the group name of the keychain
    public String getGroup(){
    	return groupName;
    }
    
    // Return the last (freshest) key that will be used for encryption
    public KeySet getEncryptionKey() {
        SerKeySet skc = keychain.get(keychain.size() - 1);
        KeySet ks = new KeySet(skc.getKey(), new IvParameterSpec(skc.getIv()));
        return ks;
    }
    
    // Return the index of last (freshest) key that will be used for encryption
    public int getEncryptionKeyInd() {
        return (keychain.size() - 1);
    }

	// Returns the key for encryption, based on the version number
    public KeySet getDecryptionKey(int version) {
        SerKeySet skcd = keychain.get(version);
        KeySet ksd = new KeySet(skcd.getKey(), new IvParameterSpec(skcd.getIv()));
        return ksd;
    }
}
