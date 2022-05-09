package org.cubeville.cvscribble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvscribble.CVScribble;

import java.util.*;

public class CVScribbleDisplayList extends BaseCommand {

    public CVScribbleDisplayList() {
        super("displaylist");
    }

    @Override
    public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {

        List<String> words = CVScribble.getInstance().getScribbleList();
        Collections.sort(words);

        int totalInvs = 0;
        int i = 0;
        List<Inventory> invs = new ArrayList<>();
        Inventory inv = null;
        for(String word : words) {
            if(i == 44) {
                invs.add(inv);
                i = 0;
                totalInvs++;
            }
            if(i == 0) {
                totalInvs++;
                inv = Bukkit.createInventory(null, 54, "Scribble Words Page " + totalInvs);
            }
            ItemStack paper = new ItemStack(Material.PAPER, 1);
            ItemMeta paperMeta = paper.getItemMeta();
            assert  paperMeta != null;
            paperMeta.setDisplayName(ChatColor.GOLD + word);
            paper.setItemMeta(paperMeta);
            inv.setItem(i, paper);
            i++;
        }




        return new CommandResponse("");
    }

    private ItemStack createPlayerHead(String base64) {
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
        playerHead.setItemMeta(headMeta);
        return playerHead;
    }
}