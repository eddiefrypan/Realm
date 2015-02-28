package com.eddiefrypan.realm;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public class Realm {
    private static ArrayList<Realm> realms;

    public final String owner;
    public String name;
    public String description;
    public CoordArray territory;
    public ArrayList<Portion> portions;
    public ArrayList<String> fullTrusts;

    public Realm(String owner, String name) {
        this.owner = owner;
        this.name = name;
        description = "";
        territory = new CoordArray();
        portions = new ArrayList<Portion>();
        fullTrusts = new ArrayList<String>();
        if (realms == null) realms = new ArrayList<Realm>();
        realms.add(this);
    }

    public static boolean changeName(String ownerName, String newName) {
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName)) {
                realm.name = newName;
                return true;
            }
        return false;
    }

    public static boolean changeDescription(String ownerName, String newDescription) {
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName)) {
                realm.description = newDescription;
                return true;
            }
        return false;
    }

    public static boolean addFullTrust(String ownerName, String trusting) {
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName)) {
                if (!realm.fullTrusts.contains(trusting)) {
                    realm.fullTrusts.add(trusting);
                    return true;
                }
                return false;
            }
        return false;
    }

    public static boolean removeFullTrust(String ownerName, String disTrusting) {
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName)) {
                if (realm.fullTrusts.contains(disTrusting)) {
                    realm.fullTrusts.remove(disTrusting);
                    return true;
                }
                return false;
            }
        return false;
    }

    public static boolean createPortion(String ownerName, String portionName) {
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName)) {
                //boolean contains = false;
                for (Portion portion : realm.portions)
                    if (portion.name().equals(portionName)) {
                        return false;
                    }

                realm.portions.add(new Portion(realm.portions.size(), portionName));
                return true;
            }
        return false;
    }

    public static Result addLandToPortion(String ownerName, String portionName, CoordArray selection) {
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName))
                for (Portion portion : realm.portions)
                    if (portion.name().equals(portionName)) {
                        selection = realm.territory.overLap(selection);
                        portion.addCoords(selection);
                        return new Result(true, selection.size());
                    }
        return new Result(false);
    }

    public static Result removeLandFromPortion(String ownerName, String portionName, CoordArray selection) {
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName))
                for (Portion portion : realm.portions)
                    if (portion.name().equals(portionName)) {
                        selection = realm.territory.overLap(selection);
                        portion.removeCoords(selection);
                        return new Result(true, selection.size());
                    }
        return new Result(false);
    }

    public int addTrust(String portionName, String playerName) {
        for (Portion portion : portions)
            if (portion.name().equals(portionName))
                if (portion.trusts.contains(playerName))
                    return 1;
                else {
                    portion.trusts.add(playerName);
                    return 0;
                }
        return -1;
    }

    public Portion getPortion(String portionName) {
        Portion portion = null;
        for (Portion p : portions) if (p.name() == portionName) portion = p;
        return portion;
    }

    public static boolean inTerritory(String ownerName, Coord coord) {
        Realm realm = null;
        for (Realm r : realms)
            if (realm.owner.equals(ownerName)) realm = r;

        if (realm == null) return false;
        else return realm.territory.containsCoord(coord);
    }


    public static Realm getRealm(String ownerName) {
        Realm r = null;
        for (Realm realm : realms)
            if (realm.owner.equals(ownerName)) r = realm;
        return r;
    }

    public static LandType diplomacy(String playerName, Coord coord) {
        LandType landType = null;
        Realm realm = null;
        for (Realm r : realms)
            if (r.territory.containsCoord(coord)) {
                realm = r;
                landType = LandType.OTHER_TERRITORY;
                break;
            }

        if (realm == null) return LandType.UNOWNED;

        if (realm.owner.equals(playerName)) landType = LandType.OWN_TERRITORY;
        else
            for (Portion portion : realm.portions)
                if (portion.trusts.contains(playerName)) {
                    landType = LandType.TRUST;
                    break;
                }

        return landType;
    }

    public static boolean allowed(String playerName, Coord coord) {
        return diplomacy(playerName, coord) == LandType.OWN_TERRITORY || diplomacy(playerName, coord) == LandType.TRUST || diplomacy(playerName, coord) == LandType.UNOWNED;
    }

    public static String[] stats(Coord coord) {
        Realm realm = null;
        for (Realm r : realms)
            if (r.territory.containsCoord(coord)) {
                realm = r;
                break;
            }

        if (realm == null)
            return new String[]{ChatColor.GOLD + "This land is currently unowned. Ask an admin to claim it"};
        else
            return new String[]{ChatColor.AQUA + realm.name,
                    ChatColor.GOLD + "Owner: " + ChatColor.WHITE + realm.owner};
    }
}
