package org.cubeville.cvscribble.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.cubeville.commons.commands.*;
import org.cubeville.cvmenu.CVMenu;
import org.cubeville.cvmenu.menu.MenuContainer;
import org.cubeville.cvmenu.menu.MenuManager;
import org.cubeville.cvscribble.CVScribble;

import java.lang.reflect.Field;
import java.util.*;

public class ListWordGUI extends BaseCommand {


    private final String previousHead;
    private final String nextHead;
    private final String listHead;
    private final String menuHead;
    private final String exitHead;

    public ListWordGUI() {
        super("listgui");
        addParameter("player", true, new CommandParameterString());
        addParameter("page", true, new CommandParameterInteger());

        previousHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVkNzg4MjI1NzYzMTdiMDQ4ZWVhOTIyMjdjZDg1ZjdhZmNjNDQxNDhkY2I4MzI3MzNiYWNjYjhlYjU2ZmExIn19fQ==";
        nextHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE1NDQ1ZGExNmZhYjY3ZmNkODI3ZjcxYmFlOWMxZDJmOTBjNzNlYjJjMWJkMWVmOGQ4Mzk2Y2Q4ZTgifX19";
        listHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkzN2NiMzFjMzdkN2E1ZmNjYzBjNjg1ZTZjOWFhNGU4MTk0M2M4ZDg3YWM0MjFiZmVhZjdjZGNiMDU1YWI0MiJ9fX0=";
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

        List<String> words = CVScribble.getInstance().getScribbleList();
        if(words == null || words.size() <= 0) return new CommandResponse(ChatColor.RED + "There are no words in the Scribble list!");
        Collections.sort(words);

        int totalInvs = 0;
        int t = words.size();
        int i = 0;
        MenuManager menuManager = CVMenu.getCvMenu().getMenuManager();
        int p = 1;
        while(menuManager.getTempMenu("Scribble_" + p, player) != null) {
            assert player != null;
            menuManager.removeMenu(player.getName() + "'s_" + "Scribble_" + p);
            p++;
        }
        for(String word : words) {
            if(i == 45) {
                MenuContainer menu = menuManager.getTempMenu("Scribble_" + totalInvs, player);
                if(menuManager.getTempMenu("Scribble_" + (totalInvs - 1), player) != null) {
                    menu.setItem(45, createPlayerHead(previousHead, ChatColor.LIGHT_PURPLE + "Previous"));
                    menu.addCommand(45, "scribble listgui player:" + player.getName() + " page:" + (totalInvs - 1));
                }
                if(t > 0) {
                    menu.setItem(46, createPlayerHead(nextHead, ChatColor.LIGHT_PURPLE + "Next"));
                    menu.addCommand(46, "scribble listgui player:" + player.getName() + " page:" + (totalInvs + 1));
                }
                menu.setItem(49, createPlayerHead(listHead, ChatColor.LIGHT_PURPLE + "List View"));
                menu.addCommand(49, "scribble list game:true player:" + player.getName());
                menu.setClose(49, true);
                menu.setItem(50, createPlayerHead(menuHead, ChatColor.LIGHT_PURPLE + "Main Menu"));
                menu.addCommand(50, "menu display scribble player:" + player.getName());
                menu.setItem(53, createPlayerHead(exitHead, ChatColor.LIGHT_PURPLE + "Exit"));
                menu.setClose(53, true);
                i = 0;
            }
            if(i == 0) {
                totalInvs++;
                menuManager.createTempMenu("Scribble_" + totalInvs, 54, player);
            }
            ItemStack paper = new ItemStack(Material.PAPER, 1);
            ItemMeta paperMeta = paper.getItemMeta();
            assert  paperMeta != null;
            paperMeta.setDisplayName(ChatColor.GOLD + word);
            paper.setItemMeta(paperMeta);
            menuManager.getTempMenu("Scribble_" + totalInvs, player).setItem(i, paper);
            assert player != null;
            menuManager.getTempMenu("Scribble_" + totalInvs, player).addCommand(i, "cvscribble select player:" + player.getName() + " word:\"" + word + "\"");
            menuManager.getTempMenu("Scribble_" + totalInvs, player).setCmdsRunFromConsole(i, false);
            menuManager.getTempMenu("Scribble_" + totalInvs, player).setClose(i, true);
            i++;
            t--;
            if(t == 0) {
                MenuContainer menu = menuManager.getTempMenu("Scribble_" + totalInvs, player);
                if(menuManager.getTempMenu("Scribble_" + (totalInvs - 1), player) != null) {
                    menu.setItem(45, createPlayerHead(previousHead, ChatColor.LIGHT_PURPLE + "Previous"));
                    menu.addCommand(45, "scribble listgui player:" + player.getName() + " page:" + (totalInvs - 1));
                }
                menu.setItem(49, createPlayerHead(listHead, ChatColor.LIGHT_PURPLE + "List View"));
                menu.addCommand(49, "scribble list game:true player:" + player.getName());
                menu.setClose(49, true);
                menu.setItem(50, createPlayerHead(menuHead, ChatColor.LIGHT_PURPLE + "Main Menu"));
                menu.addCommand(50, "menu display scribble player:" + player.getName());
                menu.setItem(53, createPlayerHead(exitHead, ChatColor.LIGHT_PURPLE + "Exit"));
                menu.setClose(53, true);
            }
        }
        if(parameters.containsKey("page")) {
            if(menuManager.getTempMenu("Scribble_" + parameters.get("page"), player) == null) {
                return new CommandResponse(ChatColor.RED + "This page doesn't exist!");
            }
            assert player != null;
            (player).openInventory(menuManager.getTempMenu("Scribble_" + parameters.get("page"), player).getDisplayInventory(player));
            return new CommandResponse("");
        }
        assert player != null;
        (player).openInventory(menuManager.getTempMenu("Scribble_1", player).getDisplayInventory(player));
        return new CommandResponse("");
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
}