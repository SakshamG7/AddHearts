package com.gh05typlayz.AddHearts;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Runtime.Version;

public class Main extends JavaPlugin implements Listener {
    public FileConfiguration config = this.getCustomConfig();
    private File customConfigFile;
    String help = "/addhearts help - This is the Help Command. (Shows all Commands)\n" +
            "/addhearts heal - Heals Yourself.\n" +
            "/addhearts heal <Player> - Heals Player.\n" +
            "/addhearts add <Number Of Hearts> <Player> - Adds Hearts To Given Player.\n" +
            "/addhearts subtract <Number Of Hearts> <Player> - Removes Hearts From Given Player.\n" +
            "/addhearts set <Number Of Hearts> <Player> - Sets Number Of Hearts to The Given Player.\n" +
            "/addhearts default <Number Of Hearts> - This Changes the default amount of Hearts For New Player Join With.\n" +
            "/addhearts setall <Number Of Hearts> - Sets All Online And Offline Player's Hearts to This Value.\n" +
            "/addhearts reload - Reloads The Config File And All The Players Hearts.";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        createCustomConfig();
        try {
            config.load("plugins/AddHearts/config.yml");
        } catch (Exception error) {
            getLogger().warning("Cannot Load Config File.\n" +
                    "Creating Basic Config File...");
            createCustomConfig();
            getLogger().info("Created Basic Config File.");
        }

