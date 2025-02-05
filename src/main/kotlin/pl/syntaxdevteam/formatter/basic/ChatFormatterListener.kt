package pl.syntaxdevteam.formatter.basic

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.formatter.FormatterX
import pl.syntaxdevteam.formatter.common.MessageHandler

class ChatFormatterListener(
    private val plugin: FormatterX,
    private val messageHandler: MessageHandler
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true // Usuwamy domyślne formatowanie czatu!

        val player = event.player
        val messageContent = PlainTextComponentSerializer.plainText().serialize(event.message())

        // Pobieramy szablon z configu
        val defaultFormat = plugin.config.getString("chat.defaultFormat")
            ?: "[{prefix}]{displayname} <red>» {message}</red>"

        // Pobieramy dane gracza
        val displayName = PlainTextComponentSerializer.plainText().serialize(player.displayName())
        val playerName = player.name
        val worldName = player.world.name

        // Tymczasowe wartości prefix/suffix (później można podpiąć LuckPerms)
        val prefix = "FormatterX"
        val suffix = "FormatterX"

        // Składamy finalny tekst zgodnie z szablonem
        val formattedMessage = defaultFormat
            .replace("{prefix}", prefix)
            .replace("{suffix}", suffix)
            .replace("{displayname}", displayName)
            .replace("{name}", playerName)
            .replace("{world}", worldName)
            .replace("{message}", messageContent)

        // Przetwarzamy sformatowany tekst do MiniMessage
        val finalComponent: Component = messageHandler.formatMixedTextToMiniMessage(formattedMessage)

        // Wysyłamy wiadomość na czat dla wszystkich graczy
        Bukkit.getServer().sendMessage(finalComponent)
    }
}
