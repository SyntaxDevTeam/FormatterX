package pl.syntaxdevteam.formatter.basic

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class LegacyFormatter {

    fun format(text: String): Component {
        val formattedText = text.replace("&([0-9a-fk-or])".toRegex(), "§$1") // Zamiana & -> §
        return LegacyComponentSerializer.legacySection().deserialize(formattedText) // Konwersja na Component
    }
}

