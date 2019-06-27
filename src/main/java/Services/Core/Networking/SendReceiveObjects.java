package Services.Core.Networking;

class SendReceiveObjects {

    static class Message {
        byte[] encryptMessage;
    }

    static class BufferedImage {
        java.awt.image.BufferedImage bufferedImage;
    }

    static class RSAPublicKey {
        byte[] encodedPublicKey;
    }
    static class KeySpec {
        byte[] keySpec;
    }
}
