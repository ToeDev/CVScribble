package org.cubeville.cvscribble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.commons.utils.PlayerUtils;
import org.cubeville.cvscribble.CVScribble;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostCommands extends BaseCommand {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public HostCommands() {
        super("host");
        setPermission("cvscribble.staff");
        addBaseParameter(new CommandParameterString());
        addBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        if(baseParameters.get(0).equals("status")) {
            boolean status = CVScribble.getInstance().isHostedMode();
            if(!((String) baseParameters.get(1)).equalsIgnoreCase("true") && !((String) baseParameters.get(1)).equalsIgnoreCase("false")) {
                return new CommandResponse(gold + baseParameters.get(1).toString() + red + " is not a boolean! Use true/false");
            }
            if((status && Boolean.parseBoolean((String) baseParameters.get(1))) || (!status && !Boolean.parseBoolean((String) baseParameters.get(1)))) {
                return new CommandResponse(red + "Scribble hosted mode is already set to " + gold + status);
            }
            CVScribble.getInstance().setHostedMode(Boolean.parseBoolean((String) baseParameters.get(1)));
            return new CommandResponse(purple + "Scribble hosted mode set to " + gold + baseParameters.get(1));
        } else if(baseParameters.get(0).equals("start")) {
            Player player = Bukkit.getPlayer((String) baseParameters.get(1));
            if(player == null || !player.isOnline()) {
                return new CommandResponse(red + "The player \"" + gold + baseParameters.get(0) + red + "\" is offline or non-existent!");
            }
            World world = player.getWorld();
            if(PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(world, CVScribble.getInstance().getScribbleDrawingAreaRG()), world).size() > 0) {
                return new CommandResponse(red + "Someone is currently drawing! Remove them before sending a new person to the booth!");
            }
            return startDrawing(player);
        } else if(baseParameters.get(0).equals("remove")) {
            Player player = Bukkit.getPlayer((String) baseParameters.get(1));
            if(player == null || !player.isOnline()) {
                return new CommandResponse(red + "The player \"" + gold + baseParameters.get(1) + red + "\" is offline or non-existent!");
            }
            World world = player.getWorld();
            if(!(PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(world, CVScribble.getInstance().getScribbleDrawingAreaRG()), world).contains(player))) {
                return new CommandResponse(gold + player.getName() + red + " is not in the Scribble booth!");
            }
            return stopDrawing(player);
        } else if(baseParameters.get(0).equals("select")) {
            Player player = Bukkit.getPlayer((String) baseParameters.get(1));
            if(player == null || !player.isOnline()) {
                return new CommandResponse(red + "The player \"" + gold + baseParameters.get(0) + red + "\" is offline or non-existent!");
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "menu display scribble player:" + player.getName());
            return new CommandResponse("");
        }
        return new CommandResponse(gold + baseParameters.get(0).toString() + red + " is not a valid parameter!");
    }

    public CommandResponse startDrawing(Player player) {

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, "cvportal trigger " + CVScribble.getInstance().getScribbleDrawingPortalEnter() + " player:" + player.getName() + " force");
        player.sendTitle(purple + "Please wait", purple + "Your Scribble host will provide you a word to draw", 5, 40, 5);
        player.sendMessage("Use the left and right mouse buttons to draw. With your inventory, you can change colours, and the sponge erases the board.");
        Bukkit.dispatchCommand(console, "loadout apply scribble player:" + player.getName());

        return new CommandResponse(gold + player.getName() + purple + " has been sent to the Scribble booth");
    }

    public CommandResponse stopDrawing(Player player) {
        CVScribble.getInstance().setCurrentWord(null);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cvportal trigger " + CVScribble.getInstance().getScribbleDrawingPortalExit());
        return new CommandResponse(gold + player.getName() + purple + " has been removed from the Scribble booth");
    }
}
