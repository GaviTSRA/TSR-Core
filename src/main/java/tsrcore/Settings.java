package tsrcore;

public class Settings extends DataStorage {
    /**
     * <p>Create a new data storage object.</p>
     * <p>The file is automatically created if it is not present.</p>
     * <p>The data is loaded automatically.</p>
     *
     * @param path The path of the settings file
     */
    public Settings(String path) {
        super(path);
    }

    /**
     * Register a setting, creating it with the default value if it isn't already present
     * @param settingName The name of the setting
     * @param defaultValue The default value of the setting
     */
    public void register(String settingName, String defaultValue) {
        if (this.getString(settingName) == null) {
            this.set(settingName, defaultValue);
        }
    }

    /**
     * Register a setting, creating it with the default value if it isn't already present
     * @param settingName The name of the setting
     * @param defaultValue The default value of the setting
     */
    public void register(String settingName, int defaultValue) {
        if (this.getInt(settingName) == null) {
            this.set(settingName, defaultValue);
        }
    }

    /**
     * Register a setting, creating it with the default value if it isn't already present
     * @param settingName The name of the setting
     * @param defaultValue The default value of the setting
     */
    public void register(String settingName, boolean defaultValue) {
        if (this.getString(settingName) == null) {
            this.set(settingName, defaultValue);
        }
    }

    /**
     * Register a setting, creating it with the default value if it isn't already present
     * @param settingName The name of the setting
     * @param defaultValue The default value of the setting
     */
    public void register(String settingName, double defaultValue) {
        if (this.getString(settingName) == null) {
            this.set(settingName, defaultValue);
        }
    }
}
