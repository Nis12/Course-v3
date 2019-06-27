package Services.Core.Networking;

import Services.Core.Encryption.Encryption;
import Services.Core.ExtractProperties;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import rx.subjects.PublishSubject;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ClientDataTransfer {

    private final PublishSubject<String> sendMessagePublishSubject = PublishSubject.create();
    private final PublishSubject<String> receiveMessagePublishSubject = PublishSubject.create();
    private final PublishSubject<BufferedImage> receiveBufferedImagePublishSubject = PublishSubject.create();

    private final Encryption encryption = new Encryption();

    public ClientDataTransfer(String ipAddress) {

        encryption.initClientEncryption();

        Client client = new Client();
        kryoRegisterSetup(client);
        client.start();

        try {
            ExtractProperties properties = new ExtractProperties();

            client.connect(properties.getTimeoutConnectionMillis(),
                    ipAddress,
                    properties.getPortForTCP(),
                    properties.getPortForUDP());

            listenersSetup(client);
            subscribesSetup(client);

            SendReciveObjects.RSAPublicKey publicKey = new SendReciveObjects.RSAPublicKey();
            publicKey.publicKey = encryption.clientEncryption.getKeyPairPublicKey();
            client.sendTCP(publicKey);

        } catch (IOException e) {
            client.close();
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        sendMessagePublishSubject.onNext(message);
    }

    public PublishSubject<String> subscribeOnReceiveMessagePublishSubject() {
        return receiveMessagePublishSubject;
    }

    public PublishSubject<BufferedImage> subscribeOnReceiveBufferedImagePublishSubject() {
        return receiveBufferedImagePublishSubject;
    }

    private void listenersSetup(Client client) {
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {

                if (object instanceof SendReciveObjects.ReceiveMessage) {
                    SendReciveObjects.ReceiveMessage receiveMessage = (SendReciveObjects.ReceiveMessage) object;
                    receiveMessagePublishSubject.onNext(encryption.readMessage(receiveMessage.encryptMessage));

                } else if (object instanceof SendReciveObjects.ReceiveBufferedImage) {
                    SendReciveObjects.ReceiveBufferedImage receiveBufferedImage = (SendReciveObjects.ReceiveBufferedImage) object;
                    receiveBufferedImagePublishSubject.onNext(receiveBufferedImage.bufferedImage);

                } else if (object instanceof SendReciveObjects.KeySpec) {
                    SendReciveObjects.KeySpec secretKeySpec = (SendReciveObjects.KeySpec) object;
                    encryption.clientEncryption.setSecretKeySpec(secretKeySpec.keySpec);

                }
            }
        });
    }

    private void subscribesSetup(Client client) {
        sendMessagePublishSubject.subscribe(message -> {
            SendReciveObjects.SendMessage sendMessage = new SendReciveObjects.SendMessage();
            sendMessage.encryptMessage = encryption.writeMessage(message);
            client.sendTCP(sendMessage);
        });
    }

    private void kryoRegisterSetup(Client client) {
        Kryo kryo = client.getKryo();
        kryo.register(SendReciveObjects.SendMessage.class);
        kryo.register(SendReciveObjects.ReceiveMessage.class);
        kryo.register(SendReciveObjects.SendBufferedImage.class);
        kryo.register(SendReciveObjects.ReceiveBufferedImage.class);
        kryo.register(SendReciveObjects.RSAPublicKey.class);
        kryo.register(SendReciveObjects.KeySpec.class);

        kryo.register(sun.security.rsa.RSAPublicKeyImpl.class);
        kryo.register(sun.security.x509.AlgorithmId.class);
        kryo.register(sun.security.util.ObjectIdentifier.class);
        kryo.register(sun.security.util.BitArray.class);
        kryo.register(java.math.BigInteger.class);
        kryo.register(byte[].class);
    }
}

