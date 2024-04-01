package tsrcore;

public class Commands extends DataStorage {
    /**
     * <p>Create a new commands object.</p>
     * <p>The file is automatically created if it is not present.</p>
     * <p>The data is loaded automatically.</p>
     * @param path The path of the command permissions file
     */
    public Commands(String path) {
        super(path);
    }

    /**
     * Set the permission level of a command
     * @param commandName The commands name
     * @param permissionLevel The level to set to
     */
    public void set(String commandName, int permissionLevel) {
        super.set(commandName, permissionLevel);
    }


    /**
     * <p>Get a string value from the settings</p>
     * @param commandName The name of the command
     * @return The permission level of the command or null if the command wasn't found
     */
    public int get(String commandName) {
        return super.getInt(commandName);
    }

    /**
     * <p>Get a string value from the settings</p>
     * @param commandName The name of the command
     * @param notFound The value to use if the command is not found. This is returned and saved
     * @return The permission level of the command
     */
    public int get(String commandName, int notFound) {
        return super.getInt(commandName, notFound);
    }

    /**
     * Register a command with its default permission level, creating the data if it doesn't already exist
     * @param commandName Name of the command
     * @param defaultPermissionLevel Default permission level of the command
     */
    public void register(String commandName, int defaultPermissionLevel) {
        if (this.get(commandName, -1) == -1) {
            this.set(commandName, defaultPermissionLevel);
        }
    }
}
