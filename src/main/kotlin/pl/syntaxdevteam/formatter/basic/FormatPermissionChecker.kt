package pl.syntaxdevteam.formatter.basic

import org.bukkit.entity.Player

/**
 * The [FormatPermissionChecker] class is responsible for verifying players' permissions to use various formatting features.
 * It contains methods that check permissions for colors, formatting, dynamic tags, and color tokens.
 */
object FormatPermissionChecker {

    /**
     * Checks permissions for Legacy
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use Legacy
     */
    private fun canUseAnyLegacy(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy.all")
    }

    /**
     * Checks permissions for MiniMessage
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use MiniMessage
     */
    private fun canUseAnyMinimessage(player: Player): Boolean {
        return player.hasPermission("formatterx.minimessage.all")
    }

    /**
     * Checks permissions for Legacy colors
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use Legacy colors
     */
    private fun canUseLegacyColors(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy.color") || canUseAnyLegacy(player)
    }

    /**
     * Checks permissions for MiniMessage colors
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use MiniMessage colors
     */
    fun canUseMinimessageColors(player: Player): Boolean {
        return /*player.hasPermission("formatterx.minimessage.color") || */ canUseAnyMinimessage(player)
    }

    /**
     * Checks permissions for RGB colors
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use RGB colors
     */
    private fun canUseRgbColors(player: Player): Boolean {
        return player.hasPermission("formatterx.rgb") || canUseAnyMinimessage(player)
    }

    /**
     * Checks permissions for Legacy formatting
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use Legacy formatting
     */
    private fun canUseLegacyFormatting(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy.formatting") || canUseAnyLegacy(player)
    }

    /**
     * Checks permissions for MiniMessage formatting
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use MiniMessage formatting
     */
    private fun canUseMinimessageFormatting(player: Player): Boolean {
        return player.hasPermission("formatterx.minimessage.formatting") || canUseAnyMinimessage(player)
    }

    /**
     * Checks permissions to use all types of placeholders
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use placeholders
     */
    private fun canUsePlaceholders(player: Player): Boolean {
        return player.hasPermission("formatterx.placeholder.all")
    }

    /**
     * Checks permissions to use PlaceholderAPI
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use PlaceholderAPI
     */
    fun canUsePapi(player: Player): Boolean {
        return player.hasPermission("formatterx.placeholder.papi") || canUsePlaceholders(player)
    }

    /**
     * Checks permissions to use MiniPlaceholder
     * @param player The player whose permissions will be checked
     * @return true if the player has permission to use MiniPlaceholder
     */
    fun canUseMiniPlaceholder(player: Player): Boolean {
        return player.hasPermission("formatterx.placeholder.minipapi") || canUsePlaceholders(player)
    }

    /**
     * Checks permissions for Legacy colors
     * @param player The player whose permissions will be checked
     * @param colorToken The color token
     * @return true if the player has permission to use the specified color
     */
    private fun canUseLegacyColor(player: Player, colorToken: String): Boolean {
        val legacyPermissions = mapOf(
            "&0" to "formatterx.legacy.black",
            "&1" to "formatterx.legacy.dark_blue",
            "&2" to "formatterx.legacy.dark_green",
            "&3" to "formatterx.legacy.dark_aqua",
            "&4" to "formatterx.legacy.dark_red",
            "&c" to "formatterx.legacy.red",
            "&5" to "formatterx.legacy.purple",
            "&6" to "formatterx.legacy.gold",
            "&7" to "formatterx.legacy.gray",
            "&8" to "formatterx.legacy.dark_gray",
            "&9" to "formatterx.legacy.blue",
            "&a" to "formatterx.legacy.green",
            "&b" to "formatterx.legacy.aqua",
            "&d" to "formatterx.legacy.pink",
            "&e" to "formatterx.legacy.yellow",
            "&f" to "formatterx.legacy.white"
        )

        return legacyPermissions[colorToken]?.let { player.hasPermission(it) } == true || canUseLegacyColors(player)
    }

