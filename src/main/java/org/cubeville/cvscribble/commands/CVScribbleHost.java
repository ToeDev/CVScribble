package org.cubeville.cvscribble.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.*;
import org.cubeville.cvscribble.CVScribble;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CVScribbleHost extends BaseCommand {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public CVScribbleHost() {
        super("host");
        addBaseParameter(new CommandParameterBoolean());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        Boolean status = CVScribble.getInstance().isHostedMode();
        if((status && (Boolean)baseParameters.get(0)) || (!status && !(Boolean)baseParameters.get(0))) {
            return new CommandResponse(red + "Scribble hosted mode is already set to " + gold + status);
        }
        CVScribble.getInstance().setHostedMode((Boolean) baseParameters.get(0));
        return new CommandResponse(purple + "Scribble hosted mode set to " + gold + baseParameters.get(0));
    }
}
