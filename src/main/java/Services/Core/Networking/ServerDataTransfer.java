package Services.Core.Networking;

import Services.Core.Encryption.Encryption;
import Services.Core.ExtractProperties;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import rx.subjects.PublishSubject;

import java.io.IOException;


public class ServerDataTransfer {

    private int connectionID = -1;

    private final PublishSubject<java.awt.image.BufferedImage> sendBufferedImagePublishSubject;

    private final PublishSubject<String> sendMessagePublishSubject = PublishSubject.create();
    private final PublishSubject<String> receiveMessagePublishSubject = PublishSubject.create();

    private final Encryption encryption = new Encryption();

    public ServerDataTransfer(PublishSubject<java.awt.image.BufferedImage> sendBufferedImagePublishSubject) {
        this.sendBufferedImagePublishSubject = sendBufferedImagePublishSubject;

        Server server = new Server();
        kryoRegisterSetup(server);
        server.start();

        try {
            ExtractProperties properties = new ExtractProperties();
            server.bind(properties.getPortForTCP(), properties.getPortForUDP());

            listenersSetup(server);
            subscribesSetup(server);

        } catch (IOException e) {
            server.close();
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        sendMessagePublishSubject.onNext(message);
    }

    public PublishSubject<String> subscribeOnReceiveMessagePublishSubject() {
        return receiveMessagePublishSubject;
    }

    private void listenersSetup(Server server) {
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (connectionID == -1) connectionID = connection.getID();

                if (connectionID == connection.getID()) {

                    if (object instanceof SendReceiveObjects.Message) {
                        SendReceiveObjects.Message message = (SendReceiveObjects.Message)object;
                        receiveMessagePublishSubject.onNext(encryption.readMessage(message.encryptMessage));

                    } else if (object instanceof SendReceiveObjects.RSAPublicKey) {
                        SendReceiveObjects.RSAPublicKey publicKey = (SendReceiveObjects.RSAPublicKey)object;
                        encryption.initServerEncryption(publicKey.encodedPublicKey);

                        SendReceiveObjects.KeySpec keySpec = new SendReceiveObjects.KeySpec();
                        keySpec.keySpec = encryption.serverEncryption.DESKeyInRSACrypt();
                        connection.sendTCP(keySpec);

                    }
                }
            }
        });
    }

    private void subscribesSetup(Server server) {

        sendMessagePublishSubject.subscribe(message -> {
            SendReceiveObjects.Message receiveMessage = new SendReceiveObjects.Message();
            receiveMessage.encryptMessage = encryption.writeMessage(message);
            server.sendToTCP(connectionID, receiveMessage);
        });

        sendBufferedImagePublishSubject.subscribe(bufferedImage -> {
            SendReceiveObjects.BufferedImage receiveBufferedImage = new SendReceiveObjects.BufferedImage();
            receiveBufferedImage.bufferedImage = bufferedImage;
            server.sendToUDP(connectionID, receiveBufferedImage);
        });
    }

    private void kryoRegisterSetup(Server server) {
        Kryo kryo = server.getKryo();
        kryo.register(SendReceiveObjects.Message.class);
        kryo.register(SendReceiveObjects.BufferedImage.class);
        kryo.register(SendReceiveObjects.RSAPublicKey.class);
        kryo.register(SendReceiveObjects.KeySpec.class);
        kryo.register(byte[].class);
    }
}