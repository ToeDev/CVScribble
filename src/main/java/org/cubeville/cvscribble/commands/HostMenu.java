package org.cubeville.cvscribble.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.commons.utils.PlayerUtils;
import org.cubeville.cvmenu.CVMenu;
import org.cubeville.cvmenu.menu.MenuContainer;
import org.cubeville.cvmenu.menu.MenuManager;
import org.cubeville.cvscribble.CVScribble;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HostMenu extends BaseCommand {

    private final String previousHead;
    private final String nextHead;
    private final String menuHead;
    private final String exitHead;

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public HostMenu() {
        super("hostmenu");
        setPermission("cvscribble.staff");
        addParameter("pagecontent", true, new CommandParameterString());
        addParameter("page", true, new CommandParameterString());
        addParameter("player", true, new CommandParameterString());

        previousHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVkNzg4MjI1NzYzMTdiMDQ4ZWVhOTIyMjdjZDg1ZjdhZmNjNDQxNDhkY2I4MzI3MzNiYWNjYjhlYjU2ZmExIn19fQ==";
        nextHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE1NDQ1ZGExNmZhYjY3ZmNkODI3ZjcxYmFlOWMxZDJmOTBjNzNlYjJjMWJkMWVmOGQ4Mzk2Y2Q4ZTgifX19";
        menuHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQzMGIwMmIxNTVlNjY5MjczYTc3ODQwYjA3ZjQ1M2U3OWIzOTQ4OTBhNGQ2MGI0MGM3YWMzMGFhNzhmZTkzNiJ9fX0";
        exitHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyNTRmYzA0NGVmYjg0Y2Q1NzZhNmM4ZjExNDRmODNhY2RiMTQ5OTEyMzIwNjBhYjQ4NjY5MWEwOWIifX19";
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
                return new CommandResponse(ChatColor.RED + "You cannot open a menu from console!");
            }
            player = (Player) sender;
        }
        assert player != null;
        try {
            BlockUtils.getWGRegion(player.getWorld(), CVScribble.getInstance().getScribbleArenaRG());
        } catch(IllegalArgumentException ignored) {
            return new CommandResponse(gold + player.getName() + red + " is not in the Scribble arena!");
        }
        if(!PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(player.getWorld(), CVScribble.getInstance().getScribbleArenaRG()), player.getWorld()).contains(player)) {
            return new CommandResponse(gold + player.getName() + red + " is not in the Scribble arena!");
        }
        if(!parameters.containsKey("pagecontent")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "menu display scribble_hosting player:" + player.getName());
            return new CommandResponse("");
        }
        if(parameters.get("pagecontent").equals("playerstart")) {
            List<Player> sPlayers = PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(player.getWorld(), CVScribble.getInstance().getScribbleArenaRG()), player.getWorld());
            int totalInvs = 0;
            int t = sPlayers.size();
            int i = 0;
            MenuManager menuManager = CVMenu.getCvMenu().getMenuManager();
            int p = 1;
            while(menuManager.getTempMenu("Scribble_Start_" + p, player) != null) {
                menuManager.removeMenu(player.getName() + "'s_" + "Scribble_Start_" + p);
                p++;
            }
            for(Player bPlayer : sPlayers) {
                if(i == 45) {
                    MenuContainer menu = menuManager.getTempMenu("Scribble_Start_" + totalInvs, player);
                    if(menuManager.getTempMenu("Scribble_Start_" + (totalInvs - 1), player) != null) {
                        menu.setItem(45, createPlayerHead(previousHead, ChatColor.LIGHT_PURPLE + "Previous"));
                        menu.addCommand(45, "scribble hostmenu pagecontent:playerstart page:" + (totalInvs - 1));
                    }
                    if(t > 0) {
                        menu.setItem(46, createPlayerHead(nextHead, ChatColor.LIGHT_PURPLE + "Next"));
                        menu.addCommand(46, "scribble hostmenu pagecontent:playerstart page:" + (totalInvs + 1));
                    }
                    menu.setItem(50, createPlayerHead(menuHead, ChatColor.LIGHT_PURPLE + "Main Hosting Menu"));
                    menu.addCommand(50, "scribble hostmenu");
                    menu.setItem(53, createPlayerHead(exitHead, ChatColor.LIGHT_PURPLE + "Exit"));
                    menu.setClose(53, true);
                    i = 0;
                }
                if(i == 0) {
                    totalInvs++;
                    menuManager.createTempMenu("Scribble_Start_" + totalInvs, 54, player);
                }
                menuManager.getTempMenu("Scribble_Start_" + totalInvs, player).setItem(i, createStartPlayerHead(bPlayer));
                menuManager.getTempMenu("Scribble_Start_" + totalInvs, player).addCommand(i, "cvscribble host start " + bPlayer.getName());
                menuManager.getTempMenu("Scribble_Start_" + totalInvs, player).setCmdsRunFromConsole(i, false);
                menuManager.getTempMenu("Scribble_Start_" + totalInvs, player).setClose(i, false);
                i++;
                t--;
                if(t == 0) {
                    MenuContainer menu = menuManager.getTempMenu("Scribble_Start_" + totalInvs, player);
                    if(menuManager.getTempMenu("Scribble_Start_" + (totalInvs - 1), player) != null) {
                        menu.setItem(45, createPlayerHead(previousHead, ChatColor.LIGHT_PURPLE + "Previous"));
                        menu.addCommand(45, "scribble hostmenu pagecontent:playerstart page:" + (totalInvs - 1));
                    }
                    menu.setItem(50, createPlayerHead(menuHead, ChatColor.LIGHT_PURPLE + "Main Hosting Menu"));
                    menu.addCommand(50, "scribble hostmenu");
                    menu.setCmdsRunFromConsole(50, false);
                    menu.setItem(53, createPlayerHead(exitHead, ChatColor.LIGHT_PURPLE + "Exit"));
                    menu.setClose(53, true);
                }
            }
            if(parameters.containsKey("page")) {
                if(menuManager.getTempMenu("Scribble_Start_" + parameters.get("page"), player) == null) {
                    return new CommandResponse(ChatColor.RED + "This page doesn't exist!");
                }
                (player).openInventory(menuManager.getTempMenu("Scribble_Start_" + parameters.get("page"), player).getDisplayInventory(player));
                return new CommandResponse("");
            }
            (player).openInventory(menuManager.getTempMenu("Scribble_Start_1", player).getDisplayInventory(player));
            return new CommandResponse("");
        } else if(parameters.get("pagecontent").equals("playerremove")) {
            List<Player> sPlayers = PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(player.getWorld(), CVScribble.getInstance().getScribbleArenaRG()), player.getWorld());
            int totalInvs = 0;
            int t = sPlayers.size();
            int i = 0;
            MenuManager menuManager = CVMenu.getCvMenu().getMenuManager();
            int p = 1;
            while(menuManager.getTempMenu("Scribble_Remove_" + p, player) != null) {
                menuManager.removeMenu(player.getName() + "'s_" + "Scribble_Remove_" + p);
                p++;
            }
            for(Player bPlayer : sPlayers) {
                if(i == 45) {
                    MenuContainer menu = menuManager.getTempMenu("Scribble_Remove_" + totalInvs, player);
                    if(menuManager.getTempMenu("Scribble_Remove_" + (totalInvs - 1), player) != null) {
                        menu.setItem(45, createPlayerHead(previousHead, ChatColor.LIGHT_PURPLE + "Previous"));
                        menu.addCommand(45, "scribble hostmenu pagecontent:playerremove page:" + (totalInvs - 1));
                    }
                    if(t > 0) {
                        menu.setItem(46, createPlayerHead(nextHead, ChatColor.LIGHT_PURPLE + "Next"));
                        menu.addCommand(46, "scribble hostmenu pagecontent:playerremove page:" + (totalInvs + 1));
                    }
                    menu.setItem(50, createPlayerHead(menuHead, ChatColor.LIGHT_PURPLE + "Main Hosting Menu"));
                    menu.addCommand(50, "scribble hostmenu");
                    menu.setItem(53, createPlayerHead(exitHead, ChatColor.LIGHT_PURPLE + "Exit"));
                    menu.setClose(53, true);
                    i = 0;
                }
                if(i == 0) {
                    totalInvs++;
                    menuManager.createTempMenu("Scribble_Remove_" + totalInvs, 54, player);
                }
                menuManager.getTempMenu("Scribble_Remove_" + totalInvs, player).setItem(i, createRemovePlayerHead(bPlayer));
                menuManager.getTempMenu("Scribble_Remove_" + totalInvs, player).addCommand(i, "cvscribble host remove " + bPlayer.getName());
                menuManager.getTempMenu("Scribble_Remove_" + totalInvs, player).setCmdsRunFromConsole(i, false);
                menuManager.getTempMenu("Scribble_Remove_" + totalInvs, player).setClose(i, false);
                i++;
                t--;
                if(t == 0) {
                    MenuContainer menu = menuManager.getTempMenu("Scribble_Remove_" + totalInvs, player);
                    if(menuManager.getTempMenu("Scribble_Remove_" + (totalInvs - 1), player) != null) {
                        menu.setItem(45, createPlayerHead(previousHead, ChatColor.LIGHT_PURPLE + "Previous"));
                        menu.addCommand(45, "scribble hostmenu pagecontent:playerremove page:" + (totalInvs - 1));
                    }
                    menu.setItem(50, createPlayerHead(menuHead, ChatColor.LIGHT_PURPLE + "Main Hosting Menu"));
                    menu.addCommand(50, "scribble hostmenu");
                    menu.setCmdsRunFromConsole(50, false);
                    menu.setItem(53, createPlayerHead(exitHead, ChatColor.LIGHT_PURPLE + "Exit"));
                    menu.setClose(53, true);
                }
            }
            if(parameters.containsKey("page")) {
                if(menuManager.getTempMenu("Scribble_Remove_" + parameters.get("page"), player) == null) {
                    return new CommandResponse(ChatColor.RED + "This page doesn't exist!");
                }
                (player).openInventory(menuManager.getTempMenu("Scribble_Remove_" + parameters.get("page"), player).getDisplayInventory(player));
                return new CommandResponse("");
            }
            (player).openInventory(menuManager.getTempMenu("Scribble_Remove_1", player).getDisplayInventory(player));
            return new CommandResponse("");
        }
        return new CommandResponse(gold + (String) parameters.get("page") + red + " is not a valid parameter!");
    }

    private ItemStack createPlayerHead(String base64, String name) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        assert headMeta != null;
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        headMeta.setDisplayName(name);
        playerHead.setItemMeta(headMeta);
        return playerHead;
    }

    private ItemStack createStartPlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        assert headMeta != null;
        headMeta.setOwningPlayer(player);
        headMeta.setDisplayName(gold + "Send " + purple + player.getName() + gold + " to the Drawing Booth");
        playerHead.setItemMeta(headMeta);
        return playerHead;
    }

    private ItemStack createRemovePlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        assert headMeta != null;
        headMeta.setOwningPlayer(player);
        headMeta.setDisplayName(gold + "Remove " + purple + player.getName() + gold + " from the Drawing Booth");
        playerHead.setItemMeta(headMeta);
        return playerHead;
    }
}
