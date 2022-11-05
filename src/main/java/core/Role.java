package core;

public class Role {
    int id;
    int permissionLevel;
    String name;
    boolean admin;

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
}
