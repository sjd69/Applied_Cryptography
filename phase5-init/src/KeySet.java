import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class KeySet implements java.io.Serializable {
    private static final long serialVersionUID = -4754402390251414076L;
    private SecretKey key;
    private byte[] iv;

    public KeySet(SecretKey key, IvParameterSpec iv) {
        this.key = key;
        this.iv = iv.getIV();
    }

    public SecretKey getKey() {
        return key;
    }

    public IvParameterSpec getIv() {
        return new IvParameterSpec(iv);
    }
}
