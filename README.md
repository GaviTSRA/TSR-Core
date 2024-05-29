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
TODO

## Player Accounts
TODO

## Database Syncing
TODO