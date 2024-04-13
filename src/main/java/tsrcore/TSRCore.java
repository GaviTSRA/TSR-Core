package tsrcore;

import arc.Events;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Timer;
//import at.favre.lib.crypto.bcrypt.BCrypt;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TSRCore extends Plugin {

    public int versionMajor = 3;
    public int versionMinor = 0;
    public int versionPath = 0;
    public String versionString = versionMajor + "." + versionMinor + "." + versionPath;
    public Database database;
    public Settings settings;
    public Roles roles;
    public Players players;
    public DataStorage playerRoles;
    public DataStorage ips;
    public DataStorage allowedIps;
    public DataStorage passwords;
    public Money money;
    public ArrayList<Player> notVerified = new ArrayList<>();

    public Commands commands;
    private final ObjectMap<Integer, OptionMenu> optionMenus = new ObjectMap<>();
    private List<Role> roleList = new ArrayList<>();
    private int lastMenuId;

    public TSRCore() {
        Events.on(EventType.MenuOptionChooseEvent.class, e -> {
            if (optionMenus.containsKey(e.menuId)) {
                optionMenus.get(e.menuId).run(e);
            }
        });
        Events.on(EventType.PlayerConnect.class, e -> {
            if (settings.getBool("useDB"))
                database.playerJoin(e.player.uuid());
            if (Objects.equals(ips.getString(e.player.uuid(), "null"), "null")) {
                ips.set(e.player.uuid(), e.player.ip());
            }
            if (!Objects.equals(ips.getString(e.player.uuid()), e.player.ip())) {
                ArrayList<String> allowed = new ArrayList<>(Arrays.asList(allowedIps.getString(e.player.uuid()).split(",")));
                if (!allowed.contains(e.player.ip())) {
                    Call.infoMessage(e.player.con(), "Your IP has changed. /login now to verify the new IP");
                    notVerified.add(e.player);
                    players.add(e.player, settings.getInt("defaultRoleID"));
                    return;
                }
            }
            players.add(e.player, settings.getInt("defaultRoleID"));
            Events.fire(new TSRCoreEvents.PlayerVerifyEvent(e.player));
        });
        Events.on(EventType.PlayerLeave.class, e -> {
            database.save(e.player);
            players.remove(e.player);
            notVerified.remove(e.player);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("tsrcore", "", "Info about the TSR Core Library", (args, player) -> {
            player.sendMessage("TSR-Core Library v"+ versionString);
        });
        handler.<Player>register("reload", "", "Reload various files after manual changes", (args, player) -> {
            if (!canUseCommand(player, "reload")) {
                player.sendMessage("[red]\uE815 You are not allowed to use this command.");
                return;
            }
            player.sendMessage("Reloading!");

            settings.load();
            roles.load();
            players.reload();
            money.load();
            commands.load();

            Events.fire(new TSRCoreEvents.ReloadEvent());

            player.sendMessage("[green]Reloaded!");
        });
        handler.<Player>register("register", "<password> <repeat-password>", "Register your acccount with a password. Required to save user data", (args, player) -> {
            if (passwords.getString(player.uuid()) != null && !Objects.equals(passwords.getString(player.uuid()), "")) {
                player.sendMessage("[red]\uE815 This account is already registered");
                return;
            }
            if (!Objects.equals(args[0], args[1])) {
                player.sendMessage("[red]\uE815 The passwords don't match");
                return;
            }

            //String bcryptHashString = BCrypt.withDefaults().hashToString(12, args[0].toCharArray());
            passwords.set(player.uuid(), args[0]);
        });
        handler.<Player>register("login", "<password>", "Allow a new ip for your account", (args, player) -> {
            if (Objects.equals(passwords.getString(player.uuid(), ""), "")) {
                player.sendMessage("[red]\uE815 This account is not registered. Strongly consider doing so now using /register");
                return;
            }

            ArrayList<String> allowed = new ArrayList<>(Arrays.asList(allowedIps.getString(player.uuid()).split(",")));
            if (allowed.contains(player.ip())) {
                player.sendMessage("[red]\uE815 This account is already logged in");
                return;
            }

            //String bcryptHashString = BCrypt.withDefaults().hashToString(12, args[0].toCharArray());
            //if (BCrypt.verifyer().verify(passwords.getString(player.uuid()).toCharArray(), bcryptHashString).verified) {
            if (Objects.equals(passwords.getString(player.uuid()), args[0])) {
                player.sendMessage("[green]\uE800 Logged in!");
                allowed.add(player.ip());
                allowedIps.set(player.uuid(), allowed.stream().map(Object::toString).collect(Collectors.joining(",")));
                notVerified.remove(player);
                Events.fire(new TSRCoreEvents.PlayerVerifyEvent(player));
            } else {
                player.sendMessage("[red]\uE815 Invalid password");
            }
        });
        handler.<Player>register("setperms", "", "Set the permission level of a player", (args, player) -> {
            if (!canUseCommand(player, "setperms")) {
                player.sendMessage("[red]\uE815 You are not allowed to use this command.");
                return;
            }
            useSelectPlayer(player, p -> {
                roleList = roles.all();
                HashMap<String, String> options = new HashMap<>();

                int i = 0;
                for (Role option : roleList) {
                    options.put(option.name, String.valueOf(i));
                    i++;
                }

                new OptionMenu("Choose a role", "Choose a role to set " + p.name + "'s role to", options, res -> {
                    if (res == null) return;
                    Role newRole = roleList.get(Integer.parseInt(res));
                    players.set(p, newRole.id);
                    p.sendMessage("Your role has been updated to " + roleList.get(Integer.parseInt(res)).name);
                    Events.fire(new TSRCoreEvents.PlayerRoleChangeEvent(p, newRole));
                    players.reload();
                }).open(player);
            });
        });
    }
    
    public void init() {
        Fi pluginDir = new Fi("./config/mods/tsrcore");
        if (!pluginDir.exists()) pluginDir.mkdirs();

        settings = new Settings("./config/mods/tsrcore/settings.properties");
        playerRoles = new DataStorage("./config/mods/tsrcore/playerRoles.properties");
        passwords = new DataStorage("./config/mods/tsrcore/passwords.properties");
        ips = new DataStorage("./config/mods/tsrcore/ips.properties");
        allowedIps = new DataStorage("./config/mods/tsrcore/allowedIps.properties");
        roles = new Roles("./config/mods/tsrcore/roles.properties");
        commands = new Commands("./config/mods/tsrcore/commandPermissions.properties");
        money = new Money("./config/mods/tsrcore/money.properties");
        players = new Players(this);

        database = new Database(settings.getString("dbConnectString"));

        if (settings.getBool("useDB") != null && settings.getBool("useDB")) {
            database.connect();
            Timer.schedule(() -> database.save(), 0, 60);
        }

        database.addPlayerFieldInt("role", playerRoles, settings.getInt("defaultRoleID"));
        database.addPlayerFieldInt("money", money, 0);
        database.addPlayerFieldString("ip", ips, "");
        database.addPlayerFieldString("allowedIps", allowedIps, "");
        database.addPlayerFieldString("password", passwords, "");

        commands.register("reload", 1);
        commands.register("setperms", 1);

        settings.register("defaultRoleID", 0);
        settings.register("useDB", false);
        settings.register("dbConnectString", "");
    }

    /**
     * @return Version of the plugin as a String
     */
    public String getVersion() {
        return versionString;
    }

    /**
     * Check if a player can use a command
     * @param player Player to check permissions of
     * @param commandName Command the check permissions of
     * @return Whether the player should be able to use the command
     */
    public boolean canUseCommand(Player player, String commandName) {
        if (notVerified.contains(player)) {
            player.sendMessage("[red]\uE815 You can't use this command because you haven't verified your ip with /login");
            return false;
        }
        return players.get(player).canUseCommand(commandName, commands);
    }

    /**
     * Used internally by the {@link OptionMenu} to listen for events.
     * You probably never need to call this yourself.
     * @param menu The option menu to register
     * @return ID of the registered menu
     */
    public int registerMenu(OptionMenu menu) {
        int menuId = this.lastMenuId + 1;
        this.lastMenuId = menuId;
        optionMenus.put(menuId, menu);
        return menuId;
    }

    /**
     * Make the player select another player through a menu, and execute the task with the selected player
     * @param player Player to send the menu to
     * @param task Code to run with the result
     */
    public void useSelectPlayer(Player player, Consumer<Player> task) {
        HashMap<String, String> options = new HashMap<>();
        for (Player p : Groups.player) {
            options.put(p.name, p.name);
        }
        new OptionMenu("Choose a player", "", options, res -> {
            if (res == null) return;
            new Thread(() -> task.accept(Groups.player.find(p -> Objects.equals(p.name, res)))).start();
        }).open(player);
    }
}