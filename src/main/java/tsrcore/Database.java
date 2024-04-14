package tsrcore;

import arc.util.Log;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Database {
    public String connectString;

    private Connection connection;
    private final HashMap<StorageEntry, DataStorage> storages = new HashMap<>();

    /**
     * Create a new database from
     * @param connectString The JDBC connect string
     */
    public Database(String connectString) {
        this.connectString = connectString;
        load();
    }

    /**
     * Load the database settings. This is done automatically
     */
    public void load() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Log.err("Failed to load database classes: " + e);
        }
    }

    /**
     * Connect to the database. This is done automatically
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(connectString);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet res = metaData.getTables(null, null, "players", null);
            if (!res.next())
                update("CREATE TABLE players (uuid VARCHAR(255) PRIMARY KEY)");
        } catch (SQLException e) {
            Log.err("Failed to connect to DB: " + e);
        }
    }

    private void update(String query) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            Log.err("SQL Update failed: " + e);
        }
    }

    private ResultSet query(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            Log.err("SQL Query failed: " + e);
        }
        return null;
    }

    private boolean hasColumn(String name) {
        // TODO gets stuck only in prod for some reason
        try {
            Log.info("Checking for column "+name);
            DatabaseMetaData metaData = connection.getMetaData();
            Log.info("Checking...");
            ResultSet res = metaData.getColumns(null, null, "players", name);
            Log.info("a");
            return res.next();
        } catch (SQLException e) {
            Log.err("SQL Exception 1: " + e);
            return false;
        }
    }

    /**
     * Add a new string player data field
     * @param name The name of the field
     * @param storage The DataStorage to sync the data into
     * @param defaultValue The default value if a player has no data stored in the db
     */
    public void addPlayerFieldString(String name, DataStorage storage, String defaultValue) {
        if (connection == null) return;
        storages.put(new StorageEntry(name, "string", defaultValue), storage);
        //if (hasColumn(name)) return;
        update("ALTER TABLE players ADD COLUMN "+name+" TEXT");
    }

    /**
     * Add a new int player data field
     * @param name The name of the field
     * @param storage The DataStorage to sync the data into
     * @param defaultValue The default value if a player has no data stored in the db
     */
    public void addPlayerFieldInt(String name, DataStorage storage, int defaultValue) {
        if (connection == null) return;
        storages.put(new StorageEntry(name, "int", String.valueOf(defaultValue)), storage);
        //if (hasColumn(name)) return;
        update("ALTER TABLE players ADD COLUMN "+name+" INT");
    }

    /**
     * Add a new boolean player data field
     * @param name The name of the field
     * @param storage The DataStorage to sync the data into
     * @param defaultValue The default value if a player has no data stored in the db
     */
    public void addPlayerFieldBool(String name, DataStorage storage, boolean defaultValue) {
        if (connection == null) return;
        storages.put(new StorageEntry(name, "bool", String.valueOf(defaultValue)), storage);
        //if (hasColumn(name)) return;
        update("ALTER TABLE players ADD COLUMN "+name+" TINYINT(1)");
    }

    /**
     * Add a new double player data field
     * @param name The name of the field
     * @param storage The DataStorage to sync the data into
     * @param defaultValue The default value if a player has no data stored in the db
     */
    public void addPlayerFieldDouble(String name, DataStorage storage, double defaultValue) {
        if (connection == null) return;
        storages.put(new StorageEntry(name, "double", String.valueOf(defaultValue)), storage);
        //if (hasColumn(name)) return;
        update("ALTER TABLE players ADD COLUMN "+name+" DOUBLE");
    }

    /**
     * Sync the data of a player to the storages. Done automatically on join
     * @param uuid The uuid of the new player
     */
    public void playerJoin(String uuid) {
        ResultSet res = query("SELECT * FROM players WHERE uuid = '" + uuid + "'");
        if (res == null) return;

        try {
            if (!res.isBeforeFirst()) {
                StringBuilder names = new StringBuilder("uuid");
                StringBuilder values = new StringBuilder(uuid);

                for (Map.Entry<StorageEntry, DataStorage> entry : storages.entrySet()) {
                    names.append(",").append(entry.getKey().name);
                    values.append(",").append("?");
                }

                String query = "INSERT INTO players (" + names + ") VALUES (" + values + ")";
                PreparedStatement statement = connection.prepareStatement(query);
                int i = 0;
                for (Map.Entry<StorageEntry, DataStorage> entry : storages.entrySet()) {
                    i++;
                    if (Objects.equals(entry.getKey().type, "string"))
                        statement.setString(i, entry.getKey().defaultValue);
                    if (Objects.equals(entry.getKey().type, "int"))
                        statement.setInt(i, Integer.parseInt(entry.getKey().defaultValue));
                    if (Objects.equals(entry.getKey().type, "double"))
                        statement.setDouble(i, Double.parseDouble(entry.getKey().defaultValue));
                    if (Objects.equals(entry.getKey().type, "bool"))
                        statement.setBoolean(i, Boolean.parseBoolean(entry.getKey().defaultValue));
                }
                statement.executeUpdate();
                statement.close();

                for (Map.Entry<StorageEntry, DataStorage> entry : storages.entrySet()) {
                    entry.getValue().set(uuid, entry.getKey().defaultValue);
                }

                return;
            }

            res.next();
            for (Map.Entry<StorageEntry, DataStorage> entries : storages.entrySet()) {
                String type = entries.getKey().type;
                if (Objects.equals(type, "string")) {
                    entries.getValue().set(uuid, res.getString(entries.getKey().name));
                } else if (Objects.equals(type, "int")) {
                    entries.getValue().set(uuid, res.getInt(entries.getKey().name));
                } else if (Objects.equals(type, "bool")) {
                    entries.getValue().set(uuid, res.getBoolean(entries.getKey().name));
                } else if (Objects.equals(type, "double")) {
                    entries.getValue().set(uuid, res.getDouble(entries.getKey().name));
                }
            }

            res.close();
        } catch (SQLException e) {
            Log.err("SQL Exception 2: " );
            e.printStackTrace();
        }
    }

    /**
     * Sync the data of all players to the db. This is done automatically every minute
     */
    public void save() {
        Groups.player.each(this::save);
    }

    /**
     * Sync the data of a specific player to the db. This is done automatically when a player leaves.
     * @param player The player to sync the data of
     */
    public void save(Player player) {
        try {
            String uuid = player.uuid();
            StringBuilder update = new StringBuilder();

            for (Map.Entry<StorageEntry, DataStorage> entry : storages.entrySet()) {
                if (!update.toString().isEmpty())
                    update.append(",");
                update.append(entry.getKey().name).append(" = ?");
            }

            String query = "UPDATE players SET " + update + " WHERE uuid = '" + uuid + "'";
            PreparedStatement statement = connection.prepareStatement(query);
            int i = 0;
            for (Map.Entry<StorageEntry, DataStorage> entry : storages.entrySet()) {
                i++;
                DataStorage storage = entry.getValue();
                if (Objects.equals(entry.getKey().type, "string"))
                    statement.setString(i, storage.getString(uuid));
                if (Objects.equals(entry.getKey().type, "int"))
                    statement.setInt(i, storage.getInt(uuid));
                if (Objects.equals(entry.getKey().type, "double"))
                    statement.setDouble(i, storage.getDouble(uuid));
                if (Objects.equals(entry.getKey().type, "bool"))
                    statement.setBoolean(i, storage.getBool(uuid));
            }
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            Log.err("SQL Exception 3: " + e);
        }
    }

    private static class StorageEntry {
        String name;
        String type;
        String defaultValue;

        public StorageEntry(String name, String type, String defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }
    }
}
