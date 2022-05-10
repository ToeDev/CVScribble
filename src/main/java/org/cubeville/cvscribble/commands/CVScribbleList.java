package org.cubeville.cvscribble.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvscribble.CVScribble;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public class CVScribbleList extends BaseCommand {

    public CVScribbleList() {
        super("list");
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        List<String> words = CVScribble.getInstance().getScribbleList();
        Collections.sort(words);
        if(sender instanceof Player) {
            List<TextComponent> out = new ArrayList<>();
            out.add(new TextComponent("§6===================§aWords/Phrases§6==================="));
            TextComponent list = new TextComponent("");
            int i = words.size();
            for(String word : words) {
                TextComponent w = new TextComponent(word);
                w.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/scribble edit \"" + word + "\" "));
                w.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Edit " + word).create()));
                i--;
                if(i >= 1) w.addExtra(" §c|| ");
                list.addExtra(w);
            }
            out.add(list);
            for(TextComponent o : out) {
                sender.spigot().sendMessage(o);
            }
        } else {
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
        }
        return new CommandResponse("");
    }
}
