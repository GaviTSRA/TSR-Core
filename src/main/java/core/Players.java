package core;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.io.PropertiesUtils;
import mindustry.gen.Player;

import java.io.File;
import java.io.IOException;

public class Players {
    private final TSRCore core;
    private final Fi playerRolesFile;
    private final ObjectMap<String, String> playerRoleIDs;
    private final ObjectMap<String, Role> players;

    /**
     * <p>Create a new class to store all the players in the game and their roles.</p>
     * <p>The file is automatically created if it doesn't exist.</p>
     * <p>The data is automatically loaded.</p>
     * @param core An instance of the TSRCore class
     * @param playerRolesFilePath The path to the file storing the player roles
     */
    public Players(TSRCore core, String playerRolesFilePath) {
        this.core = core;
        playerRoleIDs = new ObjectMap<>();
        players = new ObjectMap<>();
        playerRolesFile = new Fi(playerRolesFilePath);
        if (!playerRolesFile.exists()) {
            File file = new File(playerRolesFilePath);
            try {
                file.createNewFile();
            } catch(IOException err) {
                Log.err("Error creating player roles file", err);
            }
        }
        load();
    }

    /**
     * <p>Load the player ids data from the file.</p>
     * <p>This is done automatically and only needs to be called when the file was manually changed</p>
     */
    public void load() {
        PropertiesUtils.load(playerRoleIDs, playerRolesFile.reader());
    }

    /**
     * <p>Add a player to the online players list.</p>
     * <p>This should be handled by the TSR Core in most cases.</p>
     * @param player The player to add
     * @param defaultRoleID The default role id
     */
    public void add(Player player, int defaultRoleID) {
        String id = playerRoleIDs.get(player.uuid());
        if (id == null) {
            set(player, defaultRoleID);
            id = String.valueOf(defaultRoleID);
        }
        Role role = core.roles.get(Integer.parseInt(id));
        players.put(player.uuid(), role);
        if (role.admin) player.admin = true;
    }

    /**
     * <p>Remove the player from the online players.</p>
     * <p>This should be handled by the TSR Core in most cases.</p>
     * @param player The player to remove
     */
    public void remove(Player player) {
        players.remove(player.uuid());
    }

    /**
     * <p>Get a players role by his uuid</p>
     * @param uuid The uuid of the player
     * @return The {@link Role} of the player
     */
    public Role get(String uuid) {
        return players.get(uuid);
    }

    /**
     * <p>Get a players role</p>
     * @param player The instance of the {@link Player}
     * @return The {@link Role} of the player
     */
    public Role get(Player player) {
        return get(player.uuid());
    }

    /**
     * Set the role of a player
     * @param player The {@link Player} to set the role of
     * @param role The {@link Role}
     */
    public void set(Player player, Role role) {
        set(player.uuid(), role.id);
    }

    /**
     * Set the role of a player
     * @param uuid The uuid of the {@link Player} to set the role of
     * @param role The {@link Role}
     */
    public void set(String uuid, Role role) {
        set(uuid, role.id);
    }

    /**
     * Set the role of a player
     * @param player The {@link Player} to set the role of
     * @param id The id of the {@link Role}
     */
    public void set(Player player, int id) {
        set(player.uuid(), id);
    }

    /**
     * Set the role of a player
     * @param uuid The uuid of the {@link Player} to set the role of
     * @param id The id of the {@link Role}
     */
    public void set(String uuid, int id) {
        playerRoleIDs.put(uuid, String.valueOf(id));
        try {
            PropertiesUtils.store(playerRoleIDs, playerRolesFile.writer(false), "");
        } catch (IOException err) {
            Log.err("Error saving player roles", err);
        }
    }
}
