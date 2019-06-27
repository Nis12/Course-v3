package Services.Core.Networking;

import java.awt.image.BufferedImage;
import java.security.PublicKey;

class SendReciveObjects {

    static class SendMessage {
        byte[] encryptMessage;
    }
    static class ReceiveMessage {
        byte[] encryptMessage;
    }

    static class SendBufferedImage {
        BufferedImage bufferedImage;
    }
    static class ReceiveBufferedImage {
        BufferedImage bufferedImage;
    }

    static class RSAPublicKey {
        PublicKey publicKey;
    }
    static class KeySpec {
        byte[] keySpec;
    }
}
