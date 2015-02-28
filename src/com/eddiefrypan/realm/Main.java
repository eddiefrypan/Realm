package com.eddiefrypan.realm;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JavaPlugin {
    public static Main plugin;
    //public static HashMap<String, CoordArray> playerCoords;
    //public static HashMap<String, String> realms;
    public static HashMap<String, CoordArray> selections;
    //public static HashMap<String, CoordArray> trusts;
    public static ArrayList<String> trustworthy;
    //public static boolean coordsChanged;
    //public static boolean trustsChanged;
    private FileConfiguration config;
    public static final String opCheck = "landlord";

    @Override
    public void onEnable() {
        plugin = this;
        //playerCoords = new HashMap<String, CoordArray>();
        //realms = new HashMap<String, String>();
        selections = new HashMap<String, CoordArray>();
        //trusts = new HashMap<String, CoordArray>();
        trustworthy = new ArrayList<String>();
        //coordsChanged = false;
        //trustsChanged = false;
        config = getConfig();
        loadConfig();
        new EventListener();
        // getCommand("grant").setExecutor(new Grant());
        // getCommand("revoke").setExecutor(new Revoke());
        // getCommand("realm").setExecutor(new Realm());
        // getCommand("select").setExecutor(new Select());
        // getCommand("trust").setExecutor(new Trust());
        // getCommand("distrust").setExecutor(getCommand("trust").getExecutor());

    }

    @Override
    public void onDisable() {
    }

    private String path(String... dir) {
        String path = "";
        for (int i = 0; i < dir.length - 1; i++) {
            path += dir[i];
            path += ".";
        }
        path += dir[dir.length - 1];
        return path;
    }

    private String getString(String... dir) {
        String path = path(dir);
        return config.getString(path);
    }

    private void loadConfig() {
        String version = getConfig().getString("version");
        if (version == null) {
            getConfig().set("version", getDescription().getVersion());
        }
        // TODO: create config updater

        String rawOwners = getString("realms", "owners");
        String[] owners = rawOwners == null ? null : rawOwners.split(";");
        if (owners != null)
            for (String owner : owners) {
                String realmName = config.getString("realms." + owner);
                if (realmName != null) {
                    String root = "realms." + owner;

                    Realm realm = new Realm(owner, realmName);

                    String territory = getString(root, "territory");
                    if (territory != null) {
                        String[] stringCoords = territory.split(";");
                        for (String stringCoord : stringCoords) {
                            String[] stringNums = stringCoord.split("#");
                            int x = Integer.parseInt(stringNums[0]);
                            int firstZ = Integer.parseInt(stringNums[1]);
                            int lastZ = Integer.parseInt(stringNums[2]);
                            realm.territory.addCoord(new Coord(x, firstZ, lastZ));
                        }
                    }


                    String portionIDs = getString(root, "portions");
                    if (portionIDs != null) {
                        String[] IDs = portionIDs.split(";");
                        for (String ID : IDs) {
                            boolean legit = true;
                            String portionName = getString(root, "portions", ID);
                            Portion portion = null;
                            if (portionName != null) {
                                portion = new Portion(Integer.parseInt(ID), portionName);

                                String rawTerritory = getString(root, "portions", ID);
                                if (rawTerritory != null) {
                                    String[] coords = rawTerritory.split(";");
                                    for (String coord : coords) {
                                        String[] stringNums = coord.split("#");
                                        int x = Integer.parseInt(stringNums[0]);
                                        int firstZ = Integer.parseInt(stringNums[1]);
                                        int lastZ = Integer.parseInt(stringNums[2]);
                                        portion.addCoord(new Coord(x, firstZ, lastZ));
                                    }
                                } else legit = false;

                                String rawTrusts = getString(root, "portions", ID, "trusts");
                                if (rawTrusts != null) {
                                    String[] vassals = rawTrusts.split(";");
                                    for (String vassal : vassals)
                                        portion.trusts.add(vassal);
                                }
                            } else legit = false;

                            if (legit) {
                                realm.portions.add(portion);
                            } else {
                                portionIDs = portionIDs.replace(ID + ";", "");
                                config.set(root + ".portions", portionIDs);
                            }
                        }
                    }
                } else {
                    rawOwners = rawOwners.replace(owner + ";", "");
                    config.set("realms.owners", rawOwners);
                }
            }

        String rawTrustworthy = getString("trustworthy");
        String[] trustworthyPlayers = rawTrustworthy == null ? null : rawTrustworthy.split(";");
        if (trustworthyPlayers != null)
            for (String playerName : trustworthyPlayers)
                trustworthy.add(playerName);

        /*
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
                    //realms.put(players[i], realmName);
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

        String rawBeenOnline = Main.plugin.getConfig().getString("hasBeen");
        if (rawBeenOnline != null) {
            String[] beenOnlineNames = rawBeenOnline.split(";");
            for (int i = 0; i < beenOnlineNames.length; i++) {
                trustworthy.add(beenOnlineNames[i]);
            }
        }
        */
    }
}
