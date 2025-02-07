package pl.syntaxdevteam.formatter.basic

import io.papermc.paper.event.player.AsyncChatEvent
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

class ChatFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler,
    private val hookHandler: HookHandler
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val player = event.player
        val messageContent = PlainTextComponentSerializer.plainText().serialize(event.message())
        val group = hookHandler.getPrimaryGroup(player)
        val format = generateChatFormat(player, group, messageContent)
        val finalComponent: Component = messageHandler.formatMixedTextToMiniMessage(format)

        Bukkit.getServer().sendMessage(finalComponent)
    }

    private fun generateChatFormat(player: Player, group: String, messageContent: String): String {
        val prefix = hookHandler.getPlayerPrefix(player)
        val suffix = hookHandler.getPlayerSuffix(player)
        val usernameColor = hookHandler.getLuckPermsMetaValue(player, "username-color") ?: "<white>"
        val messageColor = hookHandler.getLuckPermsMetaValue(player, "message-color") ?: "<gray>"

        val displayName = PlainTextComponentSerializer.plainText().serialize(player.displayName())
        val playerName = player.name
        val worldName = player.world.name

        return (plugin.config.getString("chat.group-formats.$group") ?: plugin.config.getString("chat.defaultFormat") ?: "${messageHandler.getPrefix()} {displayname} » {message}")
            .replace("{prefix}", prefix)
            .replace("{suffix}", suffix)
            .replace("{displayname}", displayName)
            .replace("{name}", playerName)
            .replace("{world}", worldName)
            .replace("{username-color}", usernameColor)
            .replace("{message-color}", messageColor)
            .replace("{message}", messageContent)
    }
}
