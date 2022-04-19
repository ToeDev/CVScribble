package org.cubeville.cvscribble;

import java.util.*;

import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import org.cubeville.commons.utils.BlockUtils;

public class CVScribble extends JavaPlugin implements Listener
{
    private UUID worldId;
    private World world;

    private int lastY, lastX;
    private long lastTimestamp;
    
    private int regionMinX, regionMinY, regionMinZ;
    private int regionMaxX, regionMaxY, regionMaxZ;
    
    private int scribblerMinX, scribblerMinY, scribblerMinZ;
    private int scribblerMaxX, scribblerMaxY, scribblerMaxZ;

    Map<Material, Material> itemmap;
	
    @Override
    public void onEnable() {
        List<World> worlds = Bukkit.getServer().getWorlds();
	    world = null;
        for(World world: worlds) {
	        try {
		        BlockUtils.getWGRegion(world, "scribble_board");
		        this.world = world;
		        break;
            }
            catch(IllegalArgumentException e) {
                e.printStackTrace();
                System.out.println("No \"scribble_board\" region found");
	        }
        }

        if(world == null) {
            throw new RuntimeException("Region scribble_board not found!");
        }

        {
            Vector min = BlockUtils.getWGRegionMin(world, "scribble_player");
            scribblerMinX = min.getBlockX();
            scribblerMinY = min.getBlockY();
            scribblerMinZ = min.getBlockZ();
        }
        {
            Vector max = BlockUtils.getWGRegionMax(world, "scribble_player");
            scribblerMaxX = max.getBlockX();
            scribblerMaxY = max.getBlockY();
            scribblerMaxZ = max.getBlockZ();
        }

            worldId = world.getUID();

        {
            Vector min = BlockUtils.getWGRegionMin(world, "scribble_board");
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

        itemmap = new HashMap<>();
        itemmap.put(Material.WHITE_DYE, Material.WHITE_CONCRETE);
        itemmap.put(Material.ORANGE_DYE, Material.ORANGE_CONCRETE);
        itemmap.put(Material.MAGENTA_DYE, Material.MAGENTA_CONCRETE);
        itemmap.put(Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_CONCRETE);
        itemmap.put(Material.YELLOW_DYE, Material.YELLOW_CONCRETE);
        itemmap.put(Material.LIME_DYE, Material.LIME_CONCRETE);
        itemmap.put(Material.PINK_DYE, Material.PINK_CONCRETE);
        itemmap.put(Material.GRAY_DYE, Material.GRAY_CONCRETE);
        itemmap.put(Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_CONCRETE);
        itemmap.put(Material.CYAN_DYE, Material.CYAN_CONCRETE);
        itemmap.put(Material.PURPLE_DYE, Material.PURPLE_CONCRETE);
        itemmap.put(Material.BLUE_DYE, Material.BLUE_CONCRETE);
        itemmap.put(Material.BROWN_DYE, Material.BROWN_CONCRETE);
        itemmap.put(Material.GREEN_DYE, Material.GREEN_CONCRETE);
        itemmap.put(Material.RED_DYE, Material.RED_CONCRETE);
        itemmap.put(Material.BLACK_DYE, Material.BLACK_CONCRETE);
	
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("scribbleclear")) {
            clearBoard();
            return true;
        }
        return false;
    }

    private void plot(int x, int y, Material mat) {
        Location location = new Location(world, x, y, regionMinZ);
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

    private void clearBoard() {
        for(int cx = regionMinX; cx <= regionMaxX; cx++) {
            for(int cy = regionMinY; cy <= regionMaxY; cy++) {
                plot(cx, cy, Material.LIGHT_BLUE_CONCRETE);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_AIR) return;
        if(!Objects.requireNonNull(event.getPlayer().getLocation().getWorld()).getUID().equals(worldId)) return;

        Location loc = event.getPlayer().getLocation();
        if(loc.getBlockX() < scribblerMinX || loc.getBlockX() > scribblerMaxX ||
           loc.getBlockY() < scribblerMinY || loc.getBlockY() > scribblerMaxY ||
           loc.getBlockZ() < scribblerMinZ || loc.getBlockZ() > scribblerMaxZ) return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if(item.getType() == Material.WET_SPONGE) {
            clearBoard();
	    return;
        }

        if(!itemmap.containsKey(item.getType())) return;
	    Material paintmat = itemmap.get(item.getType());

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

}
