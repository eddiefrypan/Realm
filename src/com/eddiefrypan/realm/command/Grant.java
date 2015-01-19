package com.eddiefrypan.realm.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.eddiefrypan.realm.Coord;
import com.eddiefrypan.realm.CoordArray;
import com.eddiefrypan.realm.Main;

public class Grant implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("grant")) {
				if (args.length > 0) {
					Player op = (Player) sender;
					String ownerName = args[0];

					if (Main.playerCoords.containsKey(ownerName)) {
						CoordArray selection;
						if (Main.selections.containsKey(op.getName()))
							selection = Main.selections.get(op.getName());
						else {
							op.sendMessage(ChatColor.GOLD
									+ "No selection made. Using current single block");
							Location loc = op.getLocation();
							Coord single = new Coord(loc.getBlockX(),
									loc.getBlockZ(), loc.getBlockZ());
							selection = new CoordArray(single);
						}

						Main.playerCoords.get(ownerName).addCoords(selection);
						Main.selections.remove(op.getName());
						Main.coordsChanged = true;
						Main.plugin.getConfig().set(
								"coords." + ownerName,
								Main.playerCoords.get(ownerName)
										.getCoordString());

						op.sendMessage(ChatColor.GREEN
								+ "Granted selection to " + ownerName);
						Player player = Main.plugin.getServer().getPlayer(
								ownerName);
						if (player != null)
							player.sendMessage(ChatColor.GREEN
									+ op.getName()
									+ " has just given you land for your realm!");
					} else {
						CoordArray newCoords = null;
						if (Main.selections.containsKey(op.getName())) {
							newCoords = Main.selections.get(op.getName());
						} else {
							op.sendMessage(ChatColor.GOLD
									+ "No selection made. Using current single block");
							Location loc = op.getLocation();
							Coord[] c = { new Coord(loc.getBlockX(),
									loc.getBlockZ(), loc.getBlockZ()) };
							newCoords = new CoordArray(c);
						}

						Main.playerCoords.put(ownerName, newCoords);
						Main.coordsChanged = true;
						Main.selections.remove(op.getName());
						Main.plugin.getConfig().set("coords." + ownerName,
								newCoords.getCoordString());
						Main.plugin.getConfig().set(
								"coords.players",
								Main.plugin.getConfig().getString(
										"coords.players") == null ? ownerName
										+ ";" : Main.plugin.getConfig()
										.getString("coords.players")
										+ ownerName + ";");

						op.sendMessage(ChatColor.GREEN
								+ "Granted selection to " + ownerName);
						Player player = Main.plugin.getServer().getPlayer(
								ownerName);
						if (player != null)
							player.sendMessage(ChatColor.GREEN
									+ op.getName()
									+ " has just given you land for your realm!");
					}

					Main.plugin.saveConfig();
				} else
					return false;
			} else
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to use /grant");
		} else
			sender.sendMessage("You must be a player to use /grant");
		return true;
	}
}
