package org.cubeville.cvscribble;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.commons.utils.PlayerUtils;
import org.cubeville.cvchat.SendLocal;

import java.util.*;

public class CVScribbleListener implements Listener {

    private final UUID worldId;
    private World world;

    private int lastY, lastX;
    private long lastTimestamp;

    private final int regionMinX;
    private final int regionMinY;
    private final int regionMinZ;

    private final int regionMaxX;
    private final int regionMaxY;
    private final int regionMaxZ;

    private final int scribblerMinX;
    private final int scribblerMinY;
    private final int scribblerMinZ;

    private final int scribblerMaxX;
    private final int scribblerMaxY;
    private final int scribblerMaxZ;

    private final Map<Material, Material> itemMap;

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

    public CVScribbleListener() {
        List<World> worlds = Bukkit.getServer().getWorlds();
        world = null;
        for(World world: worlds) {
            try {
                BlockUtils.getWGRegion(world, "scribble_board");
                this.world = world;
                break;
            }
            catch(IllegalArgumentException e) {
                System.out.println("No \"scribble_board\" region found in world: " + world.getName());
            }
        }

        if(world == null) {
            throw new RuntimeException("Region scribble_board not found!");
        }

        {
            org.bukkit.util.Vector min = BlockUtils.getWGRegionMin(world, "scribble_player");
            scribblerMinX = min.getBlockX();
            scribblerMinY = min.getBlockY();
            scribblerMinZ = min.getBlockZ();
        }
        {
            org.bukkit.util.Vector max = BlockUtils.getWGRegionMax(world, "scribble_player");
            scribblerMaxX = max.getBlockX();
            scribblerMaxY = max.getBlockY();
            scribblerMaxZ = max.getBlockZ();
        }

        worldId = world.getUID();

        {
            org.bukkit.util.Vector min = BlockUtils.getWGRegionMin(world, "scribble_board");
            regionMinX = min.getBlockX();
            regionMinY = min.getBlockY();
            regionMinZ = min.getBlockZ();
        }
        {
            Vector max = BlockUtils.getWGRegionMax(world, "scribble_board");
            regionMaxX = max.getBlockX();
            regionMaxY = max.getBlockY();
            regionMaxZ = max.getBlockZ();
        }

        itemMap = new HashMap<>();
        itemMap.put(Material.WHITE_DYE, Material.WHITE_CONCRETE);
        itemMap.put(Material.ORANGE_DYE, Material.ORANGE_CONCRETE);
        itemMap.put(Material.MAGENTA_DYE, Material.MAGENTA_CONCRETE);
        itemMap.put(Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_CONCRETE);
        itemMap.put(Material.YELLOW_DYE, Material.YELLOW_CONCRETE);
        itemMap.put(Material.LIME_DYE, Material.LIME_CONCRETE);
        itemMap.put(Material.PINK_DYE, Material.PINK_CONCRETE);
        itemMap.put(Material.GRAY_DYE, Material.GRAY_CONCRETE);
        itemMap.put(Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_CONCRETE);
        itemMap.put(Material.CYAN_DYE, Material.CYAN_CONCRETE);
        itemMap.put(Material.PURPLE_DYE, Material.PURPLE_CONCRETE);
        itemMap.put(Material.BLUE_DYE, Material.BLUE_CONCRETE);
        itemMap.put(Material.BROWN_DYE, Material.BROWN_CONCRETE);
        itemMap.put(Material.GREEN_DYE, Material.GREEN_CONCRETE);
        itemMap.put(Material.RED_DYE, Material.RED_CONCRETE);
        itemMap.put(Material.BLACK_DYE, Material.BLACK_CONCRETE);
    }

