package core;

import arc.Events;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TSRCore extends Plugin {

    public int versionMajor = 1;
    public int versionMinor = 1;
    public int versionPath = 0;
    public String versionString = versionMajor + "." + versionMinor + "." + versionPath;
    public DataStorage settings;
    public Roles roles;
    public Players players;
    public Money money;

    public Commands commands;
    private ObjectMap<Integer, OptionMenu> optionMenus = new ObjectMap<>();
    private List<Role> roleList = new ArrayList<>();
    private int lastMenuId;

    public TSRCore() {
        Events.on(EventType.MenuOptionChooseEvent.class, e -> {
            if (optionMenus.containsKey(e.menuId)) {
                optionMenus.get(e.menuId).run(e);
            }
        });
        Events.on(EventType.PlayerJoin.class, e -> {
            players.add(e.player, settings.getInt("defaultRoleID", 0));
        });
        Events.on(EventType.PlayerLeave.class, e -> {
            players.remove(e.player);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("core", "", "Info about the TSR Core Library",
                (args, player) -> player.sendMessage("TSR-Core Library v"+ versionString));
        handler.<Player>register("reload", "", "Reload various files after manuel changes",
                (args, player) -> {
                    if (!canUseCommand(player, "reload", 1)) {
                        player.sendMessage("[red]No.");
                        return;
                    }
                    player.sendMessage("Reloading!");

                    settings.load();
                    roles.load();
                    players.reload();
                    money.load();
                    commands.load();
                    player.sendMessage("[green]Reloaded!");
                });
        handler.<Player>register("setperms", "", "Set the permission level of a player", (args, player) -> {
            if (!canUseCommand(player, "setperms", 1)) {
                player.sendMessage("[red]No.");
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
                    players.set(p, roleList.get(Integer.parseInt(res)).id);
                    p.sendMessage("Your role has been updated to " + roleList.get(Integer.parseInt(res)).name);
                    players.reload();
                }).open(player);
            });
        });
    }
    
    public void init() {
        Fi pluginDir = new Fi("./config/mods/tsrcore");
        if (!pluginDir.exists()) pluginDir.mkdirs();

        settings = new DataStorage("./config/mods/tsrcore/config.properties");
        roles = new Roles("./config/mods/tsrcore/roles.properties");
        players = new Players(this, "./config/mods/tsrcore/playerRoles.properties");
        money = new Money("./config/mods/tsrcore/money.properties");
        commands = new Commands("./config/mods/tsrcore/commandPermissions.properties");
    }

    public String getVersion() {
        return versionString;
    }

    public boolean canUseCommand(String uuid, String commandName, int notFound) {
        return players.get(uuid).canUseCommand(commandName, notFound, commands);
    }

    public boolean canUseCommand(Player player, String commandName, int notFound) {
        return players.get(player).canUseCommand(commandName, notFound, commands);
    }

    public int registerMenu(OptionMenu menu) {
        int menuId = this.lastMenuId + 1;
        this.lastMenuId = menuId;
        optionMenus.put(menuId, menu);
        return menuId;
    }

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