package pl.syntaxdevteam.formatter

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.formatter.basic.ChatFormatterListener
import pl.syntaxdevteam.formatter.basic.CommandsFormatterListener
import pl.syntaxdevteam.formatter.commands.CommandManager
import pl.syntaxdevteam.formatter.common.*
import pl.syntaxdevteam.formatter.hooks.HookHandler
import java.io.File
import java.util.*

@Suppress("UnstableApiUsage")
class FormatterX : JavaPlugin() {
    private val config: FileConfiguration = getConfig()
    var logger: Logger = Logger(this, config.getBoolean("debug"))
    lateinit var pluginsManager: PluginManager
    private lateinit var statsCollector: StatsCollector
    lateinit var messageHandler: MessageHandler
    private lateinit var updateChecker: UpdateChecker
    private lateinit var hookHandler: HookHandler
    private lateinit var commandManager: CommandManager

    override fun onEnable() {
        saveDefaultConfig()
        hookHandler = HookHandler(this)
        messageHandler = MessageHandler(this).apply { initial() }
        server.pluginManager.registerEvents(ChatFormatterListener(this, messageHandler, hookHandler), this)
        server.pluginManager.registerEvents(CommandsFormatterListener(this, messageHandler, hookHandler), this)
        registerCommands()
        pluginsManager = PluginManager(this)
        statsCollector = StatsCollector(this)
        updateChecker = UpdateChecker(this)
        updateChecker.checkForUpdates()
    }

    fun onReload() {
        reloadConfig()

        logger.debug("The configuration file has been reloaded.")
    }

    override fun onDisable() {
        logger.err(pluginMeta.name + " " + pluginMeta.version + " has been disabled ☹️")
    }

    /**
     * Registers the plugin commands.
     */
    private fun registerCommands(){
        commandManager = CommandManager(this)
        commandManager.registerCommands()
    }

    /**
     * Retrieves the plugin file.
     *
     * @return The plugin file.
     */
    fun getPluginFile(): File {
        return this.file
    }

    /**
     * Retrieves the server name from the server.properties file.
     *
     * @return The server name, or "Unknown Server" if not found.
     */
    fun getServerName(): String {
        val properties = Properties()
        val file = File("server.properties")
        if (file.exists()) {
            properties.load(file.inputStream())
            val serverName = properties.getProperty("server-name")
            if (serverName != null) {
                return serverName
            } else {
                logger.debug("Property 'server-name' not found in server.properties file.")
            }
        } else {
            logger.debug("The server.properties file does not exist.")
        }
        return "Unknown Server"
    }
}