    @EventHandler
    public void onLocalChat(SendLocal event) {
        if(CVScribble.getInstance().getCurrentWord() == null) return;
        if(event.getMessage().equalsIgnoreCase(CVScribble.getInstance().getCurrentWord())) {
            CVScribble.getInstance().setCurrentWord(null);
            String currentWord = event.getMessage();
            String pName = event.getPlayer().getName();
            World pWorld = event.getPlayer().getWorld();
            String scribbleArena = CVScribble.getInstance().getScribbleArenaRG();
            sendTitle(purple + "Congratulations " + gold + pName, purple + "The word/phrase was " + gold + currentWord, pWorld, scribbleArena);
            sendMessage(purple + "The word/phrase was " + gold + currentWord + purple + "! Congratulations " + gold + pName, pWorld, scribbleArena);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cvportal trigger " + CVScribble.getInstance().getScribbleDrawingPortalExit());
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        String r = CVScribble.getInstance().getScribbleDrawingAreaRG();
        World w = event.getPlayer().getWorld();
        try {
            if (PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(w, r), w).contains(event.getPlayer())) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cvportal logintarget " + event.getPlayer().getUniqueId() + " " + CVScribble.getInstance().getScribbleDrawingPortalExit());
            }
        } catch (IllegalArgumentException | NullPointerException ignored) {

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_AIR) return;
        if(!event.getPlayer().getWorld().getUID().equals(worldId)) return;

        Location loc = event.getPlayer().getLocation();
        if(loc.getBlockX() < scribblerMinX || loc.getBlockX() > scribblerMaxX ||
                loc.getBlockY() < scribblerMinY || loc.getBlockY() > scribblerMaxY ||
                loc.getBlockZ() < scribblerMinZ || loc.getBlockZ() > scribblerMaxZ) return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if(item.getType() == Material.WET_SPONGE) {
            clearBoard();
            return;
        }

        if(!itemMap.containsKey(item.getType())) return;
        Material paintmat = itemMap.get(item.getType());

        Block block = event.getPlayer().getTargetBlock(null, 75);
        Location blockLocation = block.getLocation();

        int x = blockLocation.getBlockX();
        int y = blockLocation.getBlockY();
        int z = blockLocation.getBlockZ();

        if(x < regionMinX || y < regionMinY || z < regionMinZ ||
                x > regionMaxX || y > regionMaxY || z > regionMaxZ) return;

        if(event.getAction() == Action.LEFT_CLICK_AIR) {
            for(int yo = -3; yo <= 3; yo++) {
                if(y + yo < regionMinY || y + yo > regionMaxY) continue;
                for(int xo = -3; xo <= 3; xo++) {
                    if(x + xo < regionMinX || x + xo > regionMaxX) continue;
                    int lv = Math.min(xo, yo);
                    int hv = Math.max(xo, yo);
                    if(Math.abs(lv) >= 2 && Math.abs(hv) >= 3) continue;
                    if(Math.abs(lv) >= 3 && Math.abs(hv) >= 2) continue;
                    plot(x + xo, y + yo, paintmat);
                }
            }
            return;
        }

        long ts = System.currentTimeMillis();
        if(ts - lastTimestamp > 700) {
            plot(x, y, paintmat);
        }
        else {
            drawLine(lastX, lastY, x, y, paintmat);
        }

        lastY = y;
        lastX = x;
        lastTimestamp = ts;
    }

    private void plot(int x, int y, Material mat) {
        Location location = new Location(this.world, x, y, regionMinZ);
        Block block = world.getBlockAt(location);
        block.setType(mat);
    }

    private void drawLine(int x1, int y1, int x2, int y2, Material mat) {
        int d = 0;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dx2 = 2 * dx;
        int dy2 = 2 * dy;
        int ix = x1 < x2 ? 1 : -1;
        int iy = y1 < y2 ? 1 : -1;
        int x = x1;
        int y = y1;

        if (dx >= dy) {
            for(int limiter = 0; limiter < 200; limiter++) {
                plot(x, y, mat);
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            for(int limiter = 0; limiter < 200; limiter++) {
                plot(x, y, mat);
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
    }

    public void clearBoard() {
        for(int cx = regionMinX; cx <= regionMaxX; cx++) {
            for(int cy = regionMinY; cy <= regionMaxY; cy++) {
                plot(cx, cy, Material.LIGHT_BLUE_CONCRETE);
            }
        }
    }

    public void sendTitle(String title, String subtitle, World pWorld, String region) {
        for(Player player : PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(pWorld, region), pWorld)) {
            player.sendTitle(title, subtitle, 5, 30, 5);
        }
    }

    public void sendMessage(String message, World pWorld, String region) {
        for(Player player : PlayerUtils.getPlayersInsideRegion(BlockUtils.getWGRegion(pWorld, region), pWorld)) {
            player.sendMessage(message);
        }
    }
}
