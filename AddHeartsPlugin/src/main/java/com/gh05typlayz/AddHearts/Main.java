package com.gh05typlayz.AddHearts;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends JavaPlugin {
    public double defaultHealth = 20;
    @Override
    public void onEnable() { Bukkit.getLogger().info("[AddHearts] Has Started."); }

    @Override
    public void onDisable() { Bukkit.getLogger().info("[AddHearts] Has Stopped."); }

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
                            double oldHealth = player.getHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, addedHealth);
                            player.setMaxHealth(newHealth);
                            player.sendMessage("You now have " + newHealth/2 + " hearts!");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            assert p != null;
                            double oldHealth = p.getHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, addedHealth);
                            if (p.isOnline()) {
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth/2 + " hearts!");
                                player.sendMessage(args[2] + " now has " + newHealth/2 + " hearts!");
                            } else {
                                player.sendMessage( args[2] + " is Offline.");
                            }
                        } else {
                            player.performCommand("addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("subtract") && player.hasPermission("addhearts.subtract")) {
                        if (args.length == 2) {
                            double oldHealth = player.getHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, -addedHealth);
                            player.setMaxHealth(newHealth);
                            player.sendMessage("You now have " + newHealth/2 + " hearts!");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            double oldHealth = p.getHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, -addedHealth);
                            if (p.isOnline()) {
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth/2 + " hearts!");
                                player.sendMessage(args[2] + " now has " + newHealth/2 + " hearts!");
                            } else {
                                player.sendMessage( args[2] + " is Offline.");
                            }
                        } else {
                            player.performCommand("addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("set") && player.hasPermission("addhearts.set")) {
                        if (args.length == 2) {
                            double newHealth = Double.parseDouble(args[1]);
                            player.setMaxHealth(newHealth);
                            player.sendMessage("You now have " + newHealth/2 + " hearts!");
                        } else if (args.length == 3) {
                            double newHealth = Double.parseDouble(args[1]);
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p.isOnline()) {
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth/2 + " hearts!");
                                player.sendMessage(args[2] + " now has " + newHealth/2 + " hearts!");
                            } else {
                                player.sendMessage( args[2] + " is Offline.");
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
                            if (p.isOnline()) {
                                p.setHealth(p.getMaxHealth());
                                p.setSaturation(20);
                                p.setFoodLevel(20);
                                p.setFireTicks(0);
                                p.sendMessage("You have been healed!");
                                player.sendMessage(args[2] + " has been healed!");
                            } else {
                                player.sendMessage( args[2] + " is Offline.");
                            }
                        } else {
                            player.performCommand("addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("default") && player.hasPermission("addhearts.default")) {
                        if (args.length == 2) {
                            defaultHealth = Double.parseDouble(args[1]);
                            player.sendMessage("Default Health For New Players Has Been Set To " + defaultHealth + "!");
                        } else {
                            player.performCommand("addhearts help");
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
                            double oldHealth = p.getHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, addedHealth);
                            if (p.isOnline()) {
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth/2 + " hearts!");
                                getServer().getLogger().info(args[2] + " now has " + newHealth/2 + " hearts!");
                            } else {
                                getServer().getLogger().warning( args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("subtract")) {
                        if (args.length == 2) {
                            getServer().getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            assert p != null;
                            double oldHealth = p.getHealth();
                            double addedHealth = Double.parseDouble(args[1]);
                            double newHealth = Double.sum(oldHealth, -addedHealth);
                            if (p.isOnline()) {
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth/2 + " hearts!");
                                getServer().getLogger().info(args[2] + " now has " + newHealth/2 + " hearts!");
                            } else {
                                getServer().getLogger().warning( args[2] + " is Offline.");
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
                            if (p.isOnline()) {
                                p.setMaxHealth(newHealth);
                                p.sendMessage("You now have " + newHealth/2 + " hearts!");
                                getServer().getLogger().info(args[2] + " now has " + newHealth/2 + " hearts!");
                            } else {
                                getServer().getLogger().info(args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("heal")) {
                        if (args.length == 2) {
                            getServer().getLogger().warning("You have to add a player's name.");
                        } else if (args.length == 3) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p.isOnline()) {
                                p.setHealth(p.getMaxHealth());
                                p.setSaturation(20);
                                p.setFoodLevel(20);
                                p.setFireTicks(0);
                                p.sendMessage("You have been healed!");
                                getServer().getLogger().info(args[2] + " has been healed!");
                            } else {
                                getServer().getLogger().warning( args[2] + " is Offline.");
                            }
                        } else {
                            getServer().dispatchCommand(sender, "addhearts help");
                        }
                    } else if (args[0].equalsIgnoreCase("default")) {
                        if (args.length == 2) {
                            defaultHealth = Double.parseDouble(args[1]);
                            getServer().getLogger().info("Default Health For New Players Has Been Set To " + defaultHealth + "!");
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
