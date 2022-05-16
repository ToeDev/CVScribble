package org.cubeville.cvscribble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.commons.utils.PlayerUtils;
import org.cubeville.cvscribble.CVScribble;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectWord extends BaseCommand {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public SelectWord() {
        super("select");
        addParameter("player", true, new CommandParameterString());
        addParameter("word", false, new CommandParameterString());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        Player player;
        if(parameters.containsKey("player")) {
            if(Bukkit.getPlayer((String) parameters.get("player")) == null) {
                throw new CommandExecutionException(ChatColor.GOLD + (String) parameters.get("player") + ChatColor.RED + " is not online!");
            }
            player = Bukkit.getPlayer((String) parameters.get("player"));
        } else {
            if(!(sender instanceof Player)) {
                return new CommandResponse(ChatColor.RED + "You cannot select a word from console!");
            }
            player = (Player) sender;
        }
        assert player != null;
        World world = player.getWorld();
        if(!PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(world, CVScribble.getInstance().getScribbleDrawingAreaRG()), world).contains(player)) {
            return new CommandResponse(red + player.getName() + " must be in the drawing booth to select a word!");
        }
        CVScribble.getInstance().setCurrentWord((String) parameters.get("word"));
        player.sendMessage(purple + "The word/phrase \"" + gold + parameters.get("word") + purple + "\" was set as the current Scribble word");
        return new CommandResponse("");
    }
}