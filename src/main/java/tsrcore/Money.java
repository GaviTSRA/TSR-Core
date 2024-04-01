package tsrcore;

import mindustry.gen.Player;

public class Money extends DataStorage{
    /**
     * <p>>Create a new object storing the money each player has.</p>
     * <p>The file is created and loaded automatically.</p>
     * @param path The file where the data is stored
     */
    public Money(String path) {
        super(path);
    }

    /**
     * Set the money of a player by his uuid
     * @param uuid The uuid of the player
     * @param amount The amount to set to
     */
    public void set(String uuid, int amount) {
        super.set(uuid, amount);
    }

    /**
     * Set the money of a player by an instance
     * @param player The instance of the player
     * @param amount The amount to set to
     */
    public void set(Player player, int amount) {
        set(player.uuid(), amount);
    }

    /**
     * Give money to a player by his uuid
     * @param uuid The uuid of the player
     * @param amount The amount to give
     */
    public void add(String uuid, int amount) {
        int before = get(uuid);
        set(uuid, before + amount);
    }

    /**
     * Give money to a player by an instance
     * @param player The instance of the player
     * @param amount The amount to give
     */
    public void add(Player player, int amount) {
        add(player.uuid(), amount);
    }

    /**
     * Remove money from a player by his uuid
     * @param uuid The uuid of the player
     * @param amount The amount to remove
     */
    public void remove(String uuid, int amount) {
        int before = get(uuid);
        set(uuid, before - amount);
    }

    /**
     * Remove money from a player by an instance
     * @param player The instance of the player
     * @param amount The amount to remove
     */
    public void remove(Player player, int amount) {
        remove(player.uuid(), amount);
    }

    /**
     * Get the amount of money a player has by his uuid
     * @param uuid The uuid of the player
     * @return The amount of money the player has
     */
    public int get(String uuid) {
        return super.getInt(uuid, 0);
    }

    /**
     * Get the money a player has by an instance
     * @param player The player instance
     * @return The amount of money the player has
     */
    public int get(Player player) {
        return get(player.uuid());
    }
}
