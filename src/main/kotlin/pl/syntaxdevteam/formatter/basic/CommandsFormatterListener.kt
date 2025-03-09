package pl.syntaxdevteam.formatter.basic

import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.formatter.FormatterX
import pl.syntaxdevteam.formatter.common.MessageHandler
import pl.syntaxdevteam.formatter.hooks.HookHandler

class CommandsFormatterListener(
private val plugin: FormatterX,
private val messageHandler: MessageHandler,
private val hookHandler: HookHandler
) : Listener {
    private val fpc: FormatPermissionChecker = FormatPermissionChecker

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChat(event: AsyncChatCommandDecorateEvent) {

    }
}