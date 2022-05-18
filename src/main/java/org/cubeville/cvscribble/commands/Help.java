package org.cubeville.cvscribble.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Help extends Command {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public Help() {
        super("help");
        setPermission("cvscribble.staff");
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        List<String> out = new ArrayList<>();
        out.add(purple + "=================" + gold + "Scribble Commands" + purple + "=================");

        out.add(gold + "/scribble hostmenu" + purple + " - Open a menu/gui for hosting");
        out.add(gold + "/scribble list" + purple + " - List all defined Scribble words or phrases");

        if(player.hasPermission("cvscribble.admin")) {
            out.add(gold + "/scribble add <word/phrase>" + purple + " - Add a word or phrase to the Scribble list");
            out.add(gold + "/scribble remove <word/phrase>" + purple + " - Remove a word or phrase from the Scribble list");
            out.add(gold + "/scribble edit <current word/phrase> <updated word/phrase>" + purple + " - Edit a word or phrase on the Scribble list");
        }

        if(player.hasPermission("cvscribble.scribbleclear")) {
            out.add(gold + "/scribbleclear" + purple + " - Erase the Scribble board");
        }

        for(String o : out) {
            player.sendMessage(o);
        }
        return new CommandResponse("");
    }
}