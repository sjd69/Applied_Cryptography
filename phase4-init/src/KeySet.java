import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class KeySet {
    private SecretKey key;
    private IvParameterSpec iv;

    public KeySet(SecretKey key, IvParameterSpec iv) {
        this.key = key;
        this.iv = iv;
    }

    public SecretKey getKey() {
        return key;
    }

    public IvParameterSpec getIv() {
        return iv;
    }
}
