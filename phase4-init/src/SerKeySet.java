import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SerKeySet implements java.io.Serializable {
	private static final long serialVersionUID = 4L;
    private SecretKey key;
    private byte[] iv;

    public SerKeySet(SecretKey key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    public SecretKey getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }
}