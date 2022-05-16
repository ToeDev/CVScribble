package org.cubeville.cvscribble.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvscribble.CVScribble;

import java.util.*;

public class CVScribbleEdit extends Command {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public CVScribbleEdit() {
        super("edit");
        addBaseParameter(new CommandParameterString());
        addBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        List<String> list = CVScribble.getInstance().getScribbleList();
        if(!list.contains(baseParameters.get(0).toString().toLowerCase())) {
            return new CommandResponse(red + "The word/phrase \"" + gold + baseParameters.get(0) + red + "\" does not exist!");
        }
        list.remove(baseParameters.get(0).toString().toLowerCase());
        list.add(baseParameters.get(1).toString().toLowerCase());
        CVScribble.getInstance().saveScribbleList();
        return new CommandResponse(purple + "The word/phrase \"" + gold + baseParameters.get(0) + purple + "\" was changed to \"" + gold + baseParameters.get(1) + purple + "\"");
    }
}
