package tsrcore;

import arc.util.Log;

public class Role {
    public int id;
    public int permissionLevel;
    public String name;
    public boolean admin;

    /**
     * <p>Creates a new role from the given parameters.</p>
     * <p>This should probably never be used since roles are generated automatically from the role file</p>
     * @param id The id of the role
     * @param permissionLevel The permission level of the role
     * @param name The display name of the role
     * @param admin Whether the role should have the admin role
     */
    public Role(int id, int permissionLevel, String name, boolean admin) {
        this.id = id;
        this.permissionLevel = permissionLevel;
        this.name = name;
        this.admin = admin;
    }

    /**
     * Whether a command is usable by this rule
     * @param commandName The name of the command
     * @param commands The commands data
     * @return Whether the command can be used
     */
    public boolean canUseCommand(String commandName, Commands commands) {
        int result = commands.get(commandName, 9999);
        if (result == 9999) {
            Log.err("Command not registered: " + commandName);
        }
        return result <= permissionLevel;
    }
}
