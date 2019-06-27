package UI.Client;

import Services.Core.Extensions.LocalizableExtension;
import Services.Core.Networking.ClientDataTransfer;
import rx.subjects.PublishSubject;

class ClientScreenModel {

    private final ClientDataTransfer dataTransfer;

    ClientScreenModel(String ipAddress) {
        dataTransfer = new ClientDataTransfer(ipAddress);
    }

    PublishSubject<String> receiveMessagePublishSubject() {
        return dataTransfer.subscribeOnReceiveMessagePublishSubject();
    }

    void sendMessage(String text) {
        dataTransfer.sendMessage(text);
    }

    String localizeTitle() {
        return LocalizableExtension.getBrowserLocalizer().getLocalizedText("client_title");
    }
}
