package pl.syntaxdevteam.formatter

import net.luckperms.api.LuckPerms
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.formatter.basic.ChatFormatterListener
import pl.syntaxdevteam.formatter.common.*
import java.io.File
import java.util.*

class FormatterX : JavaPlugin() {
    private val config: FileConfiguration = getConfig()
    var logger: Logger = Logger(this, config.getBoolean("debug"))
    lateinit var pluginsManager: PluginManager
    private lateinit var statsCollector: StatsCollector
    private lateinit var messageHandler: MessageHandler
    private lateinit var chatListener: ChatFormatterListener
    private lateinit var updateChecker: UpdateChecker
    private var luckPerms: LuckPerms? = null

    override fun onEnable() {
        luckPerms = server.servicesManager.load(LuckPerms::class.java)
        saveDefaultConfig()
        messageHandler = MessageHandler(this).apply { initial() }
        chatListener = ChatFormatterListener(this, messageHandler)
        server.pluginManager.registerEvents(chatListener, this)
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
