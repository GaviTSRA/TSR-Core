package core;

import arc.Events;
import arc.files.Fi;
import arc.util.CommandHandler;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

public class TSRCore extends Plugin {

    public int versionMajor = 1;
    public int versionMinor = 0;
    public String versionString = this.versionMajor + "." + this.versionMinor;
    public Settings settings = new Settings("./config/mods/tsrcore/config.properties");
    public Roles roles = new Roles("./config/mods/tsrcore/roles.properties");
    public Players players = new Players(this, "./config/mods/tsrcore/playerRoles.properties");

    //constructor (used for events)
    public TSRCore() {
        Events.on(EventType.PlayerJoin.class, e -> {
            players.add(e.player, settings.getInt("defaultRoleID", 0));
        });

        Events.on(EventType.PlayerLeave.class, e -> {
            players.remove(e.player);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("core", "", "Desc",
                (args, player) -> player.sendMessage("TSR-Core Library v"+ versionString));
    }
    
    public void init() {
        Fi pluginDir = new Fi("./config/mods/tsrcore");
        if (!pluginDir.exists()) pluginDir.mkdirs();
    }

    public String getVersion() {
        return versionString;
    }
}