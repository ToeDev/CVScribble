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

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CVScribbleStart extends BaseCommand {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public CVScribbleStart() {
        super("start");
        addBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        Player player = Bukkit.getPlayer((String) baseParameters.get(0));
        if(player == null || !player.isOnline()) {
            return new CommandResponse(red + "The player \"" + gold + baseParameters.get(0) + red + "\" is offline or non-existent!");
        }
        World world = player.getWorld();
        if(PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(world, CVScribble.getInstance().getScribbleDrawingAreaRG()), world).size() > 0) {
            return new CommandResponse(red + "Someone is currently drawing! Check back later");
        }
        return startDrawing(player);
    }

    public CommandResponse startDrawing(Player player) {

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, "cvportal trigger " + CVScribble.getInstance().getScribbleDrawingPortalEnter() + " player:" + player.getName() + " force");
        player.sendTitle("Be courteous", "Don't afk and please take turns", 5, 40, 5);
        player.sendMessage("Use the left and right mouse buttons to draw. With your inventory, you can change colours, and the sponge erases the board.");
        Bukkit.dispatchCommand(console, "loadout apply scribble player:" + player.getName());
        Bukkit.dispatchCommand(console, "menu display scribble player:" + player.getName());
        return new CommandResponse(gold + player.getName() + purple + " has started Scribble");
    }
}