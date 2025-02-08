package pl.syntaxdevteam.formatter.basic

import org.bukkit.entity.Player

object FormatPermissionChecker {

    // Ogólne uprawnienie do używania kolorów (wszystkich)
    fun canUseAnyColor(player: Player): Boolean {
        return player.hasPermission("formatterx.color.*")
    }

    // Używanie legacy kolorów (np. &0 - &f, &k, &l, itd.)
    fun canUseLegacyColors(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy")
    }

    // Używanie kolorów RGB (np. &#FF00FF)
    fun canUseRgbColors(player: Player): Boolean {
        return player.hasPermission("formatterx.rgb")
    }

    // Używanie kolorów MiniMessage (np. <red>, <#FF00FF>)
    fun canUseMinimessageColors(player: Player): Boolean {
        return player.hasPermission("formatterx.minimessage.color")
    }

    // Sprawdzanie uprawnień dla konkretnego legacy koloru, np. czerwony
    fun canUseLegacyColor(player: Player, colorToken: String): Boolean {
        // Przykładowo: jeśli token to "&c" lub "&4", mogę wymagać uprawnienia "formatterx.red"
        // Mogę zdefiniować mapę, która przypisuje tokeny do konkretnych uprawnień.
        return when (colorToken.lowercase()) {
            "&c", "&4" -> player.hasPermission("formatterx.red")
            "&a" -> player.hasPermission("formatterx.green")
            // TODO: Dodać kolejne przypadki dla innych kolorów...
            else -> canUseLegacyColors(player)
        }
    }

    // Metoda ogólna, która na podstawie tokenu decyduje, jaką kontrolę uprawnień wykonać.
    fun canUseColorToken(player: Player, token: String): Boolean {
        return when {
            token.startsWith("&") -> {
                // Jeśli jest to legacy token, sprawdzam szczegółowo lub ogólnie
                canUseLegacyColor(player, token)
            }
            token.startsWith("&#") -> {
                // Token RGB – wystarczy sprawdzić uprawnienie formatterx.rgb
                canUseRgbColors(player)
            }
            token.startsWith("<") && token.endsWith(">") -> {
                // Token MiniMessage – sprawdzam uprawnienie formatterx.minimessage.color
                canUseMinimessageColors(player)
            }
            else -> true
        }
    }

    // Uprawnienia do formatowania
    fun canUseAnyFormat(player: Player): Boolean {
        return player.hasPermission("formatterx.format.*")
    }

    fun canUseLegacyFormat(player: Player): Boolean {
        return player.hasPermission("formatterx.legacy.format")
    }

    fun canUseBold(player: Player): Boolean {
        return player.hasPermission("formatterx.bold")
    }

    fun canUseItalic(player: Player): Boolean {
        return player.hasPermission("formatterx.italic")
    }

    // TODO: rozszerzać o kolejne metody, np. underline, strikethrough itd.
}
