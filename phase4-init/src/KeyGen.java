import java.io.StringWriter;
import java.security.*;
import java.util.Scanner;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

public class KeyGen {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        PrivateKey rsaPrivate = null;
        PublicKey rsaPublic = null;
        try {
            KeyPairGenerator rsaGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            rsaGenerator.initialize(2048);
            KeyPair rsaKeys = rsaGenerator.generateKeyPair();
            rsaPrivate = rsaKeys.getPrivate();
            rsaPublic = rsaKeys.getPublic();

            if (rsaPrivate != null && rsaPublic != null) {
                System.out.println("RSA keys successfully generated.");
                System.out.println("\nPRIVATE: "
                        + new String(java.util.Base64.getEncoder().encode(rsaPrivate.getEncoded())));
                System.out.println("\nPUBLIC: "
                        + new String(java.util.Base64.getEncoder().encode(rsaPublic.getEncoded())));
                System.out.println("\nDO NOT GIVE YOUR PRIVATE KEY TO ANYONE!");
            }

            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
