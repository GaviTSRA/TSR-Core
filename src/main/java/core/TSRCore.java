package core;

import arc.Events;
import arc.util.CommandHandler;
import mindustry.game.EventType.PlayerJoin;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TSRCore extends Plugin {

    public static String version = "1.0";
    public Settings settings = new Settings("./config/mods/tsrcore/config.properties");
    public Permissions permissions = new Permissions("./config/mods/tsrcore/perms.properties");
    public List<Role> roles = new ArrayList<>();

    //constructor (used for events)
    public TSRCore() {
        Events.on(PlayerJoin.class, e -> {

        });
    }

    @Override //commands that players can use in game
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("core", "", "Desc",
                (args, player) -> player.sendMessage("TSR-Core Library v"+version));
    }
    
    public void init() {

    }

    public String getVersion() {
        return version;
    }
}