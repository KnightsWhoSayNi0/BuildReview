package me.knightswhosayni.buildreview;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class BuildReviewCommand implements CommandExecutor {

    BuildReview plugin;

    public BuildReviewCommand(BuildReview plugin) {
        this.plugin = plugin;
    }

    String prefix = ChatColor.DARK_RED + "[" + ChatColor.RED + "Review" + ChatColor.DARK_RED + "]";

    ArrayList<String> requestline = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //enable check
        if (plugin.getConfig().getBoolean("enable")) {
            //player check
            if (sender instanceof Player) {
                //player
                Player p = (Player) sender;

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("request")) { // /review request
                        if (p.hasPermission("buildreview.request")) { //perm check [buildreview.request]
                            if (plugin.getConfig().getBoolean("requests." + p.getName() + ".active")) {
                                p.sendMessage(prefix + ChatColor.GREEN + " You already have a pending request.");
                            } else {
                                //get & store info
                                Location l = p.getLocation();
                                World w = p.getWorld();
                                //String info = p.getName() + "," + w.getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ();
                                requestline.add(p.getName());
                                plugin.getConfig().createSection("requests." + p.getName());
                                plugin.getConfig().set("requests." + p.getName() + ".active", true);
                                plugin.getConfig().set("requests." + p.getName() + ".x", l.getX());
                                plugin.getConfig().set("requests." + p.getName() + ".y", l.getY());
                                plugin.getConfig().set("requests." + p.getName() + ".z", l.getZ());
                                plugin.getConfig().set("requests." + p.getName() + ".world", w.getName());
                                plugin.saveConfig();

                                p.sendMessage(prefix + ChatColor.GREEN + " Your request has been submitted. A staff member will review it shortly.");
                            }
                        } else { //no request perms
                            p.sendMessage(prefix + " You do not have permission to run this command.");
                        }
                    } else if (args[0].equalsIgnoreCase("position")) { // /review position
                        if (p.hasPermission("buildreview.position")) {
                            int amount = requestline.indexOf(p.getDisplayName());
                            amount++;
                            if (amount > 0) {
                                p.sendMessage(prefix + ChatColor.GREEN + " You are currently " + ChatColor.RED + "#" + amount + ChatColor.GREEN + " in the queue.");
                            } else {
                                p.sendMessage(prefix + ChatColor.GREEN + " You are not in the queue.");
                            }
                        } else { //no position perms
                            p.sendMessage(prefix + " You do not have permission to run this command.");
                        }
                    } else if (args[0].equalsIgnoreCase("teleport")) { // /review teleport
                        if (p.hasPermission("buildreview.admin")) { //perm check [buildreview.admin]
                            if (requestline.size() > 0) { //player in queue?
                                World world = Bukkit.getWorld((String) Objects.requireNonNull(plugin.getConfig().get("requests." + requestline.get(0) + ".world")));
                                double x = (double) plugin.getConfig().get("requests." + requestline.get(0) + ".x");
                                double y = (double) plugin.getConfig().get("requests." + requestline.get(0) + ".y");
                                double z = (double) plugin.getConfig().get("requests." + requestline.get(0) + ".z");
                                Location location = new Location(world, x, y, z);
                                p.teleport(location);
                                p.sendMessage(prefix + ChatColor.GREEN + " Teleporting you to the first build in the queue.");
                                plugin.getConfig().set("requests." + p.getName() + ".active", false);
                                plugin.saveConfig();
                                requestline.remove(0);
                            } else { //no one in queue
                                p.sendMessage(prefix + ChatColor.GREEN + " There is no one in the queue.");
                            }
                        } else { //no admin perms
                            p.sendMessage(prefix + " You do not have permission to run this command.");
                        }
                    } else if (args[0].equalsIgnoreCase("list")) { // /review list
                        if (p.hasPermission("buildreview.list")) {
                            if (p.hasPermission("buildreview.admin")) { //perm check [buildreview.admin]
                                p.sendMessage(prefix + ChatColor.GREEN + "The queue is " + ChatColor.RED + requestline.size() + ChatColor.GREEN + " players long.");
                                p.sendMessage(ChatColor.RED + "Player List");
                                p.sendMessage(ChatColor.GREEN + requestline.toString());
                            } else { //no admin perms
                                p.sendMessage(prefix + ChatColor.GREEN + "The queue is " + ChatColor.RED + requestline.size() + ChatColor.GREEN + " players long.");
                            }
                        } else { //no list perms
                            p.sendMessage(prefix + " You do not have permission to run this command.");
                        }
                    } else if (args[0].equalsIgnoreCase("help")) { // /review help
                        p.sendMessage(ChatColor.GREEN + "===============" + prefix + ChatColor.GREEN + "===============");
                        p.sendMessage(ChatColor.DARK_AQUA + "By " + ChatColor.DARK_GREEN + "KnightsWhoSayNi_");
                        p.sendMessage(ChatColor.DARK_AQUA + "Commands:");
                        p.sendMessage(ChatColor.DARK_RED + "/review request" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Submits a request to be reviewed.");
                        p.sendMessage(ChatColor.DARK_RED + "/review position" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Displays your position in the queue.");
                        if (p.hasPermission("buildreview.admin")) {
                            p.sendMessage(ChatColor.DARK_RED + "/review list" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Lists the players in the queue.");
                        } else {
                            p.sendMessage(ChatColor.DARK_RED + "/review list" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Lists the amount players in the queue.");
                        }
                        if (p.hasPermission("buildreview.admin")) {
                            p.sendMessage(ChatColor.DARK_RED + "/review teleport" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Teleports you to the first build in the queue");
                        }
                        p.sendMessage(ChatColor.GREEN + "=====================================");

                    }
                } else { //help command
                    p.sendMessage(ChatColor.GREEN + "===============" + prefix + ChatColor.GREEN + "===============");
                    p.sendMessage(ChatColor.DARK_AQUA + "By " + ChatColor.DARK_GREEN + "KnightsWhoSayNi_");
                    p.sendMessage(ChatColor.DARK_AQUA + "Commands:");
                    p.sendMessage(ChatColor.DARK_RED + "/review request" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Submits a request to be reviewed.");
                    p.sendMessage(ChatColor.DARK_RED + "/review position" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Displays your position in the queue.");
                    if (p.hasPermission("buildreview.admin")) {
                        p.sendMessage(ChatColor.DARK_RED + "/review list" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Lists the players in the queue.");
                    } else {
                        p.sendMessage(ChatColor.DARK_RED + "/review list" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Lists the amount players in the queue.");
                    }
                    if (p.hasPermission("buildreview.admin")) {
                        p.sendMessage(ChatColor.DARK_RED + "/review teleport" + ChatColor.DARK_AQUA + " | " + ChatColor.RED + "Teleports you to the first build in the queue");
                    }
                    p.sendMessage(ChatColor.GREEN + "=====================================");

                }
            } else {
                //console, not player
                System.out.println(prefix + " You need to be a player to run this command!");
            }
        } else { //not enabled
            System.out.println(prefix + " The plugin is not enabled.");
        }

        return true;
    }
}
