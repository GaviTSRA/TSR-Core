package tsrcore;

import mindustry.gen.Player;

public class TSRCoreEvents {
    /** Fired when the /reload command is used */
    public static class ReloadEvent {}

    /** Fired when the role of a player changes */
    public static class PlayerRoleChangeEvent {
        public final Player player;
        public final Role newRole;
        public PlayerRoleChangeEvent(Player player, Role newRole) {
            this.player = player;
            this.newRole = newRole;
        }
    }

    /** Fired when a players ip is verified */
    public static class PlayerVerifyEvent {
        public final Player player;

        public PlayerVerifyEvent(Player player) {
                this.player = player;
        }
    }
}
