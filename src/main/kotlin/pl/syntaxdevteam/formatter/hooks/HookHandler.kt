package pl.syntaxdevteam.formatter.hooks

import net.luckperms.api.LuckPerms
import net.luckperms.api.model.user.User
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import pl.syntaxdevteam.formatter.FormatterX

class HookHandler(private val plugin: FormatterX) {
    private var luckPerms: LuckPerms? = null
    private var chat: Chat? = null
    private var permission: Permission? = null

    init {
        checkLuckPerms()
        checkVault()
        if (chat == null || permission == null) {
            checkVaultUnlocked()
        }
    }

    private fun checkLuckPerms() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
            if (provider != null) {
                luckPerms = provider.provider
                plugin.logger.success("Hooked into LuckPerms!")
            }
        }else{
            plugin.logger.warning("LuckPerms plugin not found on server!")
        }
    }

    private fun checkVault() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            val chatProvider = Bukkit.getServicesManager().getRegistration(Chat::class.java)
            val permProvider = Bukkit.getServicesManager().getRegistration(Permission::class.java)

            if (chatProvider != null && permProvider != null) {
                chat = chatProvider.provider
                permission = permProvider.provider
                plugin.logger.success("Hooked into Vault!")
            }
        }else{
            plugin.logger.warning("Vault plugin not found on server!")
        }
    }

    private fun checkVaultUnlocked() {
        if (Bukkit.getPluginManager().isPluginEnabled("VaultUnlocked")) {
            val chatProvider = Bukkit.getServicesManager().getRegistration(Chat::class.java)
            val permProvider = Bukkit.getServicesManager().getRegistration(Permission::class.java)

            if (chatProvider != null && permProvider != null) {
                chat = chatProvider.provider
                permission = permProvider.provider
                plugin.logger.success("Hooked into VaultUnlocked!")
            }
        }else{
            plugin.logger.warning("VaultUnlocked plugin not found on server!")
        }
    }

    fun getPrimaryGroup(player: Player): String {
        return luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)?.primaryGroup
            ?: permission?.getPrimaryGroup(player)
            ?: "default"
    }

    fun getPlayerPrefix(player: Player): String {
        return luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)?.prefix
            ?: chat?.getPlayerPrefix(player)
            ?: ""
    }

    fun getPlayerSuffix(player: Player): String {
        return luckPerms?.getPlayerAdapter(Player::class.java)?.getMetaData(player)?.suffix
            ?: chat?.getPlayerSuffix(player)
            ?: ""
    }

    fun getLuckPermsMetaValue(player: Player, key: String): String? {
        val user: User? = luckPerms?.userManager?.getUser(player.uniqueId)
        return user?.cachedData?.metaData?.getMetaValue(key)
    }
}
