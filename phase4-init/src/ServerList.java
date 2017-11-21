import java.security.PublicKey;
import java.util.Hashtable;


public class ServerList implements java.io.Serializable {

    private static final long serialVersionUID = 7529994446280881724L;
    private Hashtable<String, PublicKey> list = new Hashtable<String, PublicKey>();

    public synchronized void addServer(String ip, PublicKey publicKey) {
        list.put(ip, publicKey);
    }

    public synchronized void deleteServer(String ip) {
        list.remove(ip);
    }

    public synchronized boolean checkServer(String ip) {
        return list.containsKey(ip);
    }

    public synchronized PublicKey getPublicKey(String ip) {
        return list.get(ip);
    }




} 