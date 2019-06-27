package Services.Core.Encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;

class RSACrypt {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    RSACrypt(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    RSACrypt(PublicKey publicKey) {
        this.publicKey = publicKey;
        this.privateKey = null;
    }

    byte[] encrypt(SecretKey text) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = cipher.doFinal(text.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    SecretKey decrypt(byte[] text) {
        byte[] decryptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedText = cipher.doFinal(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assert decryptedText != null;
        return new SecretKeySpec(decryptedText, 0, decryptedText.length, "DESede");
    }
}
