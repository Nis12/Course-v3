package Services.Core.Networking;

import Services.Core.Encryption.Encryption;
import Services.Core.ExtractProperties;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import rx.subjects.PublishSubject;

import java.io.IOException;

public class ClientDataTransfer {

    private final PublishSubject<String> sendMessagePublishSubject = PublishSubject.create();
    private final PublishSubject<String> receiveMessagePublishSubject = PublishSubject.create();
    private final PublishSubject<java.awt.image.BufferedImage> receiveBufferedImagePublishSubject = PublishSubject.create();

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

            SendReceiveObjects.RSAPublicKey publicKey = new SendReceiveObjects.RSAPublicKey();
            publicKey.encodedPublicKey = encryption.clientEncryption.getKeyPairPublicKey();
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

    public PublishSubject<java.awt.image.BufferedImage> subscribeOnReceiveBufferedImagePublishSubject() {
        return receiveBufferedImagePublishSubject;
    }

    private void listenersSetup(Client client) {
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {

                if (object instanceof SendReceiveObjects.Message) {
                    SendReceiveObjects.Message message = (SendReceiveObjects.Message) object;
                    receiveMessagePublishSubject.onNext(encryption.readMessage(message.encryptMessage));

                } else if (object instanceof SendReceiveObjects.BufferedImage) {
                    SendReceiveObjects.BufferedImage bufferedImage = (SendReceiveObjects.BufferedImage) object;
                    receiveBufferedImagePublishSubject.onNext(bufferedImage.bufferedImage);

                } else if (object instanceof SendReceiveObjects.KeySpec) {
                    SendReceiveObjects.KeySpec secretKeySpec = (SendReceiveObjects.KeySpec) object;
                    encryption.clientEncryption.setSecretKeySpec(secretKeySpec.keySpec);

                }
            }
        });
    }

    private void subscribesSetup(Client client) {
        sendMessagePublishSubject.subscribe(message -> {
            SendReceiveObjects.Message sendMessage = new SendReceiveObjects.Message();
            sendMessage.encryptMessage = encryption.writeMessage(message);
            client.sendTCP(sendMessage);
        });
    }

    private void kryoRegisterSetup(Client client) {
        Kryo kryo = client.getKryo();
        kryo.register(SendReceiveObjects.Message.class);
        kryo.register(SendReceiveObjects.BufferedImage.class);
        kryo.register(SendReceiveObjects.RSAPublicKey.class);
        kryo.register(SendReceiveObjects.KeySpec.class);
        kryo.register(byte[].class);
    }
}

