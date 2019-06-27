package Services.Core.Encryption;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class DESCrypt {

    final SecretKey key;
    final private Cipher cipher = Cipher.getInstance("DESede");

    DESCrypt(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.key = key;
    }

    DESCrypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
        key = KeyGenerator.getInstance("DESede").generateKey();
    }

    // Шифровка сообщения
    byte[] writeCryptMessage(String text) {
        byte[] oos = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            oos = cipher.doFinal(text.getBytes());
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return oos;
    }

    // Чтение зашифрованного сообщения
    String readCryptMessage(Object object) {
        String message = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            message = new String(cipher.doFinal((byte[]) object));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
}
