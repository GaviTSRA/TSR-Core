package tsrcore;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.io.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Roles {
    private List<Role> roles;
    public String path;

    /**
     * <p>Create a new Roles object and load the data from the specified file</p>
     * @param path The file that stores the role data. Created and loaded automatically
     */
    public Roles(String path) {
        this.path = path;
        Fi dataFile = new Fi(path);
        if (!dataFile.exists()) {
            File file = new File(path);
            try {
                file.createNewFile();
                ObjectMap<String, String> default_role = new ObjectMap<>();
                default_role.put("default.id", "0");
                default_role.put("default.permissionLevel", "0");
                default_role.put("default.name", "Player");
                default_role.put("default.admin", "false");
                PropertiesUtils.store(default_role, dataFile.writer(false), "See below for a default role. You need to add your own here!");
            } catch(IOException err) {
                Log.err("Error creating roles file", err);
            }
        }
        load();
    }

    /**
     * Loads the roles data
     */
    public void load() {
        Fi dataFile = new Fi(path);
        roles = new ArrayList<>();

        ObjectMap<String, String> data = new ObjectMap<>();
        ObjectMap<String, String> ids = new ObjectMap<>();
        ObjectMap<String, String> permissionLevels = new ObjectMap<>();
        ObjectMap<String, String> names = new ObjectMap<>();
        ObjectMap<String, String> admin = new ObjectMap<>();

        PropertiesUtils.load(data, dataFile.reader());

        data.forEach(entry -> {
            List<String> parts = Arrays.asList(entry.key.split("\\."));
            if (Objects.equals(parts.get(1), "id")) ids.put(parts.get(0), entry.value);
            if (Objects.equals(parts.get(1), "permissionLevel")) permissionLevels.put(parts.get(0), entry.value);
            if (Objects.equals(parts.get(1), "name")) names.put(parts.get(0), entry.value);
            if (Objects.equals(parts.get(1), "admin")) admin.put(parts.get(0), entry.value);
        });

        ids.forEach(entry -> {
            if (!permissionLevels.containsKey(entry.key)) Log.err("Role with id " + entry.value + " is missing a permission level");
            else if (!names.containsKey(entry.key)) Log.err("Role with id " + entry.value + " is missing a name");
            else if (!admin.containsKey(entry.key)) Log.err("Role with id " + entry.value + " is missing an admin status");
            else {
                Role role = new Role(Integer.parseInt(entry.value), Integer.parseInt(permissionLevels.get(entry.key)),
                        names.get(entry.key), Boolean.parseBoolean(admin.get(entry.key)));
                roles.add(role);
            }
        });
    }

    /**
     * <p>Get a {@link Role} by the id</p>
     * @param id The id of the {@link Role}
     * @return The {@link Role}
     */
    public Role get(int id) {
        for (Role role : roles) {
            if (role.id == id) return role;
        }
        return null;
    }

    /**
     * Get all the roles
     * @return All loaded roles
     */
    public List<Role> all() {
        return roles;
    }
}
