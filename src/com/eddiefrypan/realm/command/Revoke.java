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

public class Revoke implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("revoke")) {
				if (args.length > 0) {
					Player op = (Player) sender;
					String ownerName = args[0];
					CoordArray playerCoords = Main.playerCoords.get(ownerName);

					if (playerCoords == null)
						op.sendMessage(ChatColor.RED
								+ "That player does not own any land");
					else {
						CoordArray selection;
						if (Main.selections.containsKey(op.getName())) {
							selection = Main.selections.get(op.getName());
						} else {
							op.sendMessage(ChatColor.GOLD
									+ "No selection made. Using current single block");
							Location loc = op.getLocation();
							Coord singleCoord = new Coord(loc.getBlockX(),
									loc.getBlockZ(), loc.getBlockZ());
							selection = new CoordArray(singleCoord);
						}

						Main.playerCoords.get(ownerName)
								.removeCoords(selection);
						Main.selections.remove(op.getName());
						Main.coordsChanged = true;
						Main.plugin.getConfig().set(
								"coords." + ownerName,
								Main.playerCoords.get(ownerName)
										.getCoordString());
						Main.plugin.saveConfig();

						op.sendMessage(ChatColor.GREEN + "Removed blocks from "
								+ ownerName + "'s realm");
						Player player = Main.plugin.getServer().getPlayer(
								ownerName);
						if (player != null)
							player.sendMessage(ChatColor.RED + op.getName()
									+ " has revoked blocks from your realm");
					}
				} else
					return false;
			} else
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to use /revoke");
		} else
			sender.sendMessage(ChatColor.RED
					+ "You must be a player to use /revoke");
		return true;
	}
}
