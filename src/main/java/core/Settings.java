package core;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.io.PropertiesUtils;

import java.io.File;
import java.io.IOException;

public class Settings {
    private final ObjectMap<String, String> settings;
    private final Fi settingsFile;

    /**
     * <p>Create a new settings object.</p>
     * <p>The file is automatically created if it is not present.</p>
     * <p>The data is loaded automatically.</p>
     * @param path The path of the settings file
     */
    public Settings(String path) {
        settings = new ObjectMap<>();
        settingsFile = new Fi(path);
        if (!settingsFile.exists()) {
            File file = new File(path);
            try {
                file.createNewFile();
            } catch(IOException err) {
                Log.err("Error creating settings file", err);
            }
        }
        load();
    }

    /**
     * <p>Set a settings using a key value pair, where the value is a string.</p>
     * @param key The name of the setting to set
     * @param value The value to set it to
     */
    public void setString(String key, String value) {
        settings.put(key, value);
        try {
            PropertiesUtils.store(settings, settingsFile.writer(false), "");
        } catch (IOException err) {
            Log.err("Error saving settings", err);
        }
    }

    /**
     * <p>Set a settings using a key value pair, where the value is an int.</p>
     * @param key The name of the setting to set
     * @param value The value to set it to
     */
    public void setInt(String key, int value) {
        setString(key, String.valueOf(value));
    }

    /**
     * <p>Set a settings using a key value pair, where the value is a boolean.</p>
     * @param key The name of the setting to set
     * @param value The value to set it to
     */
    public void setBool(String key, boolean value) {
        setString(key, String.valueOf(value));
    }

    /**
     * <p>Reload the data from the file.</p>
     * <p>This is only required if the file was changed manually.</p>
     */
    public void load() {
        PropertiesUtils.load(settings, settingsFile.reader());
    }

    /**
     * <p>Get a string value from the settings</p>
     * @param key The name if the setting
     * @param notFound The value to use if the setting is not found. This is returned and saved.
     * @return The value of the setting or the not found value if the setting wasn't present in the file.
     */
    public String getString(String key, String notFound) {
        String result = settings.get(key);
        if (result == null) {
            setString(key, notFound);
            return notFound;
        }
        return result;
    }

    /**
     * <p>Get an int value from the settings</p>
     * @param key The name if the setting
     * @param notFound The value to use if the setting is not found. This is returned and saved.
     * @return The value of the setting or the not found value if the setting wasn't present in the file.
     */
    public int getInt(String key, int notFound) {
        return Integer.parseInt(getString(key, String.valueOf(notFound)));
    }

    /**
     * <p>Get a boolean value from the settings</p>
     * @param key The name if the setting
     * @param notFound The value to use if the setting is not found. This is returned and saved.
     * @return The value of the setting or the not found value if the setting wasn't present in the file.
     */
    public boolean getBool(String key, boolean notFound) {
        return Boolean.parseBoolean(getString(key, String.valueOf(notFound)));
    }
}
