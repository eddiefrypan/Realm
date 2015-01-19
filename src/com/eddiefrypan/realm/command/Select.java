package com.eddiefrypan.realm.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.eddiefrypan.realm.Coord;
import com.eddiefrypan.realm.CoordArray;
import com.eddiefrypan.realm.EventListener;
import com.eddiefrypan.realm.Main;

public class Select implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length > 0) {
				switch (args[0]) {
				case "start":
					Player player1 = (Player) sender;
					if (!Main.selections.containsKey(player1.getName())) {
						int x = player1.getLocation().getBlockX();
						int z = player1.getLocation().getBlockZ();
						if (EventListener.inTerritory(player1.getName(),
								new Coord(x, z, z))
								|| player1.hasPermission("grant")) {
							CoordArray start = new CoordArray(
									new Coord(x, z, z));
							Main.selections.put(player1.getName(), start);
							player1.sendMessage(ChatColor.GREEN
									+ "Selection began");
						} else
							player1.sendMessage(ChatColor.RED
									+ "This block is not in your territory");
					} else
						player1.sendMessage(ChatColor.RED
								+ "You are already making a selction");
					break;
				case "end":
					Player player = (Player) sender;
					if (Main.selections.containsKey(player.getName())) {
						int x = player.getLocation().getBlockX();
						int z = player.getLocation().getBlockZ();
						if (EventListener.inTerritory(player.getName(),
								new Coord(x, z, z))
								|| player.hasPermission("grant")) {
							Coord start = Main.selections.get(player.getName())
									.getCoord(0);
							int xDelta = Math.abs(x - start.getX()) + 1;
							int zDelta = Math.abs(z - start.getFirstZ()) + 1;
							if (xDelta <= 50 && zDelta <= 50) {
								Coord[] end = new Coord[xDelta];
								for (int i = 0; i < end.length; i++) {
									end[i] = new Coord(x
											+ (x < start.getX() ? i
													: x > start.getX() ? i * -1
															: 0),
											(z <= start.getFirstZ() ? z : start
													.getFirstZ()),
											(z >= start.getLastZ() ? z : start
													.getLastZ()));
								}

								boolean legit = true;
								if (!player.hasPermission("grant"))
									for (int i = 0; i < end.length; i++) {
										if (!EventListener.inTerritory(
												player.getName(), end[i])) {
											legit = false;
											break;
										}
									}

								if (legit) {
									Main.selections.put(player.getName(),
											new CoordArray(end));
									player.sendMessage(ChatColor.GREEN
											+ "Selection ended");
								} else {
									Main.selections.remove(player.getName());
									player.sendMessage(ChatColor.RED
											+ "Some of the blocks in your selection do not belong to you");
								}
							} else
								player.sendMessage(ChatColor.RED
										+ "Selction must be 50 block or less in each direction");
						} else
							player.sendMessage(ChatColor.RED
									+ "This block is not in your territory");
					} else
						player.sendMessage(ChatColor.RED
								+ "Begin a selction first by using /select start");
					break;
				case "cancel":
					Player player2 = (Player) sender;
					Main.selections.remove(player2.getName());
					player2.sendMessage(ChatColor.GREEN
							+ "Cancelled all of your selections");
					break;
				default:
					return false;
				}
			} else
				return false;
		}

		return true;
	}
}
