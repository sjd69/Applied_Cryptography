import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Hashtable;


public class ServerList implements java.io.Serializable {

    private static final long serialVersionUID = 7529994446280881724L;
    private Hashtable<String, Server> list = new Hashtable<String, Server>();

    public synchronized void addServer(String ip, Server server) {
        list.put(ip, server);
    }

    public synchronized void deleteServer(String ip) {
        list.remove(ip);
    }

    public synchronized boolean checkServer(String ip) {
        return list.containsKey(ip);
    }

    public synchronized Server getPublicKey(String ip) {
        return list.get(ip);
    }

    public synchronized void updateMessageNumber(String ip, int messageNumber) {
        list.get(ip).setMessageNumber(messageNumber);
    }

    public synchronized int getMessageNumber(String ip, int messageNumber) {
        return list.get(ip).getMessageNumber();
    }

    class Server implements java.io.Serializable {
        private static final long serialVersionUID = 4115471873380081485L;
        private PublicKey publicKey;
        private int messageNumber;

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