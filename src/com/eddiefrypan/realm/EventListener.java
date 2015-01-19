package com.eddiefrypan.realm;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
	private static HashMap<String, CoordArray> playerCoords;
	private static HashMap<String, CoordArray> trusts;

	public EventListener() {
		Main.plugin.getServer().getPluginManager()
				.registerEvents(this, Main.plugin);
		EventListener.playerCoords = Main.playerCoords;
		EventListener.trusts = Main.trusts;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		int x = event.getClickedBlock().getX(), z = event.getClickedBlock()
				.getZ();
		boolean okay = false;
		switch (event.getAction()) {
		case LEFT_CLICK_AIR:
			okay = true;
			break;
		case RIGHT_CLICK_AIR:
			okay = true;
			break;
		case LEFT_CLICK_BLOCK:
			x = event.getClickedBlock().getX();
			z = event.getClickedBlock().getZ();
			String name1 = event.getPlayer().getName();
			Coord coord1 = new Coord(x, z, z);
			okay = inTerritory(name1, coord1) || inTrusts(name1, coord1)
					|| inUnownedTerritory(coord1);
			break;
		case RIGHT_CLICK_BLOCK:
			x = event.getClickedBlock().getX();
			z = event.getClickedBlock().getZ();
			switch (event.getBlockFace()) {
			case UP:
			case SELF:
			case DOWN:
				break;
			case EAST_NORTH_EAST:
			case EAST_SOUTH_EAST:
			case EAST:
				x++;
				break;
			case NORTH_EAST:
			case NORTH_NORTH_EAST:
			case NORTH_NORTH_WEST:
			case NORTH_WEST:
			case NORTH:
				z--;
				break;
			case SOUTH_EAST:
			case SOUTH_SOUTH_EAST:
			case SOUTH_SOUTH_WEST:
			case SOUTH_WEST:
			case SOUTH:
				z++;
				break;
			case WEST_NORTH_WEST:
			case WEST_SOUTH_WEST:
			case WEST:
				x--;
				break;
			default:
				break;
			}

			boolean allowedBlocked = false;
			switch (event.getClickedBlock().getType().name()) {
			case "WOODEN_DOOR":
			case "SPRUCE_DOOR":
			case "BIRCH_DOOR":
			case "JUNGLE_DOOR":
			case "ACACIA_DOOR":
			case "DARK_OAK_DOOR":
			case "LEVER":
			case "STONE_BUTTON":
			case "WOOD_BUTTON":
			case "TRAP_DOOR":
			case "IRON_TRAPDOOR":
			case "FENCE_GATE":
			case "SPRUCE_FENCE_GATE":
			case "BIRCH_FENCE_GATE":
			case "JUNGLE_FENCE_GATE":
			case "DARK_OAK_FENCE_GATE":
			case "ACACIA_FENCE_GATE":
			case "ENDER_CHEST":
			case "WORKBENCH":
				allowedBlocked = true;
			}

			String name = event.getPlayer().getName();
			Coord coord = new Coord(x, z, z);
			okay = inTerritory(name, coord) || inTrusts(name, coord)
					|| inUnownedTerritory(coord)
					|| (!event.getPlayer().isSneaking() && allowedBlocked);
			break;
		case PHYSICAL:
			okay = true;
			break;
		default:
			okay = false;
		}

		if (!okay) {
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			event.getPlayer().sendMessage(ChatColor.RED + "No!");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Main.plugin.getConfig().getString("hasBeen") != null) {
			String[] beenOnlineNames = Main.plugin.getConfig()
					.getString("hasBeen").split(";");
			boolean contains = false;
			for (int i = 0; i < beenOnlineNames.length; i++) {
				if (beenOnlineNames[i].equals(event.getPlayer().getName())) {
					contains = true;
					break;
				}
			}

			if (!contains) {
				Main.plugin.getConfig().set(
						"hasBeen",
						Main.plugin.getConfig().getString("hasBeen")
								+ event.getPlayer().getName() + ";");
			}
		} else {
			Main.plugin.getConfig().set("hasBeen",
					event.getPlayer().getName() + ";");
		}

		Main.plugin.saveConfig();
	}

	public static boolean inTerritory(String playerName, Coord coord) {
		if (Main.coordsChanged) {
			playerCoords = Main.playerCoords;
			Main.coordsChanged = false;
		}

		boolean inTerritory = false;
		CoordArray coords = playerCoords.get(playerName);
		if (coords != null) {
			inTerritory = coords.containsCoord(coord);
		} else
			inTerritory = false;

		return inTerritory;
	}

	public static boolean inTrusts(String playerName, Coord coord) {
		if (Main.trustsChanged) {
			trusts = Main.trusts;
			Main.trustsChanged = false;
		}

		boolean inTrusts = false;
		CoordArray coords = trusts.get(playerName);
		if (coords != null) {
			inTrusts = coords.containsCoord(coord);
		} else
			inTrusts = false;

		return inTrusts;
	}

	public static boolean inUnownedTerritory(Coord coord) {
		if (Main.coordsChanged) {
			playerCoords = Main.playerCoords;
			Main.coordsChanged = false;
		}

		boolean inUnownedTerritory = true;
		for (String player : playerCoords.keySet()) {
			if (playerCoords.get(player).containsCoord(coord))
				inUnownedTerritory = false;
			if (!inUnownedTerritory)
				break;
		}

		return inUnownedTerritory;
	}

}
