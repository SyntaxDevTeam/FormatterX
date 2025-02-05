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

class ChatFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler
) : Listener {
    private val luckPerms = plugin.luckPerms
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val player = event.player
        val messageContent = PlainTextComponentSerializer.plainText().serialize(event.message())

        val defaultFormat = plugin.config.getString("chat.defaultFormat")
            ?: "${messageHandler.getPrefix()} {displayname} » {message}"

        val metaData = luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)

        val prefix = metaData?.prefix ?: ""
        val suffix = metaData?.suffix ?: ""

        val prefixes = metaData?.prefixes?.entries
            ?.sortedByDescending { it.key }
            ?.joinToString(" ") { it.value } ?: ""

        val suffixes = metaData?.suffixes?.entries
            ?.sortedByDescending { it.key }
            ?.joinToString(" ") { it.value } ?: ""

        val usernameColor = metaData?.getMetaValue("username-color") ?: "<white>"
        val messageColor = metaData?.getMetaValue("message-color") ?: "<gray>"

        val displayName = PlainTextComponentSerializer.plainText().serialize(player.displayName())
        val playerName = player.name
        val worldName = player.world.name

        val formattedMessage = defaultFormat
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

        val finalComponent: Component = messageHandler.formatMixedTextToMiniMessage(formattedMessage)

        Bukkit.getServer().sendMessage(finalComponent)
    }
}
