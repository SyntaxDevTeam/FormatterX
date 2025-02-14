package pl.syntaxdevteam.formatter.basic

import org.bukkit.entity.Player

/**
 * Klasa [FormatPermissionChecker] jest odpowiedzialna za sprawdzanie uprawnień graczy do używania różnych funkcji formatowania.
 * Zawiera metody sprawdzające uprawnienia do używania kolorów, formatowania, tagów dynamicznych oraz tokenów kolorów.
 */
object FormatPermissionChecker {

    /**
     * Sprawdzenie uprawnień dla Legacy
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania Legacy
     */
    private fun canUseAnyLegacy(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy.all")
    }

    /**
     * Sprawdzenie uprawnień dla MiniMessage
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania MiniMessage
     */
    private fun canUseAnyMinimessage(player: Player): Boolean {
        return player.hasPermission("formatterx.minimessage.all")
    }

    /**
     * Sprawdzenie uprawnień dla kolorów (Legacy)
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania kolorów Legacy
     */
    private fun canUseLegacyColors(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy.color") || canUseAnyLegacy(player)
    }

    /**
     * Sprawdzenie uprawnień dla kolorów MiniMessage
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania kolorów MiniMessage
     */
    fun canUseMinimessageColors(player: Player): Boolean {
        return player.hasPermission("formatterx.minimessage.color") || canUseAnyMinimessage(player)
    }

    /**
     * Sprawdzenie uprawnień dla kolorów RGB
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania kolorów RGB
     */
    private fun canUseRgbColors(player: Player): Boolean {
        return player.hasPermission("formatterx.rgb") || canUseAnyMinimessage(player)
    }

    /**
     * Sprawdzenie uprawnień dla formatowania (Legacy)
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania formatowania
     */
    private fun canUseLegacyFormatting(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy.formatting") || canUseAnyLegacy(player)
    }

    /**
     * Sprawdzenie uprawnień dla formatowania (MiniMessage)
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania formatowania
     */
    private fun canUseMinimessageFormatting(player: Player): Boolean {
        return player.hasPermission("formatterx.minimessage.formatting") || canUseAnyMinimessage(player)
    }

    /**
     * Sprawdzenie uprawnień do użycia wszystkich typów placeholderów
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania placeholderów
     */
    private fun canUsePlaceholders(player: Player): Boolean {
        return player.hasPermission("formatterx.placeholder.all")
    }

    /**
     * Sprawdzenie uprawnień do użycia PlaceholderAPI
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania PlaceholderAPI
     */
    fun canUsePapi(player: Player): Boolean {
        return player.hasPermission("formatterx.placeholder.papi") || canUsePlaceholders(player)
    }

    /**
     * Sprawdzenie uprawnień do użycia MiniPlaceholder
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @return true, jeśli gracz ma uprawnienia do używania MiniPlaceholder
     */
    fun canUseMiniPlaceholder(player: Player): Boolean {
        return player.hasPermission("formatterx.placeholder.minipapi") || canUsePlaceholders(player)
    }

    /**
     * Sprawdzenie uprawnień dla kolorów (Legacy)
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @param colorToken Token koloru
     * @return true, jeśli gracz ma uprawnienia do używania danego koloru
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
     * Sprawdzenie uprawnień dla formatowania (Legacy)
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @param formatToken Token formatowania
     * @return true, jeśli gracz ma uprawnienia do używania danego formatowania
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
     * Sprawdzenie uprawnień dla formatowania (MiniMessage)
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @param formatToken Token formatowania
     * @return true, jeśli gracz ma uprawnienia do używania danego formatowania
     */
    fun canUseMinimessageFormat(player: Player, formatToken: String): Boolean {
        val minimessagePermissions = mapOf(
            "<bold>" to "formatterx.minimessage.bold",
            "<b>" to "formatterx.minimessage.bold",
            "<italic>" to "formatterx.minimessage.italic",
            "<em>" to "formatterx.minimessage.italic",
            "<i>" to "formatterx.minimessage.italic",
            "<underlined>" to "formatterx.minimessage.underline",
            "<strikethrough>" to "formatterx.minimessage.strikethrough",
            "<st>" to "formatterx.minimessage.strikethrough",
            "<obfuscated>" to "formatterx.minimessage.magic",
            "<obf>" to "formatterx.minimessage.magic",
            "<reset>" to "formatterx.minimessage.reset"
        )

        return minimessagePermissions[formatToken]?.let { player.hasPermission(it) } == true || canUseMinimessageFormatting(player)
    }

    /**
     * Sprawdzenie uprawnień dla dynamicznych tagów (MiniMessage)
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @param tagName Nazwa tagu
     * @return true, jeśli gracz ma uprawnienia do używania danego tagu
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
     * Sprawdzenie uprawnień dla tokena koloru
     * @param player Gracz, którego uprawnienia zostaną sprawdzone
     * @param token Token koloru
     * @return true, jeśli gracz ma uprawnienia do używania danego koloru
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