package pl.syntaxdevteam.formatter.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.formatter.FormatterX

@Suppress("UnstableApiUsage", "DEPRECATION")
class FormatterXCommands(private val plugin: FormatterX) : BasicCommand {
    private val mH = plugin.messageHandler

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        if (!stack.sender.hasPermission("formatterx.cmd.ftx")) {
            stack.sender.sendMessage(plugin.messageHandler.getMessage("error", "no_permission"))
            return
        }
        val pluginMeta = (plugin as LifecycleEventOwner).pluginMeta
        val pdf = plugin.description
        if (args.isNotEmpty()) {
            when {
                args[0].equals("help", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("formatterx.cmd.help")) {
                        val page = args.getOrNull(1)?.toIntOrNull() ?: 1
                        sendHelp(stack, page)
                    } else {
                        stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                    }
                }
                args[0].equals("version", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("formatterx.cmd.version")) {
                        stack.sender.sendMessage(mH.miniMessageFormat("\n <gray>+-------------------------------------------------\n" +
                                " <gray>|\n" +
                                " <gray>|   <gold>→ <bold>" + pluginMeta.name + "</bold> ←\n" +
                                " <gray>|   <white>Author: <bold><gold>" + pdf.authors + "</gold></bold>\n" +
                                " <gray>|   <white>Website: <bold><gold><click:open_url:'" + pdf.website + "'>"  + pdf.website + "</click></gold></bold>\n" +
                                " <gray>|   <white>Version: <bold><gold>" + pluginMeta.version + "</gold></bold>\n" +
                                " <gray>|" +
                                "\n<gray>+-------------------------------------------------"))
                    } else {
                        stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                    }
                }
                args[0].equals("reload", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("formatterx.cmd.reload")) {
                        plugin.onReload()
                        stack.sender.sendMessage(mH.getMessage("formatterx", "reload"))
                    } else {
                        stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                    }
                }
            }
        } else {
            stack.sender.sendMessage(mH.miniMessageFormat("<green>Type </green><gold>/formatterx help</gold> <green>to see available commands</green>"))
        }
    }

    private fun sendHelp(stack: CommandSourceStack, page: Int) {
        val commands = listOf(
            "  <gold>/formatterx help <gray>- <white>Displays this prompt.",
            "  <gold>/formatterx version <gray>- <white>Shows plugin info.",
            "  <gold>/formatterx reload <gray>- <white>Reloads the configuration file.",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " "
        )

        val itemsPerPage = 12
        val totalPages = (commands.size + itemsPerPage - 1) / itemsPerPage
        val currentPage = page.coerceIn(1, totalPages)

        stack.sender.sendMessage(mH.miniMessageFormat(" <gray>+-------------------------------------------------"))
        stack.sender.sendMessage(mH.miniMessageFormat(" <gray>|    <gold>Available commands for ${plugin.pluginMeta.name}:"))
        stack.sender.sendMessage(mH.miniMessageFormat(" <gray>|"))

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = (currentPage * itemsPerPage).coerceAtMost(commands.size)
        for (i in startIndex until endIndex) {
            stack.sender.sendMessage(mH.miniMessageFormat(" <gray>|  ${commands[i]}"))
        }

        val prevPage = if (currentPage > 1) currentPage - 1 else totalPages
        val nextPage = if (currentPage < totalPages) currentPage + 1 else 1
        stack.sender.sendMessage(mH.miniMessageFormat(" <gray>|"))
        stack.sender.sendMessage(mH.miniMessageFormat(" <gray>|"))
        stack.sender.sendMessage(mH.miniMessageFormat(
            " <gray>| (Page $currentPage/$totalPages) <click:run_command:'/ftx help $prevPage'><white>[Previous]</white></click>   " +
                    "<click:run_command:'/ftx help $nextPage'><white>[Next]</white></click>"
        ))
        stack.sender.sendMessage(mH.miniMessageFormat(" <gray>|"))
        stack.sender.sendMessage(mH.miniMessageFormat(" <gray>+-------------------------------------------------"))
    }

    override fun suggest(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>): List<String> {
        return if (stack.sender.hasPermission("legacyteamx.cmd.ftx")) {

            when (args.size) {
                1 -> listOf("help", "version", "reload")
                else -> emptyList()
            }
        } else {
            emptyList()
        }
    }
}
