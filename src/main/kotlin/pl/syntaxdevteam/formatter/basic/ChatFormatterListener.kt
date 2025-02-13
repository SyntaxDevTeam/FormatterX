package pl.syntaxdevteam.formatter.basic

import io.github.miniplaceholders.api.MiniPlaceholders
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.formatter.FormatterX
import pl.syntaxdevteam.formatter.common.MessageHandler
import pl.syntaxdevteam.formatter.hooks.HookHandler

class ChatFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler,
    private val hookHandler: HookHandler
) : Listener {
    private val fpc: FormatPermissionChecker = FormatPermissionChecker

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val player: Player = event.player
        val messageContent = PlainTextComponentSerializer.plainText().serialize(event.message())
        val group = hookHandler.getPrimaryGroup(player)
        val format = generateChatFormat(player, group, messageContent)
        plugin.logger.debug("Chat format: $format")

        val resolver = if (hookHandler.checkMiniPlaceholderAPI()) {
            TagResolver.resolver(
                MiniPlaceholders.getGlobalPlaceholders(),
                MiniPlaceholders.getAudiencePlaceholders(player)
            )
        } else {
            TagResolver.empty()
        }

        val finalComponent: Component = messageHandler.formatMixedTextToMiniMessage(format, resolver)
        Bukkit.getServer().sendMessage(finalComponent)
    }

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

        if (hookHandler.checkPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format)
        }
        return format
    }

    /**
     * Filterring message content based on player permissions
     * @param player Player who sent the message
     * @param message Message content
     * @return Filtered message content
     */
    private fun filterMessageContent(player: Player, message: String): String {
        val filteredMessage = StringBuilder()
        var i = 0

        while (i < message.length) {
            val char = message[i]
            if (char == '&' && i + 1 < message.length) {
                val token = message.substring(i, i + 2)
                if (fpc.canUseColorToken(player, token) || fpc.canUseLegacyFormat(player, token)) {
                    filteredMessage.append(token)
                }
                i += 2
            } else if (char == '<' && message.indexOf('>', i) != -1) {
                val endIndex = message.indexOf('>', i)
                val token = message.substring(i, endIndex + 1)
                if (fpc.canUseColorToken(player, token) || fpc.canUseMinimessageFormat(player, token) || fpc.canUseMinimessageColors(player)) {
                    filteredMessage.append(token)
                }
                i = endIndex + 1
            } else {
                filteredMessage.append(char)
                i++
            }
        }

        return filteredMessage.toString()
    }
}