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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvmenu.CVMenu;
import org.cubeville.cvmenu.menu.MenuContainer;
import org.cubeville.cvmenu.menu.MenuManager;
import org.cubeville.cvscribble.commands.*;

public class CVScribble extends JavaPlugin {

    private Logger logger;
    private static CVScribble instance;
    private CVScribbleListener cvScribbleListener;

    private String scribbleBoardRG;
    private String scribbleArenaRG;
    private String scribbleDrawingAreaRG;
    private String scribbleDrawingPortalEnter;
    private String scribbleDrawingPortalExit;
    private List<String> scribbleList;

    private CommandParser commandParser;

    private String currentWord = null;
    private boolean hostedMode = false;

    private final ChatColor gold = ChatColor.GOLD;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor purple = ChatColor.LIGHT_PURPLE;

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
                logger.log(Level.WARNING, red + "Unable to generate config file", e);
                throw new RuntimeException(red + "Unable to generate config file", e);
            }
        }

        this.scribbleList = new ArrayList<>();
        YamlConfiguration mainConfig = new YamlConfiguration();
        try {
            mainConfig.load(configFile);

            scribbleBoardRG = mainConfig.getString("Scribble-Board-Region");
            logger.log(Level.INFO, purple + "Scribble Board Region set to \"" + gold + scribbleBoardRG + purple + "\"");
            scribbleArenaRG = mainConfig.getString("Scribble-Arena-Region");
            logger.log(Level.INFO, purple + "Scribble Arena Region set to \"" + gold + scribbleArenaRG + purple + "\"");
            scribbleDrawingAreaRG = mainConfig.getString("Scribble-Drawing-Region");
            logger.log(Level.INFO, purple + "Scribble Arena Region set to \"" + gold + scribbleDrawingAreaRG + purple + "\"");
            scribbleDrawingPortalEnter = mainConfig.getString("Scribble-Drawing-Portal-Enter");
            logger.log(Level.INFO, purple + "Scribble Drawing Start Portal set to \"" + gold + scribbleDrawingPortalEnter + purple + "\"");
            scribbleDrawingPortalExit = mainConfig.getString("Scribble-Drawing-Portal-Exit");
            logger.log(Level.INFO, purple + "Scribble Drawing Exit Portal set to \"" + gold + scribbleDrawingPortalExit + purple + "\"");

            scribbleList.addAll(mainConfig.getStringList("Scribble-List"));
        } catch(IOException | InvalidConfigurationException e) {
            logger.log(Level.WARNING, red + "Unable to load config file", e);
        }

        if(!CVMenu.getCvMenu().getMenuManager().menuExists("Scribble")) {
            MenuManager menuManager = CVMenu.getCvMenu().getMenuManager();
            logger.log(Level.WARNING, purple + "Scribble menu not found! Attempting to create now");

            menuManager.createMenu("Scribble", 9);
            MenuContainer menu = menuManager.getMenu("Scribble");

            ItemStack book = new ItemStack(Material.BOOK, 1);
            ItemMeta bookMeta = book.getItemMeta();
            assert bookMeta != null;
            bookMeta.setDisplayName(gold + "List of Words");
            book.setItemMeta(bookMeta);
            menu.getInventory().setItem(2, book);

            ItemStack feather = new ItemStack(Material.FEATHER, 1);
            ItemMeta featherMeta = feather.getItemMeta();
            assert featherMeta != null;
            featherMeta.setDisplayName(gold + "Enter Custom Word/Phrase");
            feather.setItemMeta(featherMeta);
            menu.getInventory().setItem(4, feather);

            ItemStack string = new ItemStack(Material.STRING, 1);
            ItemMeta stringMeta = string.getItemMeta();
            assert  stringMeta != null;
            stringMeta.setDisplayName(gold + "Freestyle");
            string.setItemMeta(stringMeta);
            menu.getInventory().setItem(6, string);

            menu.addCommand(2, "cvscribble listgui player:%player%");
            menu.addCommand(4, "cvscribble sendsuggestion player:%player% suggestion:\"/scribble custom \"");
            menu.setClose(4, true);
            menu.setClose(6, true);
            CVMenu.getCvMenu().saveMenuManager();
            logger.log(Level.INFO, purple + "Scribble menu created!");
        }

        if(!CVMenu.getCvMenu().getMenuManager().menuExists("Scribble_Hosting")) {
            MenuManager menuManager = CVMenu.getCvMenu().getMenuManager();
            logger.log(Level.WARNING, purple + "Scribble Hosting menu not found! Attempting to create now");

            menuManager.createMenu("Scribble_Hosting", 9);
            MenuContainer menu = menuManager.getMenu("Scribble_Hosting");

            ItemStack limeWool = new ItemStack(Material.LIME_WOOL, 1);
            ItemMeta limeWoolMeta = limeWool.getItemMeta();
            assert limeWoolMeta != null;
            limeWoolMeta.setDisplayName(gold + "Enable Hosted Mode (closes portal)");
            limeWool.setItemMeta(limeWoolMeta);
            menu.getInventory().setItem(0, limeWool);

            ItemStack redWool = new ItemStack(Material.RED_WOOL, 1);
            ItemMeta redWoolMeta = redWool.getItemMeta();
            assert redWoolMeta != null;
            redWoolMeta.setDisplayName(gold + "Disable Hosted Mode (opens portal)");
            redWool.setItemMeta(redWoolMeta);
            menu.getInventory().setItem(1, redWool);

            ItemStack pHead = new ItemStack(Material.PLAYER_HEAD, 1);
            ItemMeta pHeadMeta = pHead.getItemMeta();
            assert  pHeadMeta != null;
            pHeadMeta.setDisplayName(gold + "Send a Player to the Drawing Booth");
            pHead.setItemMeta(pHeadMeta);
            menu.getInventory().setItem(4, pHead);

            ItemStack sHead = new ItemStack(Material.SKELETON_SKULL, 1);
            ItemMeta sHeadMeta = sHead.getItemMeta();
            assert sHeadMeta != null;
            sHeadMeta.setDisplayName(gold + "Remove a Player from the Drawing Booth");
            sHead.setItemMeta(sHeadMeta);
            menu.getInventory().setItem(5, sHead);

            ItemStack arrow = new ItemStack(Material.ARROW, 1);
            ItemMeta arrowMeta = arrow.getItemMeta();
            assert arrowMeta != null;
            arrowMeta.setDisplayName(gold + "Select a word/phrase");
            arrow.setItemMeta(arrowMeta);
            menu.getInventory().setItem(8, arrow);

            menu.addCommand(0, "cvscribble host status true");
            menu.setCmdsRunFromConsole(0, false);
            menu.addCommand(1, "cvscribble host status false");
            menu.setCmdsRunFromConsole(1, false);
            menu.addCommand(4, "cvscribble hostmenu pagecontent:playerstart");
            menu.setCmdsRunFromConsole(4, false);
            menu.addCommand(5, "cvscribble hostmenu pagecontent:playerremove");
            menu.setCmdsRunFromConsole(5, false);
            menu.addCommand(8, "cvscribble host select %player%");
            menu.setCmdsRunFromConsole(8, false);
            CVMenu.getCvMenu().saveMenuManager();
            logger.log(Level.INFO, purple + "Scribble Hosting menu created!");
        }

        this.commandParser = new CommandParser();
        this.commandParser.addCommand(new ListWord());
        this.commandParser.addCommand(new EditWord());
        this.commandParser.addCommand(new AddWord());
        this.commandParser.addCommand(new RemoveWord());

        this.commandParser.addCommand(new StartGame());
        this.commandParser.addCommand(new ListWordGUI());
        this.commandParser.addCommand(new SelectWord());
        this.commandParser.addCommand(new CustomSelect());
        this.commandParser.addCommand(new SendSuggestion());
        this.commandParser.addCommand(new HostCommands());
        this.commandParser.addCommand(new HostMenu());

        this.commandParser.addCommand(new Help());

        cvScribbleListener = new CVScribbleListener();
        Bukkit.getPluginManager().registerEvents(cvScribbleListener, this);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "menu removecommand scribble slot:4");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "menu addcommand scribble slot:4 command:\"cvscribble sendsuggestion player:%player% suggestion:\"/scribble custom \"\"");

        logger.log(Level.INFO, purple + "CVScribble is now enabled");
    }

    public String getCurrentWord() {
        return this.currentWord;
    }

    public void setCurrentWord(String newWord) {
        this.currentWord = newWord;
    }

    public boolean isHostedMode() {
        return this.hostedMode;
    }

    public void setHostedMode(boolean status) {
        if(status) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "menu removecommand scribble slot:4");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "menu addcommand scribble slot:4 command:\"cvscribble sendsuggestion player:%player% suggestion:\"/scribble custom hosted:true \"\"");
        } else {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "menu removecommand scribble slot:4");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "menu addcommand scribble slot:4 command:\"cvscribble sendsuggestion player:%player% suggestion:\"/scribble custom \"\"");
        }
        this.hostedMode = status;
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

    public String getScribbleDrawingPortalEnter() {
        return this.scribbleDrawingPortalEnter;
    }

    public String getScribbleDrawingPortalExit() {
        return this.scribbleDrawingPortalExit;
    }

    public List<String> getScribbleList() {
        return this.scribbleList;
    }

    public void saveScribbleList() {
        final File dataDir = getDataFolder();
        File configFile = new File(dataDir, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
            config.set("Scribble-List", scribbleList);
            config.save(configFile);
        }
        catch (IOException | InvalidConfigurationException e) {
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
