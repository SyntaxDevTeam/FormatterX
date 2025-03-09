package pl.syntaxdevteam.formatter.basic

import io.github.miniplaceholders.api.MiniPlaceholders
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import java.time.Duration
import org.bukkit.Bukkit
import org.bukkit.Sound
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

        if (!fpc.canUseMention(player)) {
            return
        }

        val lowerCaseMessage = messageContent.lowercase()
        val onlinePlayers = Bukkit.getOnlinePlayers()

        val mentionedPlayers = onlinePlayers.filter { mentioned ->
            lowerCaseMessage.contains("@${mentioned.name.lowercase()}") && fpc.canReceiveMention(mentioned)
        }
        val soundName = plugin.config.getString("chat.mention-sound", "ENTITY_EXPERIENCE_ORB_PICKUP")?.uppercase()
        val sound = try {
            Sound.valueOf(soundName ?: "ENTITY_EXPERIENCE_ORB_PICKUP")
        } catch (e: IllegalArgumentException) {
            plugin.logger.warning("Niepoprawna wartość dźwięku w konfiguracji: $soundName. Użycie domyślnego.")
            null
        }


        for (mentioned in mentionedPlayers) {

            sound?.let {
                mentioned.playSound(mentioned.location, it, 1.0f, 1.0f)
            }
            val titleComponent = messageHandler.getLogMessage("chat", "mention_title")
            val subtitleComponent = messageHandler.getLogMessage("chat", "mention", mapOf("player" to player.name))
            val times = Title.Times.times(
                Duration.ofMillis(500), // fadeIn
                Duration.ofSeconds(3),  // stay
                Duration.ofMillis(500)  // fadeOut
            )
            val title = Title.title(titleComponent, subtitleComponent, times)

            mentioned.showTitle(title)

        }
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

    private fun filterMessageContent(player: Player, message: String): String {
        val allowedMiniMessageColors = setOf(
            "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "red",
            "purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "pink", "yellow", "white"
        )
        val allowedMiniMessageFormats = setOf(
            "bold", "italic", "underlined", "strikethrough", "magic", "reset",
            "rainbow", "gradient", "click", "hover", "font", "insertion",
            "keybind", "translatable", "selector"
        )

        val legacyColors = setOf(
            "&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"
        )

        val legacyFormats = setOf(
            "&k", "&l", "&m", "&n", "&o", "&r"
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
                continue
            }

            if (char == '§' && i + 1 < message.length) {
                val token = message.substring(i, i + 2)
                if (legacyColors.contains(token) || legacyFormats.contains(token)) {
                    if (fpc.canUseColorToken(player, token) || fpc.canUseLegacyFormat(player, token)) {
                        filteredMessage.append(token)
                    }
                    i += 2
                    continue
                }
            }
            if (char == '&' && i + 1 < message.length) {
                val token = message.substring(i, i + 2)
                if (legacyColors.contains(token) || legacyFormats.contains(token)) {
                    if (fpc.canUseColorToken(player, token) || fpc.canUseLegacyFormat(player, token)) {
                        filteredMessage.append(token)
                    }
                    i += 2
                    continue
                }
            }

            if (char == '&' && (i + 1 >= message.length || !message[i + 1].lowercaseChar().isLetterOrDigit())) {
                filteredMessage.append("&")
                i++
                continue
            }

            if (char == '<') {
                val endIndex = message.indexOf('>', i)
                if (endIndex != -1) {
                    val token = message.substring(i, endIndex + 1)
                    val tagContent = token.substring(1, token.length - 1)
                    val tagName = tagContent.substringBefore(':').lowercase()

                    when {
                        allowedMiniMessageColors.contains(tagName) && fpc.canUseMinimessageColors(player) ->
                            filteredMessage.append(token)

                        allowedMiniMessageFormats.contains(tagName) && fpc.canUseMinimessageFormat(player, token) ->
                            filteredMessage.append(token)

                        fpc.canUseMiniPlaceholder(player) ->
                            filteredMessage.append(token)
                    }
                    i = endIndex + 1
                    continue
                }
            }

            if (char == '%' && message.indexOf('%', i + 1) != -1) {
                val endIndex = message.indexOf('%', i + 1)
                val token = message.substring(i, endIndex + 1)
                if (fpc.canUsePapi(player)) {
                    filteredMessage.append(token)
                }
                i = endIndex + 1
                continue
            }

            filteredMessage.append(char)
            i++
        }

        return filteredMessage.toString()
    }
}