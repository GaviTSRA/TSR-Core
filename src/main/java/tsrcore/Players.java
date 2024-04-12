package tsrcore;

import arc.struct.ObjectMap;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class Players {
    private final TSRCore core;
    private ObjectMap<String, Role> players;

    /**
     * <p>Create a new class to store all the players in the game and their roles.</p>
     * <p>The file is automatically created if it doesn't exist.</p>
     * <p>The data is automatically loaded.</p>
     * @param core An instance of the TSRCore class
     */
    public Players(TSRCore core) {
        this.core = core;
        players = new ObjectMap<>();
    }

    /**
     * <p>Add a player to the online players list.</p>
     * <p>This should be handled by the TSR Core in most cases.</p>
     * @param player The player to add
     * @param defaultRoleID The default role id
     */
    public void add(Player player, int defaultRoleID) {
        String id = core.playerRoles.getString(player.uuid());
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
        core.playerRoles.set(uuid, String.valueOf(id));
    }

    /**
     * Reloads the data
     */
    public void reload() {
        ObjectMap<String, Role> newPlayers = new ObjectMap<>();
        Groups.player.forEach(player -> {
            Role role = core.roles.get(core.playerRoles.getInt(player.uuid()));
            newPlayers.put(player.uuid(), role);
            player.admin = role.admin;
        });
        players = newPlayers;
    }
}
