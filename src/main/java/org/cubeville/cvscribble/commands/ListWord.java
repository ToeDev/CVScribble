package org.cubeville.cvscribble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.cvscribble.CVScribble;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public class ListWord extends BaseCommand {

    public ListWord() {
        super("list");
        setPermission("cvscribble.admin");
        addParameter("player", true, new CommandParameterString());
        addParameter("game", true, new CommandParameterBoolean());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        Player player;
        List<String> words = CVScribble.getInstance().getScribbleList();
        Collections.sort(words);
        if(parameters.containsKey("player")) {
            if(Bukkit.getPlayer((String) parameters.get("player")) == null) {
                throw new CommandExecutionException(ChatColor.GOLD + (String) parameters.get("player") + ChatColor.RED + " is not online!");
            }
            player = Bukkit.getPlayer((String) parameters.get("player"));
        } else {
            if(!(sender instanceof Player)) {
                List<String> out = new ArrayList<>();
                out.add("§6===================§aWords/Phrases§6===================");
                String list = "";
                int i = words.size();
                for(String word : words) {
                    i--;
                    if(i >= i) word = word.concat(" §c|| ");
                    list = list.concat(word);
                }
                out.add(list);
                for(String o : out) {
                    sender.sendMessage(o);
                }
                return new CommandResponse("");
            }
            player = (Player) sender;
        }
        List<TextComponent> out = new ArrayList<>();
        out.add(new TextComponent("§6===================§aWords/Phrases§6==================="));
        TextComponent list = new TextComponent("");
        int i = words.size();
        for(String word : words) {
            TextComponent w = new TextComponent(word);
            if(parameters.get("game") != null && parameters.get("game").equals(true)) {
                w.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/scribble select word:\"" + word + "\" player:" +player.getName()));
                w.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Select " + word).create()));
            } else {
                w.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/scribble edit \"" + word + "\" "));
                w.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Edit " + word).create()));
            }
            i--;
            if(i >= 1) w.addExtra(" §c|| ");
            list.addExtra(w);
        }
        out.add(list);
        for(TextComponent o : out) {
            player.spigot().sendMessage(o);
        }
        return new CommandResponse("");
    }
}
