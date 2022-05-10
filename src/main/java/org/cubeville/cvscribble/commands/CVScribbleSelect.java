package org.cubeville.cvscribble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.commons.utils.PlayerUtils;
import org.cubeville.cvscribble.CVScribble;
import org.cubeville.cvscribble.Word;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CVScribbleSelect extends BaseCommand {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public CVScribbleSelect() {
        super("select");
        addBaseParameter(new CommandParameterString());
        addParameter("word", false, new CommandParameterString());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        Player player = Bukkit.getPlayer((String) baseParameters.get(0));
        if(player == null || !player.isOnline()) {
            return new CommandResponse(red + "The player \"" + gold + baseParameters.get(0) + red + "\" is offline or non-existent!");
        }
        World world = player.getWorld();
        if(!PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(world, CVScribble.getInstance().getScribbleDrawingAreaRG()), world).contains(player)) {
            return new CommandResponse(red + "You must be in the drawing booth to select a word!");
        }
        Word
        return new CommandResponse("");
    }
}