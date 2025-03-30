package pl.syntaxdevteam.formatter.basic

import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import pl.syntaxdevteam.formatter.FormatterX
import pl.syntaxdevteam.formatter.common.MessageHandler
import pl.syntaxdevteam.formatter.hooks.HookHandler
import java.util.concurrent.ConcurrentHashMap

class CommandsFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler,
    private val hookHandler: HookHandler
) : Listener {

    private val resolverCache = ConcurrentHashMap<Player, TagResolver>()

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPrivateMessage(event: PlayerCommandPreprocessEvent) {

        val player = event.player
        val messageParts = event.message.split(" ", limit = 3)
        if (messageParts.size < 3) return
        /* ---------------------------------------------- */
        val command = messageParts[0].lowercase()
        val privateMessageCommands = plugin.config.getStringList("private-message-commands")
            .ifEmpty { listOf("/msg", "/whisper", "/tell", "/w", "/r") }

        if (command !in privateMessageCommands.map { it.lowercase() }) return
        if (!player.hasPermission("formatterx.chat.private-message")) {
            return
        }
        /*----------------------------------------------- */

        event.isCancelled = true

        val targetPlayer = getPlayerByNameOrDisplayName(messageParts[1]) ?: return
        val messageContent = PlainTextComponentSerializer.plainText().serialize(Component.text(messageParts[2]))

        val senderFormat = generatePrivateMessageFormat(player, targetPlayer, messageContent, true)
        val receiverFormat = generatePrivateMessageFormat(player, targetPlayer, messageContent, false)

        val senderResolver = getResolver(player)
        val receiverResolver = getResolver(targetPlayer)

        val senderComponent: Component = messageHandler.formatMixedTextToMiniMessage(senderFormat, senderResolver)
        val receiverComponent: Component = messageHandler.formatMixedTextToMiniMessage(receiverFormat, receiverResolver)

        player.sendMessage(senderComponent)
        targetPlayer.sendMessage(receiverComponent)

    }

    private fun generatePrivateMessageFormat(
        sender: Player, receiver: Player, message: String, isSender: Boolean
    ): String {

        val senderName = sender.name
        val receiverName = receiver.name

        val formatKey = if (isSender) "chat.private-message.sent" else "chat.private-message.received"
        val format = plugin.config.getString(formatKey, "<gray>[PM] {sender} -> {receiver}: {message}</gray>") ?: "<gray>[PM] {sender} -> {receiver}: {message}</gray>"

        return format
            .replace("{sender}", senderName)
            .replace("{receiver}", receiverName)
            .replace("{message}", message)
    }

    private fun getResolver(player: Player): TagResolver {
        return resolverCache.computeIfAbsent(player) {
            if (plugin.hookHandler.checkMiniPlaceholderAPI()) {
                TagResolver.resolver(MiniPlaceholders.getAudiencePlaceholders(player))
            } else {
                TagResolver.empty()
            }
        }
    }

    private fun getPlayerByNameOrDisplayName(name: String): Player? {
        Bukkit.getPlayerExact(name)?.let { return it }

        val essentials = hookHandler.checkEssentialsX()
        if (essentials != null) {
            for (player in Bukkit.getOnlinePlayers()) {
                val user = essentials.getUser(player)
                val essentialsNick = user?.nickname
                if (essentialsNick != null && essentialsNick.equals(name, ignoreCase = true)) {
                    return player
                }
            }
        }

        for (player in Bukkit.getOnlinePlayers()) {
            val displayName = PlainTextComponentSerializer.plainText().serialize(player.displayName())
            if (displayName.equals(name, ignoreCase = true)) {
                return player
            }
        }

        return null
    }

}
