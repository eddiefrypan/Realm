package com.eddiefrypan.realm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public enum GUI {
    // 0 1 2 3 4 5 6 7 8
    // Main Options
    OPTIONS("Realm Options",
            new Item("listRealms", 1, "List Realms", Material.BOOK),
            new Item("manageRealm", 3, "Manage Realm", Material.PAPER),
            new Item("managePortions", 5, "Manage Portions", Material.GRASS),
            new Item("op", 7, "Op Only", Material.REDSTONE)),

    // Manage Realm
    MANAGE_REALM("Manage Realm",
            Item.BACK,
            new Item("rename", 1, "Rename", Material.PAPER),
            new Item("changeDescription", 2, "Change Description", Material.GLASS),
            new Item("grantFullTrust", 3, "Grant Full Trust", Material.SKULL_ITEM, 1, (short) 3),
            new Item("revokeFullTrust", 4, "Revoke Full Trust", Material.SKULL_ITEM, 1, (short) 4)),
    // Fully trust a player so he can build anywhere
    GRANT_FULL_TRUST("Grant Full Trust"),
    // Get rid of a player's ability to build anywhere if already given permission
    REVOKE_FULL_TRUST("Revoke Full Trust"),

    // Configure any realm portions
    MANAGE_PORTIONS("Manage Portions"),
    // Individual portion
    PORTION("Portion", true,
            Item.BACK,
            new Item("info", 1, "Info", Material.MAP),
            new Item("addLand", 2, "Add Land", Material.GRASS),
            new Item("removeLand", 3, "Remove Land", Material.TNT),
            new Item("trust", 4, "Trust a Player", Material.SKULL_ITEM, 1, (short) 3),
            new Item("distrust", 5, "Distrust a Player", Material.SKULL_ITEM, 1, (short) 4),
            new Item("rename", 6, "Rename", Material.BOOK_AND_QUILL)),
    // Trust Player - picking a head
    TRUST_PLAYER("Trust a Player"),
    // Distrust a player
    DISTRUST_PLAYER("Distrust a Player"),

    // Op Only - can then grant or revoke
    OP_ONLY("Op",
            Item.BACK,
            new Item("grant", 3, "Grant", Material.WATER_BUCKET),
            new Item("revoke", 5, "Revoke", Material.LAVA_BUCKET)),
    // Give a player land
    GRANT_LAND("Grant Land"),
    // Take land from a player
    REVOKE_LAND("Revoke Land");

    public final int ID;
    public final String title;
    private Inventory inventory;
    private final boolean updateable;
    private Item[] items;

    // Dynamic w/ items
    GUI(String title, boolean updateable, Item... itemsArg) {
        ID = Item.guiCount++;
        this.title = title;
        this.updateable = updateable;
        items = itemsArg;
        inventory = Bukkit.createInventory(null, inventorySize(itemsArg.length), title);
        for (Item item : items)
            inventory.setItem(item.slot, item.stack);
    }

    // Static inventory
    GUI(String title, Item... itemsArg) {
        ID = Item.guiCount++;
        this.title = title;
        items = itemsArg;
        updateable = false;
        inventory = Bukkit.createInventory(null, inventorySize(itemsArg.length), title);
        for (Item item : items)
            inventory.setItem(item.slot, item.stack);
    }


    // Dynamic inventory
    GUI(String title) {
        ID = Item.guiCount++;
        this.title = title;
        updateable = true;
        inventory = Bukkit.createInventory(null, 9, title + " (NULL)");
    }

    public void updateGUI(String... args) {
        if (ID == GRANT_FULL_TRUST.ID) {
            // Needs: ownerName

            String ownerName = args[0];

            ArrayList<String> trustworthy = Main.trustworthy;
            ArrayList<String> alreadyTrusted = Realm.getRealm(ownerName).fullTrusts;
            // Subtract already granted, subtract 1 for owner, and add 1 for back button
            int size = trustworthy.size() - alreadyTrusted.size(); // -1 and +1 cancel
            inventory = Bukkit.createInventory(null, inventorySize(size), title);
            items = new Item[size];
            items[0] = Item.BACK;
            int offset = 0;
            for (int i = 0; i < trustworthy.size(); i++)
                if (trustworthy.get(i).equals(ownerName) || alreadyTrusted.contains(trustworthy.get(i))) offset++;
                else {
                    Item playerHead = new Item("trustworthy." + i, i - offset + 1, trustworthy.get(i), Material.SKULL_ITEM, 1, (short) 3);
                    items[i - offset + 1] = playerHead;
                    inventory.setItem(i - offset + 1, playerHead.stack);
                }
        } else if (ID == REVOKE_FULL_TRUST.ID) {
            // Needs: ownerName

            ArrayList<String> alreadyTrusted = Realm.getRealm(args[0]).fullTrusts;
            inventory = Bukkit.createInventory(null, inventorySize(alreadyTrusted.size() + 1), title);
            items = new Item[alreadyTrusted.size() + 1];
            items[0] = Item.BACK;
            for (int i = 0; i < alreadyTrusted.size(); i++) {
                Item playerHead = new Item("trustworthy." + i, i + 1, alreadyTrusted.get(i), Material.SKULL_ITEM, 1, (short) 4);
                items[i + 1] = playerHead;
                inventory.setItem(i + 1, playerHead.stack);
            }
        } else if (ID == MANAGE_PORTIONS.ID) {
            // Needs: ownerName

            ArrayList<Portion> portions = Realm.getRealm(args[0]).portions;
            inventory = Bukkit.createInventory(null, inventorySize(portions.size() + 2), title);
            items = new Item[portions.size() + 2];
            items[0] = Item.BACK;
            Item paper = new Item("new", 1, "New...", Material.INK_SACK);
            inventory.setItem(1, paper.stack);
            items[1] = paper;
            for (int i = 0; i < portions.size(); i++) {
                paper = new Item("portions." + i, i + 2, portions.get(i).name(), Material.PAPER);
                items[i + 2] = paper;
                inventory.setItem(i + 2, paper.stack);
            }
        } else if (ID == PORTION.ID) {
            // Needs: ownerName, portionName

            String owner = args[0];
            String portionName = args[1];
            inventory = Bukkit.createInventory(null, inventorySize(items.length), title + " - " + portionName);
            Portion portion = Realm.getRealm(owner).getPortion(portionName);

            for (Item item : items) {
                switch (item.ID) {
                    case "info":
                        ItemMeta meta = item.stack.getItemMeta();
                        ArrayList<String> lore = new ArrayList<>();
                        if (portion == null) {
                            lore.add("Error getting info");
                        } else {
                            lore.add("Name: " + portion.name());
                            int size = portion.size();
                            lore.add("Size: " + size);
                            lore.add("Trusted: ");
                            for (String trusting : portion.trusts) lore.add("  " + trusting);
                        }
                        meta.setLore(lore);
                        break;
                }
                inventory.setItem(item.slot, item.stack);
            }
        } else if (ID == TRUST_PLAYER.ID) {
            // Needs: owner, portionName

            String owner = args[0];
            String portionName = args[1];

            ArrayList<String> trustworthy = Main.trustworthy;
            ArrayList<String> alreadyTrusted = Realm.getRealm(owner).getPortion(portionName).trusts;
            int size = trustworthy.size() - alreadyTrusted.size(); // -1 and +1 cancel
            inventory = Bukkit.createInventory(null, inventorySize(size), title + " - " + portionName);
            items = new Item[size];
            items[0] = Item.BACK;
            int offset = 0;
            for (int i = 0; i < trustworthy.size(); i++)
                if (trustworthy.get(i).equals(owner) || alreadyTrusted.contains(trustworthy.get(i))) offset++;
                else {
                    Item playerHead = new Item("trustworthy." + i, i - offset + 1, trustworthy.get(i), Material.SKULL_ITEM, 1, (short) 3);
                    items[i - offset + 1] = playerHead;
                    inventory.setItem(i - offset + 1, playerHead.stack);
                }
        } else if (ID == DISTRUST_PLAYER.ID) {
            // Needs: owner, portionName

            String owner = args[0];
            String portionName = args[1];

            ArrayList<String> alreadyTrusted = Realm.getRealm(owner).getPortion(portionName).trusts;
            int size = alreadyTrusted.size() + 1;
            inventory = Bukkit.createInventory(null, inventorySize(size), title + " - " + portionName);
            items = new Item[size];
            items[0] = Item.BACK;
            for (int i = 0; i < alreadyTrusted.size(); i++) {
                Item playerHead = new Item("trustworthy." + i, i + 1, alreadyTrusted.get(i), Material.SKULL_ITEM, 1, (short) 4);
                items[i + 1] = playerHead;
                inventory.setItem(i + 1, playerHead.stack);
            }
        }
    }

    public Inventory getInventory(String ... args) {
        if (updateable)
            updateGUI(args);
        return inventory;
    }

    public String displayName(String ID) {
        for (Item item : items)
            if (item.ID.equals(ID)) return item.name;
        return null;
    }

    private int inventorySize(int size) {
        return size == 0 ? 9 : 9 * (size / 9 + (size % 9 == 0 ? 0 : 1));
    }
}
