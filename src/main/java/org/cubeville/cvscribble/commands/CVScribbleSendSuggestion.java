package org.cubeville.cvscribble.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CVScribbleSendSuggestion extends BaseCommand {

    public CVScribbleSendSuggestion() {
        super("sendsuggestion");
        addParameter("suggestion", false, new CommandParameterString());
        addParameter("player", true, new CommandParameterString());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        Player player;
        if (parameters.containsKey("player")) {
            if (Bukkit.getPlayer((String) parameters.get("player")) == null) {
                throw new CommandExecutionException(ChatColor.GOLD + (String) parameters.get("player") + ChatColor.RED + " is not online!");
            }
            player = Bukkit.getPlayer((String) parameters.get("player"));
        } else {
            if (!(sender instanceof Player)) {
                return new CommandResponse(ChatColor.RED + "You cannot suggest a command to console!");
            }
            player = (Player) sender;
        }
        TextComponent out = new TextComponent("");
        TextComponent click = new TextComponent(ChatColor.GREEN + "[" + ChatColor.BLUE + "CLICK HERE" + ChatColor.GREEN + "]");
        click.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, parameters.get("suggestion").toString()));
        click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Select a custom word/phrase").create()));
        TextComponent phrase = new TextComponent(ChatColor.GOLD + " to select a custom word/phrase.  Example: /scribble custom Oak Tree");
        out.addExtra(click);
        out.addExtra(phrase);
        assert player != null;
        player.spigot().sendMessage(out);
        return new CommandResponse("");
    }
}
