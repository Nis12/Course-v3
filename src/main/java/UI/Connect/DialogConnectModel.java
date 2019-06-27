package UI.Connect;

import Services.Core.Extensions.LocalizableExtension;

class DialogConnectModel {

    String localizeTitle() {
        return LocalizableExtension.getBrowserLocalizer().getLocalizedText("connect_title");
    }

    String localizeIPAddressLabel() {
        return LocalizableExtension.getBrowserLocalizer().getLocalizedText("connect_ip_address_label");
    }
}
