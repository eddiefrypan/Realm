package com.eddiefrypan.realm.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.eddiefrypan.realm.Coord;
import com.eddiefrypan.realm.CoordArray;
import com.eddiefrypan.realm.Main;

public class Realm implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("name")) {
					if (args.length > 1) {
						String name = args[1];
						Main.realms.put(player.getName(), name);
						Main.plugin.getConfig().set(
								"realms." + player.getName(), args[1]);
						Main.plugin.saveConfig();
						player.sendMessage(ChatColor.GREEN
								+ "Your realm's name is now " + ChatColor.AQUA
								+ name);
					} else
						return false;
				} else
					return false;
			} else {
				int x = player.getLocation().getBlockX();
				int z = player.getLocation().getBlockZ();

				HashMap<String, CoordArray> playerCoords = Main.playerCoords;
				Set<String> keySet = playerCoords.keySet();
				boolean found = false;
				String playerName = null;
				for (String keySetName : keySet) {
					found = playerCoords.get(keySetName).containsCoord(
							new Coord(x, z, z));
					if (found) {
						playerName = keySetName;
						break;
					}
				}

				if (found) {
					ArrayList<String> trustedInRealm = new ArrayList<String>();
					HashMap<String, CoordArray> trusts = Main.trusts;
					Set<String> trustsKeySet = trusts.keySet();
					// for (Coord coord : Main.playerCoords.get(playerName)
					// .getCoords()) {
					// for (String keySetName : trustsKeySet) {
					// CoordArray trustsCoords = trusts.get(keySetName);
					// if (trustsCoords.containsCoord(coord)) {
					// if (trustsCoords.containsCoord(new Coord(x, z,
					// z)))
					// spotTrusts.add(keySetName);
					// else
					// inRealm.add(keySetName);
					// break;
					// }
					// }
					// }
					for (String name : trustsKeySet) {
						CoordArray coords = Main.trusts.get(name);
						if (Main.playerCoords.get(playerName).touchesCoords(
								coords))
							trustedInRealm.add(name);
					}

					String message;

					String realmName = Main.realms.get(playerName);
					if (realmName == null) {
						message = ChatColor.GOLD
								+ "This block currently belongs to "
								+ ChatColor.AQUA + playerName;
					} else {
						message = ChatColor.GOLD + "This is the land of "
								+ ChatColor.AQUA + realmName + ChatColor.GOLD
								+ "\nOwner: " + ChatColor.BLUE + playerName
								+ "\n";
					}

					// if (spotTrusts.size() > 0) {
					// message += ChatColor.GOLD
					// + "Vassals trusted right here:\n"
					// + ChatColor.AQUA;
					// for (String name : spotTrusts) {
					// message += name;
					// message += "\n";
					// }
					// }

					if (trustedInRealm.size() > 0) {
						message += ChatColor.GOLD
								+ "Vassals trusted in the realm:\n";
						message += ChatColor.YELLOW;
						for (String name : trustedInRealm) {
							message += name;
							message += "\n";
						}
					}

					player.sendMessage(message);
				} else
					player.sendMessage(ChatColor.GOLD
							+ "This block is currently unowned. Ask an admin to claim it");
			}
		} else
			sender.sendMessage(ChatColor.RED
					+ "You must be a player to use /realm");
		return true;
	}
}
