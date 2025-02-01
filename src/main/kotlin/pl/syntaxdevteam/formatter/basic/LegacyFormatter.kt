@file:Suppress("DEPRECATION")

package pl.syntaxdevteam.formatter.basic

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.ChatColor

@Suppress("unused")
class LegacyFormatter {

    fun formatLegacyText(message: String): Component {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message)
    }

    fun formatLegacyTextBukkit(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    fun formatHexAndLegacyText(message: String): Component {

        val hexFormatted = message.replace("&#([a-fA-F0-9]{6})".toRegex()) {
            val hex = it.groupValues[1]
            "§x§${hex[0]}§${hex[1]}§${hex[2]}§${hex[3]}§${hex[4]}§${hex[5]}"
        }

        return LegacyComponentSerializer.legacySection().deserialize(hexFormatted)
    }

    fun miniMessageFormat(message: String): Component {
        return MiniMessage.miniMessage().deserialize(message)
    }

    fun getANSIText(component: Component): String {
        return ANSIComponentSerializer.ansi().serialize(component)
    }

    fun getPlainText(component: Component): String {
        return PlainTextComponentSerializer.plainText().serialize(component)
    }

    private fun convertLegacyToMiniMessage(message: String): String {
        return message
            .replace("&0", "<black>") // Czarny
            .replace("&1", "<dark_blue>") // Ciemnoniebieski
            .replace("&2", "<dark_green>") // Ciemnozielony
            .replace("&3", "<dark_aqua>") // Ciemny turkus
            .replace("&4", "<dark_red>") // Ciemnoczerwony
            .replace("&5", "<dark_purple>") // Ciemnofioletowy
            .replace("&6", "<gold>") // Złoty
            .replace("&7", "<gray>") // Szary
            .replace("&8", "<dark_gray>") // Ciemnoszary
            .replace("&9", "<blue>") // Niebieski
            .replace("&a", "<green>") // Zielony
            .replace("&b", "<aqua>") // Turkusowy
            .replace("&c", "<red>") // Czerwony
            .replace("&d", "<light_purple>") // Jasnofioletowy
            .replace("&e", "<yellow>") // Żółty
            .replace("&f", "<white>") // Biały
            .replace("&k", "<obfuscated>") // Efekt zakodowanego tekstu
            .replace("&l", "<bold>") // Pogrubienie
            .replace("&m", "<strikethrough>") // Przekreślenie
            .replace("&n", "<underlined>") // Podkreślenie
            .replace("&o", "<italic>") // Kursywa
            .replace("&r", "<reset>") // Resetowanie stylów
    }

    private fun convertHexToMiniMessage(message: String): String {
        return message.replace("&#([a-fA-F0-9]{6})".toRegex()) {
            val hex = it.groupValues[1]
            "<#$hex>" // Zmieniamy &#FF0000 na <#FF0000>
        }
    }

    fun formatMixedTextToMiniMessage(message: String): Component {
        var formattedMessage = convertLegacyToMiniMessage(message)
        formattedMessage = convertHexToMiniMessage(formattedMessage)

        return MiniMessage.miniMessage().deserialize(formattedMessage)
    }
}

