package com.gh05typlayz.AddHearts;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
    FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        Bukkit.getLogger().info("[AddHearts] Has Started.");
        new UpdateChecker(this, 100878).getLatestVerision(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("Plugin is up to date.");
            } else {
                getLogger().warning("Plugin is not up to date.");
            }
        });
    }


    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[AddHearts] Has Stopped.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.isOp()) {
            new UpdateChecker(this, 100878).getLatestVerision(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    player.sendMessage("[AddHearts] Plugin is up to date.");
                } else {
                    player.sendMessage("[AddHearts] Plugin is not up to date.");
                }
            });
        }

        if (player.hasPlayedBefore()) {
            if (!config.contains("players." + player.getUniqueId())) {
                config.set("players." + player.getUniqueId() + ".hearts", player.getMaxHealth());
                this.saveConfig();
            }
            player.setMaxHealth(config.getDouble("players." + player.getUniqueId() + ".hearts"));
        } else {
            config.set("players." + player.getUniqueId() + ".hearts", config.get("defaultHealth"));
            this.saveConfig();
            player.setMaxHealth(config.getDouble("defaultHealth"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("addhearts")) {
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(
                                "/addhearts help - This is the Help Command. (Shows all Commands)\n" +
                                        "/addhearts heal - Heals Yourself.\n" +
                                        "/addhearts heal <Player> - Heals Player.\n" +
                                        "/addhearts add <Number Of Hearts> <Player> - Adds Hearts To Given Player.\n" +
                                        "/addhearts subtract <Number Of Hearts> <Player> - Removes Hearts From Given Player.\n" +
                                        "/addhearts set <Number Of Hearts> <Player> - Sets Number Of Hearts to The Given Player.\n" +
                                        "/addhearts default <Number Of Hearts> - This Changes the default amount of Hearts For New Player Join With.\n" +
                                        "/addhearts setall <Number Of Hearts> - Sets All Online And Offline Player's Hearts to This Value."
                        );
                    } else if (args[0].equalsIgnoreCase("add") && player.hasPermission("addhearts.add")) {
                        if (args.length == 2) {
                            double oldHealth = player.getMaxHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, addedHealth);
                            config.set("players." + player.getUniqueId() + ".hearts", newHealth);
                            this.saveConfig();
                            player.setMaxHealth(newHealth);
                            player.sendMessage("You now have " + newHealth / 2 + " hearts!");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                double oldHealth = p.getMaxHealth();
                                double addedHealth = Double.parseDouble(args[1]);
                                double newHealth = Double.sum(oldHealth, addedHealth);
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                this.saveConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth / 2 + " hearts!");
                                player.sendMessage(args[2] + " now has " + newHealth / 2 + " hearts!");
                            } else {
                                player.sendMessage(args[2] + " is Offline.");
                            }
                        } else {
                            player.performCommand("addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("subtract") && player.hasPermission("addhearts.subtract")) {
                        if (args.length == 2) {
                            double oldHealth = player.getHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, -addedHealth);
                            config.set("players." + player.getUniqueId() + ".hearts", newHealth);
                            this.saveConfig();
                            player.setMaxHealth(newHealth);
                            player.sendMessage("You now have " + newHealth / 2 + " hearts!");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                double oldHealth = p.getMaxHealth();
                                double addedHealth = Double.parseDouble(args[1]);
                                double newHealth = Double.sum(oldHealth, -addedHealth);
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                this.saveConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth / 2 + " hearts!");
                                player.sendMessage(args[2] + " now has " + newHealth / 2 + " hearts!");
                            } else {
                                player.sendMessage(args[2] + " is Offline.");
                            }
                        } else {
                            player.performCommand("addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("set") && player.hasPermission("addhearts.set")) {
                        if (args.length == 2) {
                            double newHealth = Double.parseDouble(args[1]);
                            config.set("players." + player.getUniqueId() + ".hearts", newHealth);
                            this.saveConfig();
                            player.setMaxHealth(newHealth);
                            player.sendMessage("You now have " + newHealth / 2 + " hearts!");
                        } else if (args.length == 3) {
                            double newHealth = Double.parseDouble(args[1]);
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                this.saveConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth / 2 + " hearts!");
                                player.sendMessage(args[2] + " now has " + newHealth / 2 + " hearts!");
                            } else {
                                player.sendMessage(args[2] + " is Offline.");
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
                            player.sendMessage("You have been healed!");
                        } else if (args.length == 2) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                p.setHealth(p.getMaxHealth());
                                p.setSaturation(20);
                                p.setFoodLevel(20);
                                p.setFireTicks(0);
                                p.sendMessage("You have been healed!");
                                player.sendMessage(args[2] + " has been healed!");
                            } else {
                                player.sendMessage(args[2] + " is Offline.");
                            }
                        } else {
                            player.performCommand("addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("default") && player.hasPermission("addhearts.default")) {
                        if (args.length == 2) {
                            double defaultHealth = Double.parseDouble(args[1]);
                            config.set("defaultHealth", defaultHealth);
                            this.saveConfig();
                            getServer().getLogger().info("Default Health For New Players Has Been Set To " + defaultHealth + "!");
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("setall") && player.hasPermission("addhearts.setall")) {
                        if (args.length == 2) {
                            double setHealth = Double.parseDouble(args[1]);
                            Player[] online = (Player[]) getServer().getOnlinePlayers().toArray();
                            OfflinePlayer[] offline = (OfflinePlayer[]) getServer().getOnlinePlayers().toArray();
                            for (Player p : online) {
                                config.set("players." + p.getUniqueId() + ".hearts", setHealth);
                                p.setMaxHealth(setHealth);
                                this.saveConfig();
                            }
                            for (OfflinePlayer p : offline) {
                                config.set("players." + p.getUniqueId() + ".hearts", setHealth);
                                this.saveConfig();
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    }
                }
                return true;
            }
        } else {
            if (command.getName().equalsIgnoreCase("addhearts")) {
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        getServer().getLogger().info(
                                "/addhearts help - This is the Help Command. (Shows all Commands)\n" +
                                        "/addhearts heal - Heals Yourself.\n" +
                                        "/addhearts heal <Player> - Heals Player.\n" +
                                        "/addhearts add <Number Of Hearts> <Player> - Adds Hearts To Given Player.\n" +
                                        "/addhearts subtract <Number Of Hearts> <Player> - Removes Hearts From Given Player.\n" +
                                        "/addhearts set <Number Of Hearts> <Player> - Sets Number Of Hearts to The Given Player.\n" +
                                        "/addhearts default <Number Of Hearts> - This Changes the default amount of Hearts For New Player Join With.\n" +
                                        "/addhearts setall <Number Of Hearts> - Sets All Online And Offline Player's Hearts to This Value."
                        );
                    } else if (args[0].equalsIgnoreCase("add")) {
                        if (args.length == 2) {
                            getServer().getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                double oldHealth = p.getMaxHealth();
                                double addedHealth = Double.parseDouble(args[1]);
                                double newHealth = Double.sum(oldHealth, addedHealth);
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                this.saveConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth / 2 + " hearts!");
                                getServer().getLogger().info(args[2] + " now has " + newHealth / 2 + " hearts!");
                            } else {
                                getServer().getLogger().warning(args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("subtract")) {
                        if (args.length == 2) {
                            getServer().getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                double oldHealth = p.getMaxHealth();
                                ;
                                double addedHealth = Double.parseDouble(args[1]);
                                double newHealth = Double.sum(oldHealth, -addedHealth);
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                this.saveConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth / 2 + " hearts!");
                                getServer().getLogger().info(args[2] + " now has " + newHealth / 2 + " hearts!");
                            } else {
                                getServer().getLogger().warning(args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("set")) {
                        if (args.length == 2) {
                            getServer().getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            double newHealth = Double.parseDouble(args[1]);
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                config.set("players." + p.getUniqueId() + ".hearts", newHealth);
                                this.saveConfig();
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth / 2 + " hearts!");
                                getServer().getLogger().info(args[2] + " now has " + newHealth / 2 + " hearts!");
                            } else if (Bukkit.getOfflinePlayer(args[2]).hasPlayedBefore()) {
                                OfflinePlayer oP = Bukkit.getOfflinePlayer(args[2]);
                                config.set("players." + oP.getUniqueId() + ".hearts", newHealth);
                                this.saveConfig();
                                getServer().getLogger().info(args[2] + " is Offline.\nThe heart count is saved in the config file.");
                            } else {
                                getServer().getLogger().warning(args[2] + " cannot be found.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("heal")) {
                        if (args.length == 2) {
                            getServer().getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                p.setHealth(p.getMaxHealth());
                                p.setSaturation(20);
                                p.setFoodLevel(20);
                                p.setFireTicks(0);
                                p.sendMessage("You have been healed!");
                                getServer().getLogger().info(args[2] + " has been healed!");
                            } else {
                                getServer().getLogger().warning(args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("default")) {
                        if (args.length == 2) {
                            double defaultHealth = Double.parseDouble(args[1]);
                            config.set("defaultHealth", defaultHealth);
                            this.saveConfig();
                            getServer().getLogger().info("Default Health For New Players Has Been Set To " + defaultHealth + "!");
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("setall")) {
                        if (args.length == 2) {
                            double setHealth = Double.parseDouble(args[1]);
                            Player[] online = (Player[]) getServer().getOnlinePlayers().toArray();
                            OfflinePlayer[] offline = (OfflinePlayer[]) getServer().getOnlinePlayers().toArray();
                            for (Player p : online) {
                                config.set("players." + p.getUniqueId() + ".hearts", setHealth);
                                p.setMaxHealth(setHealth);
                                this.saveConfig();
                            }
                            for (OfflinePlayer p : offline) {
                                config.set("players." + p.getUniqueId() + ".hearts", setHealth);
                                this.saveConfig();
                            }
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
                Collections.sort(subCmd);
                return subCmd;
            } else if (args.length == 2 && !args[0].equalsIgnoreCase("heal")) {
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
