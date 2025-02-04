package pl.syntaxdevteam.formatter.basic

import org.bukkit.entity.Player
import pl.syntaxdevteam.formatter.FormatterX

class ChatPlaceholderProcessor(private val plugin: FormatterX) {

    fun replacePlaceholders(player: Player, message: String): String {
        return message
            .replace("{message}", message)
            .replace("{name}", player.name)
            .replace("{displayname}", player.displayName)
            .replace("{world}", player.world.name)
            .replace("{prefix}", "Prefix") // Tymczasowa wartość, później można dodać integrację z LuckPerms
            .replace("{suffix}", "Sufix") // Tymczasowa wartość
            .replace("{prefixes}", "FormatterX") // Placeholder dla listy prefiksów (w LuckPerms trzeba pobrać wszystkie prefiksy gracza)
            .replace("{suffixes}", "FormatterX") // Placeholder dla listy sufiksów
            .replace("{username-color}", "white") // Na razie stała wartość, w przyszłości można pobrać z LuckPerms lub configu
            .replace("{message-color}", "gray") // Podobnie jak powyżej
    }
}