    /**
     * Check permissions for formatting (Legacy)
     * @param player The player whose permissions will be checked
     * @param formatToken The formatting token
     * @return true if the player has permissions to use the formatting
     */
    fun canUseLegacyFormat(player: Player, formatToken: String): Boolean {
        val formatPermissions = mapOf(
            "&k" to "formatterx.legacy.magic",
            "&l" to "formatterx.legacy.bold",
            "&m" to "formatterx.legacy.strikethrough",
            "&n" to "formatterx.legacy.underline",
            "&o" to "formatterx.legacy.italic",
            "&r" to "formatterx.legacy.reset"
        )

        return formatPermissions[formatToken]?.let { player.hasPermission(it) } == true || canUseLegacyFormatting(player)
    }

    /**
     * Check permissions for formatting (MiniMessage)
     * @param player The player whose permissions will be checked
     * @param formatToken The formatting token
     * @return true if the player has permissions to use the given formatting
     */
    fun canUseMinimessageFormat(player: Player, formatToken: String): Boolean {

        val tagKey = if (formatToken.startsWith("<") && formatToken.endsWith(">")) {
            formatToken.substring(1, formatToken.length - 1)
                .substringBefore(':')
                .lowercase()
        } else {
            formatToken.lowercase()
        }

        val minimessagePermissions = mapOf(
            "bold" to "formatterx.minimessage.bold",
            "b" to "formatterx.minimessage.bold",
            "italic" to "formatterx.minimessage.italic",
            "em" to "formatterx.minimessage.italic",
            "i" to "formatterx.minimessage.italic",
            "underlined" to "formatterx.minimessage.underline",
            "strikethrough" to "formatterx.minimessage.strikethrough",
            "st" to "formatterx.minimessage.strikethrough",
            "obfuscated" to "formatterx.minimessage.magic",
            "obf" to "formatterx.minimessage.magic",
            "reset" to "formatterx.minimessage.reset",
            "rainbow" to "formatterx.minimessage.rainbow",
            "gradient" to "formatterx.minimessage.gradient",
            "click" to "formatterx.minimessage.click",
            "hover" to "formatterx.minimessage.hover",
            "font" to "formatterx.minimessage.font",
            "insertion" to "formatterx.minimessage.insertion",
            "keybind" to "formatterx.minimessage.keybind",
            "transition" to "formatterx.minimessage.transition",
            "translatable" to "formatterx.minimessage.translatable",
            "selector" to "formatterx.minimessage.selector"
        )

        return minimessagePermissions[tagKey]?.let { player.hasPermission(it) } == true || canUseMinimessageFormatting(player)
    }


    /**
     * Check permissions for dynamic tags (MiniMessage)
     * @param player The player whose permissions will be checked
     * @param tagName The tag name
     * @return true if the player has permissions to use the given tag
     */
    private fun canUseDynamicTag(player: Player, tagName: String): Boolean {
        val dynamicTagPermissions = mapOf(
            "hover" to "formatterx.minimessage.hover",
            "click" to "formatterx.minimessage.click",
            "gradient" to "formatterx.minimessage.gradient",
            "rainbow" to "formatterx.minimessage.rainbow",
            "transition" to "formatterx.minimessage.transition",
            "font" to "formatterx.minimessage.font",
            "insertion" to "formatterx.minimessage.insertion",
            "keybind" to "formatterx.minimessage.keybind",
            "translatable" to "formatterx.minimessage.translatable",
            "selector" to "formatterx.minimessage.selector"
        )

        return dynamicTagPermissions[tagName]?.let { player.hasPermission(it) } == true || canUseAnyMinimessage(player)
    }

    /**
     * Check permissions for color tokens
     * @param player The player whose permissions will be checked
     * @param token The color token
     * @return true if the player has permissions to use the given color
     */
    fun canUseColorToken(player: Player, token: String): Boolean {
        return when {
            token.startsWith("&") -> canUseLegacyColor(player, token)
            token.startsWith("&#") -> canUseRgbColors(player)
            token.startsWith("<") && token.endsWith(">") -> {
                val tagContent = token.substring(1, token.length - 1)
                val tagName = tagContent.substringBefore(':').lowercase()
                canUseDynamicTag(player, tagName)
            }
            else -> true
        }
    }
}