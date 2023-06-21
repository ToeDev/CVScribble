package org.cubeville.cvscribble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.commons.utils.PlayerUtils;
import org.cubeville.cvscribble.CVScribble;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CustomSelect extends BaseCommand {

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public CustomSelect() {
        super("custom");
        setPermission("cvscribble.usage");
        addBaseParameter(new CommandParameterString());
        addOptionalBaseParameter(new CommandParameterString());
        addOptionalBaseParameter(new CommandParameterString());
        addParameter("player", true, new CommandParameterString());
        addParameter("hosted", true, new CommandParameterBoolean());
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        if(!(sender instanceof Player)) {
            return new CommandResponse(ChatColor.RED + "You cannot select a word from console!");
        }
        Player player;
        if(parameters.containsKey("player")) {
            if(Bukkit.getPlayer((String) parameters.get("player")) == null) {
                throw new CommandExecutionException(ChatColor.GOLD + (String) parameters.get("player") + ChatColor.RED + " is not online!");
            }
            player = Bukkit.getPlayer((String) parameters.get("player"));
            if(sender instanceof Player && !Objects.equals(player, sender) && !sender.hasPermission("cvscribble.staff")) {
                throw new CommandExecutionException(red + "You do not have permission to do that!");
            }
        } else if(parameters.containsKey("hosted") && (Boolean) parameters.get("hosted")) {
            World world = ((Player) sender).getWorld();
            player = PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(world, CVScribble.getInstance().getScribbleDrawingAreaRG()), world).get(0);
        } else {
            player = (Player) sender;
        }
        assert player != null;
        World world = player.getWorld();
        if(!PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(world, CVScribble.getInstance().getScribbleDrawingAreaRG()), world).contains(player)) {
            return new CommandResponse(red + "Someone must be in the drawing booth to select a word!");
        }
        String newWord = baseParameters.get(0).toString();
        if(baseParameters.size() > 1) newWord = newWord.concat(" " + baseParameters.get(1).toString());
        if(baseParameters.size() > 2) newWord = newWord.concat(" " + baseParameters.get(2).toString());
        CVScribble.getInstance().setCurrentWord(newWord);
        player.sendMessage(purple + "The word/phrase \"" + gold + newWord + purple + "\" was set as the current Scribble word");
        if(CVScribble.getInstance().isHostedMode()) {
            return new CommandResponse(purple + "The word/phrase \"" + gold + newWord + purple + "\" was set as the current Scribble word");
        }
        return new CommandResponse("");
    }
}
