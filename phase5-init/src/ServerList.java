import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Hashtable;


public class ServerList implements java.io.Serializable {

    private static final long serialVersionUID = 7529994446280881724L;
    private Hashtable<String, Server> list = new Hashtable<String, Server>();

    public synchronized void addServer(String ip, PublicKey publicKey) {
        list.put(ip, new Server(publicKey));
    }

    public synchronized void deleteServer(String ip) {
        list.remove(ip);
    }

    public synchronized boolean checkServer(String ip) {
        return list.containsKey(ip);
    }

    public synchronized Server getServer(String ip) {
        return list.get(ip);
    }

    public synchronized PublicKey getPublicKey(String ip) {
        return list.get(ip).getPublicKey();
    }

    public synchronized void updateMessageNumber(String ip, int messageNumber) {
        list.get(ip).setMessageNumber(messageNumber);
    }

    public synchronized int getMessageNumber(String ip) {
        return list.get(ip).getMessageNumber();
    }

    class Server implements java.io.Serializable {
        private static final long serialVersionUID = 4115471873380081485L;
        private PublicKey publicKey;
        private int messageNumber;

        public Server(PublicKey publicKey) {
            setPublicKey(publicKey);
        }
        public PublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(PublicKey newPublicKey) {
            this.publicKey = newPublicKey;
        }

        public int getMessageNumber() {
            return messageNumber;
        }

        public void setMessageNumber(int messageNumber) {
            this.messageNumber = messageNumber;
        }
    }
} 
