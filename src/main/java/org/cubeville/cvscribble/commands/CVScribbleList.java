package org.cubeville.cvscribble.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvscribble.CVScribble;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public class CVScribbleList extends Command {

    public CVScribbleList() {
        super("list");
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        List<String> words = CVScribble.getInstance().getScribbleList();
        Collections.sort(words);
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
            player.spigot().sendMessage(o);
        }
        return new CommandResponse("");
    }
}