        int pluginId = 14710;
        Metrics metrics = new Metrics(this, pluginId);
        getLogger().info("Has Started.");
        new UpdateChecker(this, 100878).getLatestVerision(version -> {
            Version latest = Version.parse(version);
            Version current = Version.parse(this.getDescription().getVersion());
            if (current.compareTo(latest) >= 0) {
                getLogger().info("Plugin is up to date.");
            } else {
                getLogger().warning("Plugin is not up to date.\nLatest Version (" + latest + ")");
            }
        });
    }


    @Override
    public void onDisable() {
        getLogger().info("Has Stopped.");
    }

    public void reloadCustomConfig() {
        try {
            config.load("plugins/AddHearts/config.yml");
        } catch (Exception error) {
            getLogger().warning("Cannot Load Config File.\n" +
                    "Creating Basic Config File...");
            createCustomConfig();
            getLogger().info("Created Basic Config File.");
        }
    }

    public void saveCustomConfig() {
        try {
            this.config.save("plugins/AddHearts/config.yml");
            this.reloadCustomConfig();
        } catch (IOException error) {
            createCustomConfig();
            getLogger().warning("Error: Cannot Edit The Config File.");
            saveCustomConfig();
        }
    }

    public void heartReload(CommandSender sender) {
        if (getServer().getOnlinePlayers().size() > 0) {
            Object[] players = getServer().getOnlinePlayers().toArray();
            for (Object player : players) {
                Player p = (Player) player;
                if (config.contains("players." + p.getUniqueId() + ".hearts")) {
                    double newHealth = config.getDouble("players." + p.getUniqueId() + ".hearts");
                    p.setMaxHealth(newHealth);
                    p.sendMessage("[AddHearts] You Now Have " + newHealth + " hearts.");
                } else {
                    config.set("players." + p.getUniqueId() + ".hearts", p.getMaxHealth());
                    saveCustomConfig();
                }
            }
        } else {
            if (sender instanceof Player) {
                sender.sendMessage("[AddHearts] No Players Online, Only Changing Offline Player's Hearts.");
            } else {
                getLogger().warning("No Players Online, Only Changing Offline Player's Hearts.");
            }
        }
    }

    public FileConfiguration getCustomConfig() {
        return this.config;
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "config.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void logMessage(CommandSender sender, String s) {
        if (sender instanceof Player) {
            sender.sendMessage("[AddHearts] " + s);
        } else {
            getLogger().info(s);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.isOp()) {
            new UpdateChecker(this, 100878).getLatestVerision(version -> {
                Version latest = Version.parse(version);
                Version current = Version.parse(this.getDescription().getVersion());
                if (current.compareTo(latest) >= 0) {
                    player.sendMessage("[AddHearts] Plugin is up to date.");
                } else {
                    player.sendMessage("[AddHearts] Plugin is not up to date.\nLatest Version (" + latest + ")");
                }
            });
        }

        if (player.hasPlayedBefore()) {
            if (config.contains("players." + player.getUniqueId())) {
                double maxHealth = player.getMaxHealth();
                config.set("players." + player.getUniqueId() + ".hearts", maxHealth);
                config.set("players." + player.getUniqueId() + ".displayName", player.getDisplayName());
                saveCustomConfig();
            } else {
                player.setMaxHealth(config.getDouble("players." + player.getUniqueId() + ".hearts"));
            }
        } else {
            config.set("players." + player.getUniqueId() + ".hearts", config.getDouble("defaultHealth"));
            config.set("players." + player.getUniqueId() + ".displayName", player.getDisplayName());
            saveCustomConfig();
            player.setMaxHealth(config.getDouble("defaultHealth"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.performCommand("addhearts help");
            } else {
                getServer().dispatchCommand(sender, "addhearts help");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("addhearts") && args[0].equalsIgnoreCase("reload")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("addhearts.reload")) {
                        sender.sendMessage("[AddHearts] Reloading Config.");
                        reloadCustomConfig();
                        heartReload(sender);
                        sender.sendMessage("[AddHearts] Success!.");
                    } else {
                        sender.sendMessage("[AddHearts] You can't do that.");
                    }
                } else {
                    getLogger().info("Reloading Config.");
                    reloadCustomConfig();
                    heartReload(sender);
                    getLogger().info("Success!.");
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.performCommand("addhearts help");
                } else {
                    getServer().dispatchCommand(sender, "addhearts help");
                }
            }
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("addhearts")) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(help);
                } else if (args[0].equalsIgnoreCase("add") && player.hasPermission("addhearts.add")) {
                    if (args.length == 2) {
                        double oldHealth = player.getMaxHealth();
                        double addedHealth = Double.parseDouble(args[1]);
                        double newHealth = Double.sum(oldHealth, addedHealth);
                        config.set("players." + player.getUniqueId() + ".hearts", newHealth);
                        saveCustomConfig();
                        player.setMaxHealth(newHealth);
                        player.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                    } else if (args.length == 3) {
                        Player p = Bukkit.getPlayer(args[2]);
                        if (p != null) {
                            double oldHealth = p.getMaxHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, addedHealth);
                            config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                            saveCustomConfig();
                            p.setMaxHealth(newHealth);
                            p.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                            player.sendMessage("[AddHearts] " + args[2] + " now has " + newHealth + " hearts!");
                        } else {
                            player.sendMessage("[AddHearts] " + args[2] + " is Offline.");
                        }
                    } else {
                        player.performCommand("addhearts help");
                    }
                } else if (args[0].equalsIgnoreCase("subtract") && player.hasPermission("addhearts.subtract")) {
                    if (args.length == 2) {
                        double oldHealth = player.getHealth();
                        double addedHealth = Double.parseDouble(args[1]);
                        double newHealth = Double.sum(oldHealth, -addedHealth);
                        if (newHealth <= 0) {
                            player.sendMessage("[AddHearts] Error: The Player Cannot Have Negative Hearts.");
                        } else {
                            config.set("players." + player.getUniqueId() + ".hearts", newHealth);
                            saveCustomConfig();
                            player.setMaxHealth(newHealth);
                            player.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                        }
                    } else if (args.length == 3) {
                        Player p = Bukkit.getPlayer(args[2]);
                        if (p != null) {
                            double oldHealth = p.getMaxHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, -addedHealth);
                            if (newHealth <= 0) {
                                player.sendMessage("[AddHearts] Error: The Player Cannot Have Negative Hearts.");
                            } else {
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                saveCustomConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                                player.sendMessage("[AddHearts] " + args[2] + " now has " + newHealth + " hearts!");
                            }
                        } else {
                            player.sendMessage("[AddHearts] " + args[2] + " is Offline.");
                        }
                    } else {
                        player.performCommand("addhearts help");
                    }
                } else if (args[0].equalsIgnoreCase("set") && player.hasPermission("addhearts.set")) {
                    if (args.length == 2) {
                        double newHealth = Double.parseDouble(args[1]);
                        config.set("players." + player.getUniqueId() + ".hearts", newHealth);
                        saveCustomConfig();
                        player.setMaxHealth(newHealth);
                        player.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                    } else if (args.length == 3) {
                        double newHealth = Double.parseDouble(args[1]);
                        Player p = Bukkit.getPlayer(args[2]);
                        if (p != null) {
                            config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                            saveCustomConfig();
                            p.setMaxHealth(newHealth);
                            p.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                            player.sendMessage("[AddHearts] " + args[2] + " now has " + newHealth + " hearts!");
                        } else {
                            player.sendMessage("[AddHearts] " + args[2] + " is Offline.");
                        }
                    } else {
                        player.performCommand("addhearts help");
                    }
                } else if (args[0].equalsIgnoreCase("heal") && player.hasPermission("addhearts.heal")) {
                    if (args.length == 1) {
                        player.setHealth(player.getMaxHealth());
                        player.setSaturation(20);
                        player.setFoodLevel(20);
                        player.setFireTicks(0);
                        player.sendMessage("[AddHearts] You have been healed!");
                    } else if (args.length == 2) {
                        Player p = Bukkit.getPlayer(args[1]);
                        if (p != null) {
                            p.setHealth(p.getMaxHealth());
                            p.setSaturation(20);
                            p.setFoodLevel(20);
                            p.setFireTicks(0);
                            p.sendMessage("[AddHearts] You have been healed!");
                            player.sendMessage("[AddHearts] " + args[1] + " has been healed!");
                        } else {
                            player.sendMessage("[AddHearts] " + args[1] + " is Offline.");
                        }
                    } else {
                        player.performCommand("addhearts help");
                    }
                } else if (args[0].equalsIgnoreCase("default") && player.hasPermission("addhearts.default")) {
                    if (args.length == 2) {
                        double defaultHealth = Double.parseDouble(args[1]);
                        config.set("defaultHealth", defaultHealth);
                        saveCustomConfig();
                        player.sendMessage("[AddHearts] Default Health For New Players Has Been Set To " + defaultHealth + "!");
                    } else {
                        player.performCommand("addhearts help");
                    }
                } else if (args[0].equalsIgnoreCase("setall") && player.hasPermission("addhearts.setall")) {
                    if (args.length == 2) {
                        double setHealth = Double.parseDouble(args[1]);
                        if (getServer().getOnlinePlayers().size() > 0) {
                            Object[] online = getServer().getOnlinePlayers().toArray();
                            for (Object pl : online) {
                                Player p = (Player) pl;
                                config.set("players." + p.getUniqueId() + ".hearts", setHealth);
                                p.setMaxHealth(setHealth);
                                p.sendMessage("[AddHearts] You now have " + setHealth + " hearts!");
                                saveCustomConfig();
                            }
                        }
                        if (getServer().getOfflinePlayers().length > 0) {
                            OfflinePlayer[] offline = (OfflinePlayer[]) getServer().getOfflinePlayers();
                            for (OfflinePlayer p : offline) {
                                config.set("players." + p.getUniqueId() + ".hearts", setHealth);
                                saveCustomConfig();
                            }
                        }
                        player.sendMessage("[AddHearts] All Online and Offline Players Will Have " + args[1]);
                    } else {
                        player.performCommand("addhearts help");
                    }
                }
                return true;
            }
        } else {
            if (command.getName().equalsIgnoreCase("addhearts")) {
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        getLogger().info(help);
                    } else if (args[0].equalsIgnoreCase("add")) {
                        if (args.length == 2) {
                            getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                double oldHealth = p.getMaxHealth();
                                double addedHealth = Double.parseDouble(args[1]);
                                double newHealth = Double.sum(oldHealth, addedHealth);
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                saveCustomConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                                getLogger().info(args[2] + " now has " + newHealth + " hearts!");
                            } else {
                                getLogger().warning(args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("subtract")) {
                        if (args.length == 2) {
                            getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                double oldHealth = p.getMaxHealth();
                                double addedHealth = Double.parseDouble(args[1]);
                                double newHealth = Double.sum(oldHealth, -addedHealth);
                                if (newHealth <= 0) {
                                    getLogger().info("Error: The Player Cannot Have Negative Hearts.");
                                } else {
                                    config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                    saveCustomConfig();
                                    p.setMaxHealth(newHealth);
                                    p.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                                    getLogger().info(args[2] + " now has " + newHealth + " hearts!");
                                }
                            } else {
                                getLogger().warning(args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("set")) {
                        if (args.length == 2) {
                            getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            double newHealth = Double.parseDouble(args[1]);
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                saveCustomConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("[AddHearts] You now have " + newHealth + " hearts!");
                                getLogger().info(args[2] + " now has " + newHealth + " hearts!");
                            } else if (Bukkit.getOfflinePlayer(args[2]).hasPlayedBefore()) {
                                OfflinePlayer oP = Bukkit.getOfflinePlayer(args[2]);
                                config.set("players." + oP.getUniqueId() + ".hearts", newHealth);
                                saveCustomConfig();
                                getLogger().info(args[2] + " is Offline.\nThe heart count is saved in the config file.");
                            } else {
                                getLogger().warning(args[2] + " cannot be found.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("heal")) {
                        if (args.length == 1) {
                            getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 2) {
                            Player p = Bukkit.getPlayer(args[1]);
                            if (p != null) {
                                p.setHealth(p.getMaxHealth());
                                p.setSaturation(20);
                                p.setFoodLevel(20);
                                p.setFireTicks(0);
                                p.sendMessage("[AddHearts] You have been healed!");
                                getLogger().info(args[1] + " has been healed!");
                            } else {
                                getLogger().warning(args[1] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("default")) {
                        if (args.length == 2) {
                            double defaultHealth = Double.parseDouble(args[1]);
                            config.set("defaultHealth", defaultHealth);
                            saveCustomConfig();
                            getLogger().info("Default Health For New Players Has Been Set To " + defaultHealth + "!");
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("setall")) {
                        if (args.length == 2) {
                            double setHealth = Double.parseDouble(args[1]);
                            if (getServer().getOnlinePlayers().size() > 0) {
                                Object[] online = getServer().getOnlinePlayers().toArray();
                                for (Object p : online) {
                                    Player pl = (Player) p;
                                    config.set("players." + pl.getUniqueId() + ".hearts", setHealth);
                                    pl.setMaxHealth(setHealth);
                                    pl.sendMessage("[AddHearts] You now have " + setHealth + " hearts!");
                                    saveCustomConfig();
                                }
                            }
                            if (getServer().getOfflinePlayers().length > 0) {
                                OfflinePlayer[] offline = (OfflinePlayer[]) getServer().getOfflinePlayers();

                                for (OfflinePlayer p : offline) {
                                    config.set("players." + p.getUniqueId() + ".hearts", setHealth);
                                    saveCustomConfig();
                                }
                            }
                            getLogger().info("All Online and Offline Players Will Have " + args[1]);
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("addhearts")) {
            if (args.length == 1) {
                ArrayList<String> subCmd = new ArrayList<>();
                subCmd.add("help");
                if (sender.hasPermission("addhearts.add")) {
                    subCmd.add("add");
                }
                if (sender.hasPermission("addhearts.subtract")) {
                    subCmd.add("subtract");
                }
                if (sender.hasPermission("addhearts.set")) {
                    subCmd.add("set");
                }
                if (sender.hasPermission("addhearts.heal")) {
                    subCmd.add("heal");
                }
                if (sender.hasPermission("addhearts.default")) {
                    subCmd.add("default");
                }
                if (sender.hasPermission("addhearts.reload")) {
                    subCmd.add("reload");
                }
                Collections.sort(subCmd);
                return subCmd;
            } else if (args.length == 2 && !args[0].equalsIgnoreCase("heal") && !args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("reload")) {
                ArrayList<String> subCmd = new ArrayList<>();
                for (int i = 1; i <= 100; i++) {
                    subCmd.add(String.valueOf(i));
                }
                return subCmd;
            }
        }
        return null;
    }
}
