package com.eddiefrypan.realm.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.eddiefrypan.realm.CoordArray;
import com.eddiefrypan.realm.Main;

public class Trust implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length > 0) {
				if (Main.selections.containsKey(((Player) sender).getName())) {
					Player player = (Player) sender;
					String trusting = args[0];
					if (trusting.equals(player.getName())) {
						player.sendMessage(ChatColor.RED
								+ "You cannot trust yourself");
						return true;
					}

					boolean hasBeenOnline = false;
					if (Main.plugin.getConfig().getString("hasBeen") != null) {
						String[] beenOnlineNames = Main.plugin.getConfig()
								.getString("hasBeen").split(";");
						for (int i = 0; i < beenOnlineNames.length; i++) {
							if (beenOnlineNames[i].equalsIgnoreCase(trusting)) {
								hasBeenOnline = true;
								break;
							}
						}
					}

					if (hasBeenOnline) {
						if (!Main.trusts.containsKey(trusting)) {
							Main.trusts.put(trusting, new CoordArray());
						}
						CoordArray selection = Main.selections.get(player
								.getName());

						boolean add = label.equalsIgnoreCase("trust");
						Bukkit.broadcastMessage("Add: " + add);
						if (add)
							Main.trusts.get(trusting).addCoords(selection);
						else
							Main.trusts.get(trusting).removeCoords(selection);
						Main.selections.remove(player.getName());
						Main.trustsChanged = true;
						Main.plugin.getConfig().set("trusts." + trusting,
								Main.trusts.get(trusting).getCoordString());
						Main.plugin.getConfig().set(
								"trusts.players",
								Main.plugin.getConfig().getString(
										"trusts.players") == null ? trusting
										+ ";" : Main.plugin.getConfig()
										.getString("trusts.players")
										+ trusting
										+ ";");
						Main.plugin.saveConfig();
						if (add)
							player.sendMessage(ChatColor.GREEN
									+ "Selection trusted with " + trusting);
						else
							player.sendMessage(ChatColor.GREEN + "Distrusted "
									+ trusting + " for your selection");
						Player trustPlayer = Main.plugin.getServer().getPlayer(
								trusting);
						if (trustPlayer != null)
							if (add)
								trustPlayer
										.sendMessage(ChatColor.GREEN
												+ player.getName()
												+ " has just trusted you with some of their land!");
							else
								trustPlayer
										.sendMessage(ChatColor.GREEN
												+ player.getName()
												+ " no longer trusts you with some of their land!");
					} else
						player.sendMessage(ChatColor.RED
								+ "That player has never been on this server. Try using someone who has");
				} else
					sender.sendMessage(ChatColor.RED
							+ "Select a bit of land first by using /select");
			} else
				return false;
		} else
			sender.sendMessage(ChatColor.RED
					+ "You must be a player to use /trust");

		return true;
	}
}
