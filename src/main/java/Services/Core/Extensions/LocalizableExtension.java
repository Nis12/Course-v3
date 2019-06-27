package Services.Core.Extensions;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizableExtension {

    private final String CURRENT_BUNDLE_LANG;

    private LocalizableExtension(Locale locale) {
        final String RESOURCE_BUNDLE_RU = "dictionary.ru_ru";
        final String RESOURCE_BUNDLE_EN = "dictionary.en_en";

        if (locale.getLanguage().equals("ru")) CURRENT_BUNDLE_LANG = RESOURCE_BUNDLE_RU;
        else CURRENT_BUNDLE_LANG = RESOURCE_BUNDLE_EN;
    }

    public String getLocalizedText(String key)
    {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(CURRENT_BUNDLE_LANG);

            if (bundle.keySet().contains(key)) return bundle.getString(key);
            else return key + "(No localization entry found)";
        }
        catch (Exception e) {
            return "LOCALIZATION FAILED: " + e.toString();
        }
    }

    public static LocalizableExtension getBrowserLocalizer() {
        return new LocalizableExtension(LocaleContextHolder.getLocale());
    }
}

