package net.sf.jabref.logic.l10n;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Localization {
    private static final Log LOGGER = LogFactory.getLog(Localization.class);

    private static final Locale defaultLocale = Locale.getDefault();

    public static final String RESOURCE_PREFIX = "l10n/JabRef";
    public static final String MENU_RESOURCE_PREFIX = "l10n/Menu";

    private static ResourceBundle messages;
    private static ResourceBundle menuTitles;

    public static void setLanguage(String language) {
        Locale locale = new Locale(language);

        try {
            messages = ResourceBundle.getBundle(RESOURCE_PREFIX, locale, new EncodingControl(StandardCharsets.UTF_8));
            menuTitles = ResourceBundle.getBundle(MENU_RESOURCE_PREFIX, locale,
                    new EncodingControl(StandardCharsets.UTF_8));

            // silent fallback to system locale when bundle is not found
            if(!messages.getLocale().equals(locale)) {
                LOGGER.warn("Bundle for locale <" + locale + "> not found. Falling back to system locale <" + defaultLocale + ">");
            }
        } catch(MissingResourceException e) {
            LOGGER.warn("Bundle for locale <" + locale + "> not found. Fallback to system locale <" + defaultLocale
                    + "> failed, using locale <en> instead", e);

            locale = new Locale("en");
            messages = ResourceBundle.getBundle(RESOURCE_PREFIX, locale, new EncodingControl(StandardCharsets.UTF_8));
            menuTitles = ResourceBundle.getBundle(MENU_RESOURCE_PREFIX, locale,
                    new EncodingControl(StandardCharsets.UTF_8));
        } finally {
            // Set consistent VM locales
            Locale.setDefault(locale);
            javax.swing.JComponent.setDefaultLocale(locale);
        }
    }

    /**
     * In the translation, %c is translated to ":", %e is translated to "=", %<anythingelse> to <anythingelse>, %0, ...
     * %9 to the respective params given
     *
     * @param resBundle the ResourceBundle to use
     * @param idForErrorMessage output when translation is not found ö * @param key the key to lookup in resBundle
     * @param params a list of Strings to replace %0, %1, ...
     * @return
     */
    private static String translate(ResourceBundle resBundle, String idForErrorMessage, String key, String... params) {
        String translation = null;
        try {
            if (resBundle != null) {
                translation = resBundle.getString(new TranslationKey(key).getPropertiesKey());
            }
        } catch (MissingResourceException ex) {
            LOGGER.warn("Warning: could not get " + idForErrorMessage + " translation for \"" + key + "\" for locale "
                    + Locale.getDefault());
        }
        if (translation == null) {
            LOGGER.warn("Warning: could not get " + idForErrorMessage + " translation for \"" + key + "\" for locale "
                    + Locale.getDefault());

            translation = key;
        }

        // replace %0, %1, ...
        if ((translation != null) && !translation.isEmpty()) {
            // also done if no params are given
            //  Then, %c is translated to ":", %e is translated to "=", ...
            new Translation(translation, params).translate();
        }
        return key;
    }

    public static class TranslationKey {

        private final String key;

        public TranslationKey(String key) {
            this.key = Objects.requireNonNull(key);
        }

        public String getPropertiesKey() {
            return this.key.replaceAll(" ", "_");
        }

        public String getHumanReadableKey() {
            return this.key.replaceAll("_", " ");
        }
    }

    public static class Translation {

        private final TranslationKey key;
        private final List<String> params;

        public Translation(String key, String... params) {
            this.key = new TranslationKey(key);
            this.params = Arrays.asList(params);
            if(this.params.size() > 10) {
                throw new IllegalStateException("Translations can only have at most 10 parameters");
            }
        }

        public String translate() {
            String translation = key.getHumanReadableKey();

            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                translation = translation.replaceAll("%" + i, param);
            }

            return translation;
        }

    }

    public static String lang(String key, String... params) {
        return translate(messages, "message", key, params);
    }

    public static String lang(String key) {
        return lang(key, (String[]) null);
    }

    public static String menuTitle(String key, String... params) {
        return translate(menuTitles, "menu item", key, params);
    }

    public static List<String> getMenuTitleKeys() {
        return new LinkedList<>(menuTitles.keySet());
    }
}

