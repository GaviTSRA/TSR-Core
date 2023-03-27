package core;

public class Commands extends DataStorage{
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
     * <p>Reload the data from the file.</p>
     * <p>This is only required if the file was changed manually.</p>
     */
    public void load() {
        super.load();
    }


    /**
     * Set the permission level of a command
     * @param commandName The commands name
     * @param permissionLevel The level to set to
     */
    public void set(String commandName, int permissionLevel) {
        super.setInt(commandName, permissionLevel);
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
}
