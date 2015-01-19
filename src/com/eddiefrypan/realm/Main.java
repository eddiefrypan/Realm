package com.eddiefrypan.realm;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import com.eddiefrypan.realm.command.Grant;
import com.eddiefrypan.realm.command.Realm;
import com.eddiefrypan.realm.command.Revoke;
import com.eddiefrypan.realm.command.Select;
import com.eddiefrypan.realm.command.Trust;

public class Main extends JavaPlugin {
	public static Main plugin;
	public static HashMap<String, CoordArray> playerCoords;
	public static HashMap<String, String> realms;
	public static HashMap<String, CoordArray> selections;
	public static HashMap<String, CoordArray> trusts;
	public static boolean coordsChanged;
	public static boolean trustsChanged;

	@Override
	public void onEnable() {
		plugin = this;
		playerCoords = new HashMap<String, CoordArray>();
		realms = new HashMap<String, String>();
		selections = new HashMap<String, CoordArray>();
		trusts = new HashMap<String, CoordArray>();
		coordsChanged = false;
		trustsChanged = false;
		loadConfig();
		new EventListener();
		getCommand("grant").setExecutor(new Grant());
		getCommand("revoke").setExecutor(new Revoke());
		getCommand("realm").setExecutor(new Realm());
		getCommand("select").setExecutor(new Select());
		getCommand("trust").setExecutor(new Trust());
		getCommand("distrust").setExecutor(getCommand("trust").getExecutor());
	}

	@Override
	public void onDisable() {
	}

	private void loadConfig() {
		String version = getConfig().getString("version");
		if (version == null) {
			getConfig().set("version", getDescription().getVersion());
		}

		String rawCoordsPlayers = getConfig().getString("coords.players");
		if (rawCoordsPlayers != null) {
			String[] players = rawCoordsPlayers.split(";");
			for (int i = 0; i < players.length; i++) {
				String jumbledCoords = getConfig().getString(
						"coords." + players[i]);
				if (jumbledCoords != null) {
					String[] ranges = jumbledCoords.split(";");

					Coord[] coords = new Coord[ranges.length];
					for (int x = 0; x < ranges.length; x++) {
						String[] preParse = ranges[x].split("#");

						int[] coord = new int[preParse.length];
						for (int y = 0; y < preParse.length; y++) {
							coord[y] = Integer.parseInt(preParse[y]);
						}
						coords[x] = new Coord(coord[0], coord[1], coord[2]);
					}
					playerCoords.put(players[i], new CoordArray(coords));
				} else {
					String newPlayersRaw = rawCoordsPlayers.replaceAll(
							players[i] + ";", "");
					getConfig().set("coords.players", newPlayersRaw);
				}

				String realmName = getConfig()
						.getString("realms." + players[i]);
				if (realmName != null) {
					realms.put(players[i], realmName);
				}
			}
		}

		String rawTrustPlayers = getConfig().getString("trusts.players");
		if (rawTrustPlayers != null) {
			String[] players = rawTrustPlayers.split(";");
			for (int i = 0; i < players.length; i++) {
				String jumbledTrusts = getConfig().getString(
						"trusts." + players[i]);
				if (jumbledTrusts != null) {
					String[] ranges = jumbledTrusts.split(";");

					Coord[] coords = new Coord[ranges.length];
					for (int x = 0; x < ranges.length; x++) {
						String[] preParse = ranges[x].split("#");

						int[] coord = new int[preParse.length];
						for (int y = 0; y < preParse.length; y++) {
							coord[y] = Integer.parseInt(preParse[y]);
						}
						coords[x] = new Coord(coord[0], coord[1], coord[2]);
					}

					trusts.put(players[i], new CoordArray(coords));
				} else {
					String newPlayersRaw = rawTrustPlayers.replaceAll(
							players[i] + ";", "");
					getConfig().set("trusts.players", newPlayersRaw);
				}
			}
		}
	}
}
