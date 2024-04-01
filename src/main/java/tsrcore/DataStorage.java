package tsrcore;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.io.PropertiesUtils;

import java.io.File;
import java.io.IOException;

public class DataStorage {
    private final ObjectMap<String, String> storage;
    private final Fi storageFile;

    /**
     * <p>Create a new data storage object.</p>
     * <p>The file is automatically created if it is not present.</p>
     * <p>The data is loaded automatically.</p>
     * @param path The path of the settings file
     */
    public DataStorage(String path) {
        storage = new ObjectMap<>();
        storageFile = new Fi(path);
        if (!storageFile.exists()) {
            File file = new File(path);
            try {
                boolean created = file.createNewFile();
                if (created)
                    Log.info("Created DataStorage file: " + path);
            } catch(IOException err) {
                Log.err("Error creating data storage file " + storageFile.name(), err);
            }
        }
        load();
    }

    /**
     * <p>Set a value using a key value pair</p>
     * @param key The name of the value to set
     * @param value The value to set it to
     */
    public void set(String key, String value) {
        storage.put(key, value);
        try {
            PropertiesUtils.store(storage, storageFile.writer(false), "");
        } catch (IOException err) {
            Log.err("Error saving data storage " + storageFile.name(), err);
        }
    }

    /**
     * <p>Set a value using a key value pair</p>
     * @param key The name of the value to set
     * @param value The value to set it to
     */
    public void set(String key, int value) {
        set(key, String.valueOf(value));
    }

    /**
     * <p>Set a value using a key value pair</p>
     * @param key The name of the value to set
     * @param value The value to set it to
     */
    public void set(String key, boolean value) {
        set(key, String.valueOf(value));
    }

    /**
     * <p>Set a value using a key value pair</p>
     * @param key The name of the value to set
     * @param value The value to set it to
     */
    public void set(String key, double value) {
        set(key, String.valueOf(value));
    }

    /**
     * <p>Reload the data from the file.</p>
     * <p>This is only required if the file was changed manually.</p>
     */
    public void load() {
        PropertiesUtils.load(storage, storageFile.reader());
    }

    /**
     * <p>Get a string value from the storage</p>
     * @param key The name of the value
     * @return The value of the key or null if the key wasn't present in the file.
     */
    public String getString(String key) {
        return storage.get(key);
    }

    /**
     * <p>Get a string value from the storage</p>
     * @param key The name of the value
     * @param notFound The value to use if the key is not found. This is returned and saved.
     * @return The value of the key or the not found value if the key wasn't present in the file.
     */
    public String getString(String key, String notFound) {
        String result = storage.get(key);
        if (result == null) {
            set(key, notFound);
            return notFound;
        }
        return result;
    }

    /**
     * <p>Get a int value from the storage</p>
     * @param key The name of the value
     * @return The value of the key or null if the key wasn't present in the file.
     */
    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    /**
     * <p>Get a int value from the storage</p>
     * @param key The name of the value
     * @param notFound The value to use if the key is not found. This is returned and saved.
     * @return The value of the key or the not found value if the key wasn't present in the file.
     */
    public int getInt(String key, int notFound) {
        return Integer.parseInt(getString(key, String.valueOf(notFound)));
    }

    /**
     * <p>Get a bool value from the storage</p>
     * @param key The name of the value
     * @return The value of the key or null if the key wasn't present in the file.
     */
    public boolean getBool(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    /**
     * <p>Get a bool value from the storage</p>
     * @param key The name of the value
     * @param notFound The value to use if the key is not found. This is returned and saved.
     * @return The value of the key or the not found value if the key wasn't present in the file.
     */
    public boolean getBool(String key, boolean notFound) {
        return Boolean.parseBoolean(getString(key, String.valueOf(notFound)));
    }

    /**
     * <p>Get a double value from the storage</p>
     * @param key The name of the value
     * @return The value of the key or null if the key wasn't present in the file.
     */
    public double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }

    /**
     * <p>Get a double value from the storage</p>
     * @param key The name of the value
     * @param notFound The value to use if the key is not found. This is returned and saved.
     * @return The value of the key or the not found value if the key wasn't present in the file.
     */
    public double getDouble(String key, double notFound) {
        return Double.parseDouble(getString(key, String.valueOf(notFound)));
    }
}
