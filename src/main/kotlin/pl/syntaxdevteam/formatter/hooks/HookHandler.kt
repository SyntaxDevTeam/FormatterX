package pl.syntaxdevteam.formatter.hooks

import net.luckperms.api.LuckPerms
import net.luckperms.api.cacheddata.CachedMetaData
import net.luckperms.api.model.user.User
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import pl.syntaxdevteam.formatter.FormatterX

/**
 * The [HookHandler] class is responsible for hooking into external services such as LuckPerms, Vault, and VaultUnlocked.
 * It allows retrieving player-specific data like group, prefix, and suffix information from the respective permission and chat services.
 * This class ensures that the plugin integrates smoothly with these services if they are available on the server.
 *
 * @property plugin The instance of the FormatterX plugin, used for logging messages and accessing other plugin functionalities.
 */
class HookHandler(private val plugin: FormatterX) {

    private var luckPerms: LuckPerms? = null
    private var chat: Chat? = null
    private var permission: Permission? = null

    init {
        checkPlaceholderAPI()
        checkLuckPerms()
        checkVault()
        if (chat == null || permission == null) {
            checkVaultUnlocked()
        }
    }

    /**
     * Checks if the LuckPerms plugin is enabled on the server and retrieves the LuckPerms API if available.
     * If LuckPerms is found, it is hooked into, and a success message is logged. If not, a warning is logged.
     */
    private fun checkLuckPerms() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
            if (provider != null) {
                luckPerms = provider.provider
                plugin.logger.success("Hooked into LuckPerms!")
            }
        } else {
            plugin.logger.warning("LuckPerms plugin not found on server!")
        }
    }

    /**
     * Checks if the Vault plugin is enabled on the server and retrieves the Vault chat and permission services if available.
     * If Vault is found, it is hooked into, and a success message is logged. If not, a warning is logged.
     */
    private fun checkVault() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            val chatProvider = Bukkit.getServicesManager().getRegistration(Chat::class.java)
            val permProvider = Bukkit.getServicesManager().getRegistration(Permission::class.java)

            if (chatProvider != null && permProvider != null) {
                chat = chatProvider.provider
                permission = permProvider.provider
                plugin.logger.success("Hooked into Vault!")
            }
        } else {
            plugin.logger.warning("Vault plugin not found on server!")
        }
    }

    /**
     * Checks if the VaultUnlocked plugin is enabled on the server and retrieves the VaultUnlocked chat and permission services if available.
     * If VaultUnlocked is found, it is hooked into, and a success message is logged. If not, a warning is logged.
     */
    private fun checkVaultUnlocked() {
        if (Bukkit.getPluginManager().isPluginEnabled("VaultUnlocked")) {
            val chatProvider = Bukkit.getServicesManager().getRegistration(Chat::class.java)
            val permProvider = Bukkit.getServicesManager().getRegistration(Permission::class.java)

            if (chatProvider != null && permProvider != null) {
                chat = chatProvider.provider
                permission = permProvider.provider
                plugin.logger.success("Hooked into VaultUnlocked!")
            }
        } else {
            plugin.logger.warning("VaultUnlocked plugin not found on server!")
        }
    }

    /**
     * Retrieves the primary group of a player from the LuckPerms or Vault service.
     * If neither service is available, the default group "default" is returned.
     *
     * @param player The player whose primary group is being retrieved.
     * @return The primary group of the player as a [String].
     */
    fun getPrimaryGroup(player: Player): String {
        val lpGroup = luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)?.primaryGroup
        if (lpGroup != null) {
            plugin.logger.debug("LuckPerms primary group for ${player.name}: $lpGroup")
            return lpGroup
        }
        val groups = permission?.getPrimaryGroup(player)
        if (!groups.isNullOrEmpty()) {
            plugin.logger.debug("Vault groups for ${player.name}: $groups")
            return groups
        }
        return "default"
    }

    /**
     * Retrieves the prefix of a player from the LuckPerms or Vault service.
     * If neither service is available, an empty string is returned.
     *
     * @param player The player whose prefix is being retrieved.
     * @return The prefix of the player as a [String].
     */
    fun getPlayerPrefix(player: Player): String {
        return luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)?.prefix
            ?: chat?.getPlayerPrefix(player)
            ?: ""
    }

    /**
     * Retrieves the suffix of a player from the LuckPerms or Vault service.
     * If neither service is available, an empty string is returned.
     *
     * @param player The player whose suffix is being retrieved.
     * @return The suffix of the player as a [String].
     */
    fun getPlayerSuffix(player: Player): String {
        return luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)?.suffix
            ?: chat?.getPlayerSuffix(player)
            ?: ""
    }

    /**
     * Retrieves a specific metadata value for a player from the LuckPerms service.
     * This is typically used to get additional player data that is not covered by basic prefix/suffix/group attributes.
     *
     * @param player The player whose metadata value is being retrieved.
     * @param key The key for the metadata value being retrieved.
     * @return The metadata value associated with the given key, or null if not found.
     */
    fun getLuckPermsMetaValue(player: Player, key: String): String? {
        val user: User? = luckPerms?.userManager?.getUser(player.uniqueId)
        return user?.cachedData?.metaData?.getMetaValue(key)
    }

    /**
     * Retrieves all metadata for a player from the LuckPerms service.
     * This is typically used to get all metadata values associated with a player.
     *
     * @param player The player whose metadata is being retrieved.
     * @return The metadata for the player as a [CachedMetaData] object, or null if not found.
     */
    fun getAllLuckPermsMetData(player: Player): CachedMetaData? {
        return luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)
    }

    /**
     * Checks if the PlaceholderAPI plugin is enabled on the server.
     * If PlaceholderAPI is found, a success message is logged, and true is returned.
     * If not, a warning is logged, and false is returned.
     *
     * @return True if PlaceholderAPI is found, false otherwise.
     */
    fun checkPlaceholderAPI(): Boolean {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.logger.success("Hooked into PlaceholderAPI!")
            return true
        } else {
            plugin.logger.warning("PlaceholderAPI plugin not found on server!")
            return false
        }
    }

    fun checkMiniPlaceholderAPI(): Boolean {
        if(Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            plugin.logger.success("Hooked into MiniPlaceholders!")
            return true
        } else {
            plugin.logger.warning("MiniPlaceholders plugin not found on server!")
            return false
        }
    }
}
