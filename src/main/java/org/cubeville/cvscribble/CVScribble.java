package org.cubeville.cvscribble;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.cvscribble.commands.*;

public class CVScribble extends JavaPlugin implements Listener {

    private Logger logger;
    private static CVScribble instance;
    private CVScribbleListener cvScribbleListener;

    private String scribbleBoardRG;
    private String scribbleArenaRG;
    private String scribbleDrawingAreaRG;
    private String scribbleDrawingTeleportPortal;
    private List<String> scribbleList;

    private CommandParser commandParser;

    public static CVScribble getInstance() {
        return instance;
    }
	
    @Override
    public void onEnable() {
        logger = getLogger();
        instance = this;

        final File dataDir = getDataFolder();
        if(!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File configFile = new File(dataDir, "config.yml");
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
                final InputStream inputStream = this.getResource(configFile.getName());
                final FileOutputStream fileOutputStream = new FileOutputStream(configFile);
                final byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = Objects.requireNonNull(inputStream).read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch(IOException e) {
                logger.log(Level.WARNING, ChatColor.RED + "Unable to generate config file", e);
                throw new RuntimeException(ChatColor.RED + "Unable to generate config file", e);
            }
        }

        this.scribbleList = new ArrayList<>();
        YamlConfiguration mainConfig = new YamlConfiguration();
        try {
            mainConfig.load(configFile);

            scribbleBoardRG = mainConfig.getString("Scribble-Board-Region");
            logger.log(Level.INFO, "Scribble Board Region set to \"" + scribbleBoardRG + "\"");
            scribbleArenaRG = mainConfig.getString("Scribble-Arena-Region");
            logger.log(Level.INFO, "Scribble Arena Region set to \"" + scribbleArenaRG + "\"");
            scribbleDrawingAreaRG = mainConfig.getString("Scribble-Drawing-Region");
            logger.log(Level.INFO, "Scribble Arena Region set to \"" + scribbleDrawingAreaRG + "\"");
            scribbleDrawingTeleportPortal = mainConfig.getString("Scribble-Drawing-Teleport-Portal");
            logger.log(Level.INFO, "Scribble Drawing Teleport Portal set to \"" + scribbleDrawingTeleportPortal + "\"");

            for(String s : mainConfig.getStringList("Scribble-List")) {
                scribbleList.add(s);
                logger.log(Level.INFO, "Word/Phrase \"" + s + "\" added from Config file.");
            }
        } catch(IOException | InvalidConfigurationException e) {
            logger.log(Level.WARNING, ChatColor.RED + "Unable to load config file", e);
        }

        this.commandParser = new CommandParser();
        this.commandParser.addCommand(new CVScribbleList());
        this.commandParser.addCommand(new CVScribbleEdit());
        this.commandParser.addCommand(new CVScribbleAdd());
        this.commandParser.addCommand(new CVScribbleRemove());

        this.commandParser.addCommand(new CVScribbleStart());

        cvScribbleListener = new CVScribbleListener();
        Bukkit.getPluginManager().registerEvents(cvScribbleListener, this);

        logger.log(Level.INFO, "CVScribble is now enabled");
    }

    public String getScribbleBoardRG() {
        return this.scribbleBoardRG;
    }

    public String getScribbleArenaRG() {
        return this.scribbleArenaRG;
    }

    public String getScribbleDrawingAreaRG() {
        return this.scribbleDrawingAreaRG;
    }

    public String getScribbleDrawingTeleportPortal() {
        return this.scribbleDrawingTeleportPortal;
    }

    public List<String> getScribbleList() {
        return this.scribbleList;
    }

    public void saveScribbleList() {
        final File dataDir = getDataFolder();
        File configFile = new File(dataDir, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("Scribble-List", scribbleList);
        try {
            config.save(configFile);
        }
        catch (IOException e) {
            logger.log(Level.WARNING, "Unable to save config file!", e);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("scribbleclear")) {
            cvScribbleListener.clearBoard();
            return true;
        } else if(command.getName().equalsIgnoreCase("cvscribble")) {
            return this.commandParser.execute(sender, args);
        }
        return false;
    }

    @Override
    public void onDisable() {
        logger.log(Level.INFO, "CVScribble is now enabled");
    }
}
