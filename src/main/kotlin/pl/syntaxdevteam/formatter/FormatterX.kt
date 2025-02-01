package pl.syntaxdevteam.formatter

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.formatter.common.*
import java.io.File
import java.util.*

class FormatterX : JavaPlugin() {
    private val configHandler by lazy { ConfigHandler(this, "config.yml") }
    private val config: FileConfiguration = getConfig()
    var logger: Logger = Logger(this, config.getBoolean("debug"))
    lateinit var pluginsManager: PluginManager
    private lateinit var statsCollector: StatsCollector
    lateinit var messageHandler: MessageHandler
    private lateinit var updateChecker: UpdateChecker

    override fun onEnable() {
        configHandler.verifyAndUpdateConfig()
        messageHandler = MessageHandler(this).apply { initial() }
        pluginsManager = PluginManager(this)
        statsCollector = StatsCollector(this)
        updateChecker = UpdateChecker(this)
        updateChecker.checkForUpdates()
    }

    override fun onDisable() {

        // Plugin shutdown logic
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
