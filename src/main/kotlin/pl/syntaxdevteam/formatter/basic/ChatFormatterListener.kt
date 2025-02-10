package pl.syntaxdevteam.formatter.basic

import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.formatter.FormatterX
import pl.syntaxdevteam.formatter.common.MessageHandler
import pl.syntaxdevteam.formatter.hooks.HookHandler

/**
 * The [ChatFormatterListener] class is responsible for handling chat events and formatting the chat messages.
 * It listens for chat events and processes the messages by applying the formatting settings from the configuration.
 *
 * @property plugin The instance of the FormatterX plugin, used for logging messages and accessing other plugin functionalities.
 * @property messageHandler The message handler used for formatting the chat messages.
 * @property hookHandler The hook handler used for retrieving player-specific data from external services.
 */
class ChatFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler,
    private val hookHandler: HookHandler
) : Listener {

    /**
     * Handles the chat event by canceling it and formatting the message.
     *
     * This method does the following:
     * 1. Cancels the event to prevent the default chat handling.
     * 2. Extracts necessary information:
     *    - `player` - The player who sent the message.
     *    - `messageContent` - The raw message stripped of all formatting characters.
     *    - `group` - The primary group of the player retrieved from LuckPerms or Vault/VaultUnlocked.
     * 3. Processes the chat message:
     *    - `format` - Applies the formatting settings from the configuration.
     *    - `finalComponent` - Converts the formatted message into an Adventure Component.
     * 4. Sends the final formatted message to the server.
     *
     * @param event The chat event being handled.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true  // Prevents the default chat processing

        val player = event.player
        val messageContent = PlainTextComponentSerializer.plainText().serialize(event.message())
        val group = hookHandler.getPrimaryGroup(player)
        val format = generateChatFormat(player, group, messageContent)
        plugin.logger.debug("Chat format: $format")
        val finalComponent: Component = messageHandler.formatMixedTextToMiniMessage(format)

        Bukkit.getServer().sendMessage(finalComponent)
    }

    /**
     * Generates the chat format for the player.
     *
     * @param player The player.
     * @param group The player's group.
     * @param messageContent The message content.
     * @return The formatted chat message.
     */
    private fun generateChatFormat(player: Player, group: String, messageContent: String): String {
        val prefix = hookHandler.getPlayerPrefix(player)
        val suffix = hookHandler.getPlayerSuffix(player)
        val usernameColor = hookHandler.getLuckPermsMetaValue(player, "username-color") ?: ""
        val messageColor = hookHandler.getLuckPermsMetaValue(player, "message-color") ?: ""
        val prefixesSeparator = plugin.config.getString("chat.prefixes_separator") ?: ""
        val suffixesSeparator = plugin.config.getString("chat.suffixes_separator") ?: ""
        val prefixes = hookHandler.getAllLuckPermsMetData(player)?.prefixes?.values?.joinToString(prefixesSeparator) ?: ""
        val suffixes = hookHandler.getAllLuckPermsMetData(player)?.suffixes?.values?.joinToString(suffixesSeparator) ?: ""


        val displayName = PlainTextComponentSerializer.plainText().serialize(player.displayName())
        val playerName = player.name
        val worldName = player.world.name
        //TODO: Dodać użycie filterMessageContent()
        var format = (plugin.config.getString("chat.group-formats.$group") ?: plugin.config.getString("chat.defaultFormat") ?: "${messageHandler.getPrefix()} {displayname} » {message}")
            .replace("{group}", group)
            .replace("{prefix}", prefix)
            .replace("{suffix}", suffix)
            .replace("{prefixes}", prefixes)
            .replace("{suffixes}", suffixes)
            .replace("{displayname}", displayName)
            .replace("{name}", playerName)
            .replace("{world}", worldName)
            .replace("{username-color}", usernameColor)
            .replace("{message-color}", messageColor)
            .replace("{message}", messageContent)

        if (hookHandler.checkPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format)
        }

        return format
    }

    private fun filterMessageContent(player: Player, message: String): String {
        var filteredMessage = message

        // Przykład filtrowania legacy kolorów:
        // Znajdź wszystkie wystąpienia legacy tokenów (np. &x)
        val legacyRegex = Regex("&([0-9a-fk-or])")
        filteredMessage = legacyRegex.replace(filteredMessage) { matchResult ->
            val token = "&" + matchResult.groupValues[1]
            if (FormatPermissionChecker.canUseColorToken(player, token)) {
                token  // Pozostawiam oryginalny token (który potem zostanie przekonwertowany na MiniMessage)
            } else {
                "" // Usuń token, jeśli gracz nie ma uprawnień
            }
        }

        // Analogicznie możesz filtrować tokeny minimessage lub RGB, jeśli są obecne.

        // Przykład filtrowania formatowania (np. <bold>):
        val formatRegex = Regex("<(bold|italic|underlined|strikethrough|obfuscated|reset)>")
        filteredMessage = formatRegex.replace(filteredMessage) { matchResult ->
            val token = "<" + matchResult.groupValues[1] + ">"
            when (matchResult.groupValues[1].lowercase()) {
                "bold" -> if (FormatPermissionChecker.canUseBold(player)) token else ""
                "italic" -> if (FormatPermissionChecker.canUseItalic(player)) token else ""
                // TODO: Dodać kolejne przypadki dla innych formatów
                else -> token
            }
        }

        return filteredMessage
    }

}
