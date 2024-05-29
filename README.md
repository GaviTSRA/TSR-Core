# TSR Core
TSR Core is a Mindustry plugin that aims to add utilities for other plugin creators.
It can manage player roles, permissions, money, accounts, store custom data, show menus and more.
---


# Installation
To install TSR Core, create a Mindustry plugin from the [default template](https://github.com/Anuken/MindustryPluginTemplate),
then add TSR Core to gradle:
```groovy
dependencies {
    ...
    compileOnly "com.github.GaviTSRA:TSR-Core:v3.0.0"
}
```
Then add a variable for the TSR Core instance in your main java file:
```java
public TSRCore tsrCore;
```
and initialise it in your init function:
```java
public void init() {
    tsrCore = (TSRCore) mods.list().find(m->m.name.equals("tsrcore")).main;
}
```
---


# Features
- [Roles and Permissions](#roles-and-permissions)
- [Settings API](#settings-api)
- [Money](#money)
- [TSR Core Events](#tsr-core-events)
- [Data Storages](#data-storages)
- [Option Menus](#option-menus)
- [Player Accounts](#player-accounts)
- [Database Syncing](#database-syncing)


## Roles and Permissions
TSR Core can handle player roles and their permission to use commands.
To add a role, update your `config/tsrcore/roles.properties`.
Each role should have a unique id. Here is an example on how a custom role can look:
```properties
# Permission level of the role, explained further down
mod.permissionLevel=2
# Name of the role, displayed when you change someone's role (you can access this to add it to player names on join)
mod.name=[forest]Mod
# If the role should have the Mindustry admin status
mod.admin=false
# Unique role id
mod.id=2
```
After adding a role, you can change a players role using `/setperms` and selecting the wanted player and role through the menu.

Each role has a permission level. This is used to check if that role is able to use commands.
To set the permission level of a command, update `config/tsrcore/commandPermissions.properties`.

To use this system for your own command, first register it to TSR Core in your init method:
```java
// Register the kick command with a permission level of 1
tsrCore.commands.register("kick", 1);
```
This will automatically add it to your `config/tsrcore/commandPermission.properties` on startup, if it is not already present.
Then you need to check if the player is allowed to use the command in your command handler:
```java
handler.<Player>register("kick", "", "Kick a player", (args, player) -> {
    if(!tsrCore.canUseCommand(player, "kick")) {
        player.sendMessage("[red]You are not allowed to use this command.");
        return;
    }
    ...
}
```

## Settings API
TSR Core adds a simple-to-use settings api you can use to configure custom settings of your plugin.
To register your own setting, add this to your init function:
```java
// Register the discordInvite setting and set the default to "No discord"
tsrCore.settings.register("discordInvite", "No discord");
```
This will automatically create your setting on startup with the supplied default in `config/tsrcore/settings.properties`,
where you can configure it.
To access the setting somewhere else in your plugin you can use the `tsrCore.settings.get*()` methods for different data types:
```java
// Get the discordInvite setting
tsrCore.settings.getString("discordInvite")
```

## Money
TSRCore also provides a utility DataStorage to handle an in-game currency, that can be shared between multiple plugins.
You can use it with the these methodsd:
```java
tsrCore.money.set(player, 5);
tsrCore.money.add(player, 1);
tsrCore.money.remove(player, 2);
tsrCore.money.get(player); // 4
```

## TSR Core Events
TSRCore provides 3 events that can be used by your plugin.
- The PlayerVerifyEvent is fired when a player passes verification, either on join with a known ip or after logging in.
- The ReloadEvent is fired when the /reload command is used and should be used to reload your DataStorages from file using their load method. You should only reload DB synced DataStorages if the useDB setting is disabled!
- The PlayerRoleChangeEvent is fired when a players role is updated using /setperms


## Data Storages
Data Storages are TSR Cores way of storing data and are used internally for things like player roles, settings or command permissions.
They can also be used by plugins using TSR Core, however.
To start using a DataStorage, create a variable for it in your plugin:
```java
private DataStorage xp;
```
Then initialise it in your init function:
```java
// Initialise the new data storage with a file path
xp = new DataStorage("./config/mods/tsrcmds/xp.properties");
```
This will create the file if needed and load the data.
You can then access the data or update it:
```java
xp.getInt(p.uuid());
xp.set(p.uuid(), 10);
```

## Option Menus
TSR Core provides an API to let other plugins let players choose an option from a menu. These menu can have any options you like and automatically create pages for 
your options. These pages can have a variable amount of items and columns.
Here is an example of how to use OptionMenus:
```java
// Options to be used in the menu
HashMap<String, String> options = new HashMap<>();
// Key is shown to the player, value is returned when clicked
options.put("One Option", "a");
options.put("Another Option", "b");
options.put("More Options", "c");

// Create the menu and register the callback 
// res is either the value of the provided map or null if the menu is closed using escape
OptionMenu menu = new OptionMenu("Menu title", "Menu description", options, res -> {
    ...
});
menu.maxItemsPerPage(6);
menu.itemsPerRow(2);
menu.open(player);
```
TSRCore also provides a utility function to create a menu to choose a player:
```java
// player is the player that gets the menu, p is the selected player
tsrCore.useSelectPlayer(player, p ->{
    ...
}
```

## Player Accounts
TSRCore adds player accounts to your server to prevent people from using stolen uuids to get access to other peoples accounts (like mods).
When a player joins, they will be prompted to register, which is not required but recommended.  After a player has registered with a password, 
their account is safe. If someone tries to log in with their uuid from another ip, they won't get access to commands. If it is the actual player,
he can just log in using /login and the ip will be whitelisted. You cannot change the permissions of a player that is not registered.

## Database Syncing
TSRCore provides an easy way to sync your data to a MySQL database, including player accounts and roles.
To enable syncing, set the `useDB` setting to `true` and the `dbConnectString` to your JDBC connection string.

To sync your own DataStorages, just register them in your init function: 
```java
// Register the xp DataStorage to be synced to the xp column with a default value of 0
tsrCore.database.addPlayerFieldInt("xp", xp, 0);
```