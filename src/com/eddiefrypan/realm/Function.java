package com.eddiefrypan.realm;

import org.bukkit.ChatColor;

public class Function {
    public final Type type;
    public final String player;
    private Object[] param;
    private Result result;

    public Function(Type type, String player, Object... param) {
        this.type = type;
        this.player = player;
        this.param = param;
    }

    public void param(Object object) {
        Object[] newData = new Object[param.length + 1];
        for (int i = 0; i < param.length; i++) newData[i] = param[i];
        newData[newData.length - 1] = object;
        param = newData;
    }

    public void run() {
        switch (type) {
            case RENAME_REALM:
                result = new Result(Realm.changeName(player, (String) param[0]));
            case CHANGE_REALM_DESCRIPTION:
                result = new Result(Realm.changeDescription(player, (String) param[0]));
            case CREATE_PORTION:
                result = new Result(Realm.createPortion(player, (String) param[0]));
            case ADD_LAND_TO_PORTION:
                result = Realm.addLandToPortion(player, (String) param[0], Select.createSelection((Coord) param[1], (Coord) param[2]));
            case REMOVE_LAND_FROM_PORTION:
                result = Realm.removeLandFromPortion(player, (String) param[0], Select.createSelection((Coord) param[1], (Coord) param[2]));
        }
    }

    public int length() {
        return param.length;
    }

    public String message() {
        if (result != null) {
            String message = result.success ? type.success : type.failure;
            message = message.replaceAll("/player", player);
            for (int i = 0; i < param.length; i++) message = message.replaceAll("/p" + i, (String) param[i]);
            for (int i = 0; i < result.specs.length; i++) message = message.replaceAll("/s" + i, result.specs[i] + "");
            return message;
        }
        return null;
    }

    public enum Type {
        // Chat Based
        RENAME_REALM("Your realm's name has changed to /1", "There was an error in changing your realm's name to /1"),
        CHANGE_REALM_DESCRIPTION("Your realm's description has changed", "There was an error in changing your realm's description"),
        CREATE_PORTION("You have successfully created a new portion", "There was an error in creating a new portion"),

        // Selection based
        ADD_LAND_TO_PORTION(3, "Added land to your portion", "There was an error in adding land to your portion"),
        REMOVE_LAND_FROM_PORTION(3, "Removed land from your portion", "There was an error in removing land from your portion");

        public final String success, failure;

        public final boolean chatBased;

        public final boolean selectionBased;
        public final int argsNeeded;

        Type(String success, String failure) {
            chatBased = true;
            this.success = ChatColor.GREEN + success;
            this.failure = ChatColor.RED + failure;

            selectionBased = false;
            argsNeeded = 0;
        }

        Type(int coordsNeeded, String success, String failure) {
            selectionBased = true;
            this.argsNeeded = 2 + coordsNeeded;
            this.success = ChatColor.GREEN + success;
            this.failure = ChatColor.RED + failure;

            chatBased = false;
        }
    }
}
