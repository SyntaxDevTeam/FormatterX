package pl.syntaxdevteam.formatter.commands

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin
import pl.syntaxdevteam.formatter.FormatterX

@Suppress("UnstableApiUsage")
class CommandManager(private val plugin: FormatterX) {

    fun registerCommands() {
        val manager: LifecycleEventManager<Plugin> = plugin.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar()
            commands.register(
                "formatterx",
                "formatterx plugin command. Type /formatterx help to check available commands",
                FormatterXCommands(plugin)
            )
            commands.register(
                "ftx",
                "formatterx plugin command. Type /ftx help to check available commands",
                FormatterXCommands(plugin)
            )

            val aliases = plugin.config.getConfigurationSection("aliases")
            aliases?.getKeys(false)?.forEach { key ->
                val commandName = aliases.getString(key) ?: key
                when (key) {
                    "formatterx" -> commands.register(
                        commandName,
                        "Checking player penalties" + plugin.messageHandler.getSimpleMessage("check", "usage"),
                        FormatterXCommands(plugin)
                    )
                }
            }
        }
    }
}