package core;

import arc.Events;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import mindustry.entities.EntityGroup;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TSRCore extends Plugin {

    public int versionMajor = 1;
    public int versionMinor = 0;
    public String versionString = this.versionMajor + "." + this.versionMinor;
    public DataStorage settings;
    public Roles roles;
    public Players players;
    public Money money;
    public Commands commands;

    private ObjectMap<Integer, Consumer<EventType.MenuOptionChooseEvent>> menuTasks = new ObjectMap<>();
    private List<Role> roleList = new ArrayList<>();

    //constructor (used for events)
    public TSRCore() {
        Events.on(EventType.MenuOptionChooseEvent.class, e -> {
            if (menuTasks.containsKey(e.menuId)) {
                menuTasks.get(e.menuId).accept(e);
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
                List<List<String>> buttons = new ArrayList<>();
                List<String[]> array = new ArrayList<>();
                int x = 3;
                for (Role role : roleList) {
                    x++;
                    if (x > 3) {
                        buttons.add(new ArrayList<>());
                        x = 0;
                    }
                    buttons.get(buttons.size() - 1).add(role.name);
                }
                for (List<String> list : buttons) {
                    array.add(list.toArray(new String[0]));
                }
                Call.menu(player.con(), 2, "Choose a role", "Please choose a role to set " +
                        p.name + "'s role to", array.toArray(new String[0][0]));
                useMenuResponse(2, e -> {
                    if (e.option == -1) return;
                    players.set(p, roleList.get(e.option).id);
                    p.sendMessage("Your role has been updated to " + roleList.get(e.option).name);
                    players.reload();
                });
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

    public void useMenuResponse(int menuID, Consumer<EventType.MenuOptionChooseEvent> task) {
        menuTasks.put(menuID, task);
    }

    public void useSelectPlayer(Player player, Consumer<Player> task) {
        List<List<String>> buttons = new ArrayList<>();
        List<String[]> array = new ArrayList<>();
        int x = 3;
        for (Player p : Groups.player){
            x++;
            if (x > 3) {
                buttons.add(new ArrayList<>());
                x = 0;
            }
            buttons.get(buttons.size() - 1).add(p.name);
        }
        for (List<String> list : buttons) {
            array.add(list.toArray(new String[0]));
        }
        Call.menu(player.con(), 1, "Choose a player", "",
                array.toArray(new String[0][0]));
        useMenuResponse(1, e -> {
            if (e.option == -1) return;
            new Thread(() -> task.accept(Groups.player.find(p -> Objects.equals(p.name, Groups.player.index(e.option).name)))).start();
        });
    }
}