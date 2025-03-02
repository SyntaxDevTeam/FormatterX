# ✨ FormatterX – The intelligent chat formatting plugin

## 📄 Description
The plugin enables advanced text formatting in Minecraft, supporting various formatting styles such as:
- **Ampersand Format** (`&a`, `&l` and other codes)
- **Minecraft Legacy** (`§c`, `§l` and other codes)
- **MinecraftRGB** (`&#FF00FF` for custom colors)
- **LuckPerms RGB** (`§x§F§F§0§0§F§F`)
- **MiniMessage** (full support for formatting methods – check [the documentation](https://docs.advntr.dev/minimessage/format.html))

**The plugin doesn't require any additional plugins to format your chat.** However, the full potential of FormatterX is revealed when integrated with other plugins.

## 🔗 Integrations
The plugin works with the following plugins, though none are required for its functionality:
- **LuckPerms** – retrieving group information
- **PlaceholderAPI** and **MiniPlaceholder** – handling dynamic values
- **Vault and VaultUnlocked** – retrieving prefixes, suffixes, and group names
- Support for all plugins utilizing Vault

## 🌟 Key Features
- **Support for multiple formats simultaneously** – mix different formatting styles without affecting the plugin's operation.
- **Individual formatting for each rank/group** – or a single format for all players.
- **Advanced permission system** – filter messages to prevent unauthorized players from using certain formats (e.g., `formatterx.legacy.color`).
- **Automatic removal of unauthorized formatting** – if a player lacks the required permissions, their message will be displayed without the prohibited formatting.
- **Configurable feedback messages** – set notifications for players when they lack the necessary permissions.
- **Custom placeholder templates** – simplify formatting usage through predefined variables.

## ⚙️ Configuration
All plugin settings can be customized in the configuration file, providing full control over its functionality.

## 🛠️ Installation
1. 📥 Download the plugin and place it in the `plugins` folder on your Minecraft server.
2. 🔄 Restart the server to generate the configuration files.
3. 📖 [Read the Wiki to learn as much as possible about the plugin's setting options.](https://github.com/SyntaxDevTeam/FormatterX/wiki)
4. 🛠️ Adjust the settings as needed.
5. 🎉 Enjoy advanced text formatting on your server!

## 🤔 Why Choose FormatterX?
What sets FormatterX apart from numerous other plugins of this type? Primarily, the lack of restrictions in using different formats allows you to freely mix styles without worrying that they won't be properly formatted in the chat! Most plugins have an issue where, when you try to use `&7` codes and MiniMessage format `<bold><red>` simultaneously, the latter always dominates, and the rest aren't translated into appropriate colors or formats. For example: `<&fHello &leveryone! <green>We have a wonderful %dayplugin_day% day &rfor mining &#FFCF00Diamonds!`. Our plugin, FormatterX, handles even such a mix of formats!

Moreover, other plugins partially use built-in methods from server engines to handle certain content, complicating matters with custom builds or different forks of a given engine. We have completely solved this problem by becoming independent of such methods while maintaining the same, and often better, performance!

## 📝 Summary
FormatterX provides versatile tools for text formatting, allowing complete customization of message appearance on a Minecraft server. With support for multiple formats and integrations with popular plugins, it is an essential solution for any server administrator!
