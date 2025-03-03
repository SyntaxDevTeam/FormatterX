package pl.syntaxdevteam.formatter.basic

import io.github.miniplaceholders.api.MiniPlaceholders
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.formatter.FormatterX
import pl.syntaxdevteam.formatter.common.MessageHandler
import pl.syntaxdevteam.formatter.hooks.HookHandler
import java.util.concurrent.ConcurrentHashMap

/**
 * The `ChatFormatterListener` class is responsible for handling chat messages sent by players.
 * It listens for chat events and formats the messages according to the plugin configuration.
 *
 * @property plugin The instance of the `FormatterX` plugin, used for logging messages and accessing other plugin functionalities.
 * @property messageHandler The message handler instance, used for formatting messages and handling placeholders.
 * @property hookHandler The hook handler instance, used for retrieving player-specific data from external services.
 */
class ChatFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler,
    private val hookHandler: HookHandler
) : Listener {
    private val fpc: FormatPermissionChecker = FormatPermissionChecker
    private val resolverCache = ConcurrentHashMap<Player, TagResolver>()

    /**
     * Event handler for chat messages.
     * This method is triggered when a player sends a chat message.
     * It cancels the original chat event and formats the message according to the plugin's configuration.
     *
     * @param event The `AsyncChatEvent` that contains information about the chat message.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val player: Player = event.player
        if(player.hasPermission("formatterx.minimessage.all")) {
            plugin.logger.debug("Gracz ma uprawnienia do MiniMessage (formatterx.minimessage.all)")
        }else{
            plugin.logger.debug("Gracz nie ma uprawnień do MiniMessage (formatterx.minimessage.all)")
        }

        val messageContent = PlainTextComponentSerializer.plainText().serialize(event.message())
        val group = hookHandler.getPrimaryGroup(player)
        val format = generateChatFormat(player, group, messageContent)
        plugin.logger.debug("Chat format: $format")

        val resolver = resolverCache.computeIfAbsent(player) {
            plugin.logger.debug("Creating new resolver for ${player.name}")

            if (hookHandler.checkMiniPlaceholderAPI()) {
                TagResolver.resolver(MiniPlaceholders.getAudiencePlaceholders(player))
            } else {
                TagResolver.empty()
            }
        }
        plugin.logger.debug("Using cached resolver for ${player.name}")

        val finalComponent: Component = messageHandler.formatMixedTextToMiniMessage(format, resolver)
        Bukkit.getServer().sendMessage(finalComponent)
    }

    /**
     * Generates the chat format for a player.
     * This method retrieves various placeholders and formats the chat message according to the plugin's configuration.
     *
     * @param player The player who sent the message.
     * @param group The player's primary group.
     * @param messageContent The content of the message sent by the player.
     * @return The formatted chat message as a string.
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
        val displayName = LegacyComponentSerializer.legacyAmpersand().serialize(player.displayName())
        val playerName = player.name
        val worldName = player.world.name
        val filteredMessageContent = filterMessageContent(player, messageContent)
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
            .replace("{message}", filteredMessageContent)

        format = messageHandler.convertSectionSignToMiniMessage(format)
        plugin.logger.debug("Chat format after basic placeholder: $format")
        plugin.logger.debug("PAPI: ${hookHandler.checkPlaceholderAPI()}")
        if (hookHandler.checkPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format)
            plugin.logger.debug("Chat format after PlaceholderAPI: $format")
        }
        return format
    }

    /**
     * Filters the provided chat message content based on the player's permissions for various formatting tokens.
     *
     * This method processes the message by iterating over each character and handling different types of formatting tokens:
     *
     * 1. **Legacy Tokens with Ampersand (&) Prefix:**
     *    - When a token starts with '&' and has at least one following character, a two-character token is extracted.
     *    - The token is checked using:
     *      - [FormatPermissionChecker.canUseColorToken] to determine if the player is allowed to use the color.
     *      - [FormatPermissionChecker.canUseLegacyFormat] to determine if the player is allowed to use the format.
     *    - If either check passes, the token is appended to the filtered message.
     *
     * 2. **Legacy Tokens with Section Sign (§) Prefix:**
     *    - Similar to the ampersand tokens, if a token starts with '§', a two-character token is extracted and validated.
     *    - The same permission checks as for ampersand tokens are applied.
     *
     * 3. **MiniMessage Tokens Enclosed in Angle Brackets (< and >):**
     *    - When a token starts with '<', the method looks ahead to find the corresponding closing '>'.
     *    - The token between these delimiters is extracted, and the tag name is derived by taking the substring
     *      before the colon (if any) and converting it to lowercase.
     *    - The tag is then categorized as either a color or a formatting tag:
     *      - **Allowed MiniMessage Colors:** If the tag name is in the `allowedMiniMessageColors` set,
     *        [FormatPermissionChecker.canUseMinimessageColors] is called to verify if the player has permission.
     *      - **Allowed MiniMessage Formats:** If the tag name is in the `allowedMiniMessageFormats` set,
     *        [FormatPermissionChecker.canUseMinimessageFormat] is called with the entire token.
     *      - **Fallback Case:** If the tag does not match either allowed set, the method checks if the player can use
     *        MiniPlaceholder tokens via [FormatPermissionChecker.canUseMiniPlaceholder] and, if so, appends the token.
     *
     * 4. **Placeholder Tokens Enclosed in Percent Signs (%):**
     *    - When encountering a '%' character, the method searches for the next '%' to extract a placeholder token.
     *    - The token is appended only if [FormatPermissionChecker.canUsePapi] confirms that the player has permission.
     *
     * 5. **Other Characters:**
     *    - Characters that do not belong to any recognized formatting token are directly appended to the filtered message.
     *
     * @param player The player whose permissions determine which formatting tokens are allowed.
     * @param message The original chat message content that may contain various formatting tokens.
     * @return A filtered string containing only those formatting tokens and characters that the player is permitted to use.
     */
    private fun filterMessageContent(player: Player, message: String): String {
        val allowedMiniMessageColors = setOf(
            "black", "dark_blue", "dark_green", "dark_aqua", "dark_red",
            "red", "purple", "gold", "gray", "dark_gray", "blue",
            "green", "aqua", "pink", "yellow", "white"
        )
        val allowedMiniMessageFormats = setOf(
            "bold", "italic", "underlined", "strikethrough", "magic", "reset",
            "rainbow", "gradient", "click", "hover", "font", "insertion",
            "keybind", "translatable", "selector"
        )

        val filteredMessage = StringBuilder()
        var i = 0

        while (i < message.length) {
            val char = message[i]
            if (char == '&' && i + 7 < message.length && message[i + 1] == '#' && message.substring(i + 2, i + 8).matches(Regex("[0-9A-Fa-f]{6}"))) {
                val token = message.substring(i, i + 8)
                if (fpc.canUseColorToken(player, token)) {
                    filteredMessage.append(token)
                }
                i += 8

            } else if (char == '§' && i + 1 < message.length) {
                val token = message.substring(i, i + 2)
                if (fpc.canUseColorToken(player, token) || fpc.canUseLegacyFormat(player, token)) {
                    filteredMessage.append(token)
                }
                i += 2
            } else if (char == '<') {
                val endIndex = message.indexOf('>', i)
                if (endIndex != -1) {
                    val token = message.substring(i, endIndex + 1)
                    val tagContent = token.substring(1, token.length - 1)
                    val tagName = tagContent.substringBefore(':').lowercase()

                    if (allowedMiniMessageColors.contains(tagName)) {
                        if (fpc.canUseMinimessageColors(player)) {
                            filteredMessage.append(token)
                        }
                    } else if (allowedMiniMessageFormats.contains(tagName)) {
                        if (fpc.canUseMinimessageFormat(player, token)) {
                            filteredMessage.append(token)
                        }
                    } else {
                        if (fpc.canUseMiniPlaceholder(player)) {
                            filteredMessage.append(token)
                        } else {
                            filteredMessage.append("")
                        }
                    }
                    i = endIndex + 1
                } else {
                    filteredMessage.append(char)
                    i++
                }
            } else if (char == '%' && message.indexOf('%', i + 1) != -1) {
                val endIndex = message.indexOf('%', i + 1)
                val token = message.substring(i, endIndex + 1)
                if (fpc.canUsePapi(player)) {
                    filteredMessage.append(token)
                }
                i = endIndex + 1
            } else if (char == '&' && i + 1 < message.length) {
                val token = message.substring(i, i + 2)
                if (fpc.canUseColorToken(player, token) || fpc.canUseLegacyFormat(player, token)) {
                    filteredMessage.append(token)
                }
                i += 2
            } else {
                filteredMessage.append(char)
                i++
            }
        }

        return filteredMessage.toString()
    }
}