package core;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.io.PropertiesUtils;

import java.io.File;
import java.io.IOException;

public class Permissions {
    private final ObjectMap<String, String> permissions;
    private final Fi permissionsFile;

    /**
    <p>Creates a new permissions object.</p>
    <p>If the specified file does not exist, the file and the directories are created automatically.</p>
    <p>Automatically loads the data from the file.</p>
    @param path The path to the permissions file, including the file itself
     */
    public Permissions(String path) {
        permissions = new ObjectMap<>();
        permissionsFile = new Fi(path);
        if (!permissionsFile.exists()) {
            permissionsFile.mkdirs();
            File file = new File(path);
            try {
                file.createNewFile();
            } catch(IOException err) {
                System.out.println("Error creating permissions file: ");
                System.out.println(err.getLocalizedMessage());
            }
        }
        load();
    }

    /**
     * <p>Sets the permission level of a uuid.</p>
     * <p>The file is saved automatically.</p>
     * @param uuid The uuid of the player to set the permissions of
     * @param permissionLevel The permission level to set
     */
    public void set(String uuid, int permissionLevel) {
        permissions.put(uuid, String.valueOf(permissionLevel));
        try {
            PropertiesUtils.store(permissions, permissionsFile.writer(false), "");
        } catch (IOException ex) {
            System.out.println("Error saving permissions: ");
            System.out.println(ex.getLocalizedMessage());
        }
    }

    /**
     * <p>Load the data from the file.</p>
     * <p>This only needs to be called if the file was manually changed.</p>
     */
    public void load() {
        PropertiesUtils.load(permissions, permissionsFile.reader());
    }

    /**
     * <p>Get the permission level of a uuid.</p>
     * @param uuid The uuid to get the permission level of
     * @param notFound If the uuid is not found in the data this value is returned and saved
     * @return The permission level of the uuid, or the not found value if the uuid wasn't present
     */
    public int get(String uuid, int notFound) {
        String result = permissions.get(uuid);
        if (result == null) {
            set(uuid, notFound);
            return notFound;
        }
        return Integer.parseInt(result);
    }
}
