package pl.syntaxdevteam.formatter.basic

import io.papermc.paper.event.player.AsyncChatEvent
import pl.syntaxdevteam.formatter.common.MessageHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.formatter.FormatterX

class ChatFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        val defaultFormat = plugin.config.getString("chat.defaultFormat") ?: "[{prefix}]{displayname} » {message}"

        val messagePlain = PlainTextComponentSerializer.plainText()
            .serialize(event.message())

        val placeholderProcessor = ChatPlaceholderProcessor(plugin)
        val replacedText = placeholderProcessor.replacePlaceholders(event.player, defaultFormat)
            .replace("{message}", messagePlain)

        val finalComponent: Component = messageHandler.formatMixedTextToMiniMessage(replacedText)

        event.message(finalComponent)
    }
}

