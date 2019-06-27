package UI.Server;

import Services.Core.Extensions.LocalizableExtension;
import Services.Core.Networking.ServerDataTransfer;
import Services.ScreenRecord.ScreenCreator;
import rx.subjects.PublishSubject;

class ServerScreenModel {

    private final ServerDataTransfer dataTransfer;

     ServerScreenModel() {
         ScreenCreator creator = new ScreenCreator();
         dataTransfer = new ServerDataTransfer(creator.getBufferedImagePublishSubject());
    }

    PublishSubject<String> receiveMessagePublishSubject() {
         return dataTransfer.subscribeOnReceiveMessagePublishSubject();
    }

    void sendMessage(String text) {
         dataTransfer.sendMessage(text);
    }

    String localizeTitle() {
        return LocalizableExtension.getBrowserLocalizer().getLocalizedText("server_title");
    }



}
