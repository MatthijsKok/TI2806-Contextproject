package nl.tudelft.ewi.ds.bankver;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class Environment {
    private static Environment instance = null;

    private String bank;
    private String bankApiKey;
    private String bankUrl;

    public void setBank(@NonNull String bank) {
        this.bank = bank;
    }

    public void setBankApiKey(@NonNull String bankApiKey) {
        this.bankApiKey = bankApiKey;
    }

    public void setBankUrl(@NonNull String bankUrl) {
        this.bankUrl = bankUrl;
    }

    public String getBank() {
        return bank;
    }

    public String getBankApiKey() {
        return bankApiKey;
    }

    public String getBankUrl() {
        return bankUrl;
    }

    public static Environment loadDefaults(Resources resources, int resourceId) throws IOException {
        try (InputStream stream = resources.openRawResource(resourceId)) {
            return loadDefaults(stream);
        }
    }

    /**
     * Load the default environment from given resource
     *
     * @param stream stream
     * @return environment
     */
    public static Environment loadDefaults(@NonNull InputStream stream) throws IOException {
        try {
            Environment env = new Environment();
            XmlPullParser parser = Xml.newPullParser();
            String key = "", text = "";

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("value")) {
                            key = parser.getAttributeValue(null, "name");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        // Set key + value

                        if (key.equalsIgnoreCase("bank")) {
                            env.bank = text;
                        } else if (key.equalsIgnoreCase("bankUrl")) {
                            env.bankUrl = text;
                        } else if (key.equalsIgnoreCase("bankApiKey")) {
                            env.bankApiKey = text;
                        }

                        key = "";

                        break;

                    default:
                        break;
                }

                eventType = parser.next();
            }

            instance = env;
        } catch (XmlPullParserException e) {
            Log.e("XML", "Failed to load Environment from XML file");
        }

        return instance;
    }

    /**
     * Get the default environment. Can be null if not loaded
     * @return environment or null
     */
    public static Environment getDefaults() {
        return instance;
    }
}
