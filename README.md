# FormatterX - The intelligent chat formatting plugin

## Description
The plugin enables advanced text formatting in Minecraft, supporting various formatting styles such as:
- **Minecraft Legacy** (`&7` and other codes)
- **RGB** (`&#FF00FF` for custom colors)
- **MiniMessage** (full support for formatting methods - check https://docs.advntr.dev/minimessage/format.html)

It can operate independently while also integrating with popular plugins, maintaining full flexibility and compatibility.

## Integrations
The plugin works with the following plugins, though none are required for its functionality:
- **LuckPerms** – retrieving group information
- **PlaceholderAPI** and **MiniPlaceholder** – handling dynamic values
- **Vault and VaultUnlocked** – retrieving prefixes, suffixes, and group names
- Support for all plugins utilizing Vault

## Key Features
- **Support for multiple formats simultaneously** – mix different formatting styles without affecting the plugin's operation.
- **Individual formatting for each rank/group** – or a single format for all players.
- **Advanced permission system** – filter messages to prevent unauthorized players from using certain formats (e.g., `formatterx.legacy.color`).
- **Automatic removal of unauthorized formatting** – if a player lacks the required permissions, their message will be displayed without the prohibited formatting.
- **Configurable feedback messages** – set notifications for players when they lack the necessary permissions.
- **Custom placeholder templates** – simplify formatting usage through predefined variables.

## Configuration
All plugin settings can be customized in the configuration file, providing full control over its functionality.

## Installation
1. Download the plugin and place it in the `plugins` folder on your Minecraft server.
2. Restart the server to generate the configuration files.
3. Adjust the settings as needed.
4. Enjoy advanced text formatting on your server!

## Summary
The plugin provides versatile tools for text formatting, allowing complete customization of message appearance on a Minecraft server. With support for multiple formats and integrations with popular plugins, it is an essential solution for any server administrator!

