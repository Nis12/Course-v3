package Services.Core.Encryption;

import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class Encryption {

    private DESCrypt desCrypt;
    private RSACrypt rsaCrypt;

    public ClientEncryption clientEncryption;
    public ServerEncryption serverEncryption;

    // Шифровка сообщения
    public byte[] writeMessage(String text) throws NullPointerException {
        return desCrypt.writeCryptMessage(text);
    }

    // Дешифровка сообщения
    public String readMessage(Object object) {
        return desCrypt.readCryptMessage(object);
    }

    public void initClientEncryption() {
        clientEncryption = new ClientEncryption();
    }

    public void initServerEncryption(byte[] encodedPublicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            serverEncryption = new ServerEncryption(keyFactory.generatePublic(publicKeySpec));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            JOptionPane.showConfirmDialog(new JDialog(), "Error\n" + e.getMessage());
        }
    }

    public class ClientEncryption {

        private final KeyPair keyPair;

        ClientEncryption () {
            KeyPairGenerator keyGen = null;

            try {
                keyGen = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                JOptionPane.showConfirmDialog(new JDialog(), "Error\n" + e.getMessage());
            }

            Objects.requireNonNull(keyGen).initialize(1024, new SecureRandom());
            keyPair = keyGen.generateKeyPair();
            rsaCrypt = new RSACrypt(keyPair.getPublic(), keyPair.getPrivate());
        }

        public byte[] getKeyPairPublicKey() { return keyPair.getPublic().getEncoded(); }

        public void setSecretKeySpec(byte[] keySpec) {
            try {
                desCrypt = new DESCrypt(rsaCrypt.decrypt(keySpec));
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                JOptionPane.showConfirmDialog(new JDialog(), "Error\n" + e.getMessage());
            }
        }
    }

    public class ServerEncryption {
        ServerEncryption(PublicKey publicKey) {
            try {
                rsaCrypt = new RSACrypt(publicKey);
                desCrypt = new DESCrypt();
            } catch ( NoSuchPaddingException | NoSuchAlgorithmException e) {
                JOptionPane.showConfirmDialog(new JDialog(), "Error\n" + e.getMessage());
            }
        }

        public byte[] DESKeyInRSACrypt() {
            return rsaCrypt.encrypt(desCrypt.key);
        }
    }
}
