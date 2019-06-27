package Services.Core.Networking;

import Services.Core.Encryption.Encryption;
import Services.Core.ExtractProperties;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import rx.subjects.PublishSubject;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ServerDataTransfer {

    private int connectionID = -1;

    private final PublishSubject<BufferedImage> sendBufferedImagePublishSubject;

    private final PublishSubject<String> sendMessagePublishSubject = PublishSubject.create();
    private final PublishSubject<String> receiveMessagePublishSubject = PublishSubject.create();

    private final Encryption encryption = new Encryption();

    public ServerDataTransfer(PublishSubject<BufferedImage> sendBufferedImagePublishSubject) {
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
                if (connectionID == -1) {
                    connectionID = connection.getID();
                    connection.sendTCP("Connect");
                }
                if (connectionID == connection.getID()) {
                    if (object instanceof SendReciveObjects.ReceiveMessage) {
                        SendReciveObjects.ReceiveMessage receiveMessage = (SendReciveObjects.ReceiveMessage)object;
                        receiveMessagePublishSubject.onNext(encryption.readMessage(receiveMessage.encryptMessage));

                    } else if (object instanceof SendReciveObjects.RSAPublicKey) {
                        SendReciveObjects.RSAPublicKey publicKey = (SendReciveObjects.RSAPublicKey)object;
                        encryption.initServerEncryption(publicKey.publicKey);
                        SendReciveObjects.KeySpec keySpec = new SendReciveObjects.KeySpec();
                        keySpec.keySpec = encryption.serverEncryption.DESKeyInRSACrypt();
                        connection.sendTCP(keySpec);
                    }
                }
            }
        });
    }

    private void subscribesSetup(Server server) {

        sendMessagePublishSubject.subscribe(message -> {
            SendReciveObjects.ReceiveMessage receiveMessage = new SendReciveObjects.ReceiveMessage();
            receiveMessage.encryptMessage = encryption.writeMessage(message);
            server.sendToTCP(connectionID, receiveMessage);
        });

        sendBufferedImagePublishSubject.subscribe(bufferedImage -> {
            SendReciveObjects.ReceiveBufferedImage receiveBufferedImage = new SendReciveObjects.ReceiveBufferedImage();
            receiveBufferedImage.bufferedImage = bufferedImage;
            server.sendToUDP(connectionID, receiveBufferedImage);
        });
    }

    private void kryoRegisterSetup(Server server) {
        Kryo kryo = server.getKryo();
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
