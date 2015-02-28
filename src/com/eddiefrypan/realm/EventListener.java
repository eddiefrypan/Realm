package com.eddiefrypan.realm;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

import static com.eddiefrypan.realm.Function.Type.*;
import static com.eddiefrypan.realm.GUI.*;

public class EventListener implements Listener {
    //private static HashMap<String, CoordArray> playerCoords;
    //private static HashMap<String, CoordArray> trusts;

    private static HashMap<String, Function> data;

    // private static Inventory opOnly;

    public EventListener() {
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
        //EventListener.playerCoords = Main.playerCoords;
        //EventListener.trusts = Main.trusts;
        data = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getItem() != null && event.getItem().getType().name().equals("STICK"))
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    if (event.getPlayer().isSneaking())
                        for (String message : Realm.stats(new Coord(block.getX(), block.getZ(), block.getZ())))
                            player.sendMessage(message);
                    else {
                        if (data.containsKey(player.getName())) {
                            Function function = data.get(player.getName());
                            if (function.type.selectionBased) {
                                function.param(new Coord(block.getX(), block.getZ(), block.getZ()));
                                if (function.length() >= function.type.argsNeeded) {
                                    function.run();
                                    player.sendMessage(function.message());
                                    data.remove(player.getName());
                                }
                                return;
                            }
                        }
                        event.getPlayer().openInventory(OPTIONS.getInventory());
                    }
                    return;
            }

        int x, z;
        boolean okay;
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                okay = true;
                break;
            case RIGHT_CLICK_AIR:
                okay = true;
                break;
            case LEFT_CLICK_BLOCK:
                x = block.getX();
                z = block.getZ();
                String name1 = player.getName();
                Coord coord1 = new Coord(x, z, z);
                okay = Realm.allowed(name1, coord1);
                //okay = inTerritory(name1, coord1) || inTrusts(name1, coord1)
                //        || inUnownedTerritory(coord1);
                break;
            case RIGHT_CLICK_BLOCK:
                x = block.getX();
                z = block.getZ();
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

                boolean allowedBlock = false;
                switch (block.getType().name()) {
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
                        allowedBlock = true;
                }

                okay = Realm.allowed(player.getName(), new Coord(x, z, z)) || (!player.isSneaking() && allowedBlock);
                break;
            case PHYSICAL:
                okay = true;
                break;
            default:
                okay = false;
        }

        if (!okay) {
            event.setCancelled(true);
            player.updateInventory();
            player.sendMessage(ChatColor.RED + "No!");
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        String inventoryName = event.getClickedInventory() == null ? null : event.getClickedInventory().getName();
        if (inventoryName != null && !inventoryName.equals("")) {
            if (inventoryName.equals(OPTIONS.title)) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

                if (displayName.equals(OPTIONS.displayName("listRealms"))) {
                    // TODO: List Realms
                } else if (displayName.equals(OPTIONS.displayName("manageRealm"))) {
                    player.closeInventory();
                    player.openInventory(MANAGE_REALM.getInventory());
                } else if (displayName.equals(OPTIONS.displayName("managePortions"))) {
                    player.closeInventory();
                    player.openInventory(MANAGE_PORTIONS.getInventory(player.getName()));
                } else if (displayName.equals(OPTIONS.displayName("op"))) {
                    player.closeInventory();
                    if (player.hasPermission(Main.opCheck))
                        player.openInventory(OP_ONLY.getInventory());
                    else
                        player.sendMessage(ChatColor.RED + "You are not an OP!");
                }
            } else if (inventoryName.startsWith(MANAGE_REALM.title)) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

                if (displayName.equals(MANAGE_REALM.displayName("back"))) {
                    player.closeInventory();
                    player.openInventory(OPTIONS.getInventory());
                } else if (displayName.equals(MANAGE_REALM.displayName("rename"))) {
                    //data.put(player.getName(), new Object[]{RENAME_REALM});
                    data.put(player.getName(), new Function(RENAME_REALM, player.getName()));
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "The next thing you type will become your realm name");
                } else if (displayName.equals(MANAGE_REALM.displayName("changeDescription"))) {
                    //data.put(player.getName(), new Object[]{CHANGE_REALM_DESCRIPTION});
                    data.put(player.getName(), new Function(CHANGE_REALM_DESCRIPTION, player.getName()));
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "The next thing you type will become your realm's description");
                } else if (displayName.equals(MANAGE_REALM.displayName("grantFullTrust"))) {
                    player.closeInventory();
                    player.openInventory(GRANT_FULL_TRUST.getInventory(player.getName()));
                } else if (displayName.equals(MANAGE_REALM.displayName("revokeFullTrust"))) {
                    player.closeInventory();
                    player.openInventory(REVOKE_FULL_TRUST.getInventory(player.getName()));
                }
            } else if (inventoryName.startsWith(GRANT_FULL_TRUST.title)) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

                if (displayName.equals(GRANT_FULL_TRUST.displayName("back"))) {
                    player.closeInventory();
                    player.openInventory(MANAGE_REALM.getInventory(player.getName()));
                    return;
                }

                if (Main.trustworthy.contains(displayName)) {
                    Realm.addFullTrust(player.getName(), displayName);
                    player.sendMessage(ChatColor.GREEN + "You now fully trust " + displayName + " in your realm");
                } else
                    player.sendMessage(ChatColor.RED + "You cannot trust that player");
            } else if (inventoryName.equals(REVOKE_FULL_TRUST.title)) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

                if (displayName.equals(REVOKE_FULL_TRUST.displayName("back"))) {
                    player.closeInventory();
                    player.openInventory(MANAGE_REALM.getInventory(player.getName()));
                    return;
                }

                if (Realm.removeFullTrust(player.getName(), displayName)) {
                    player.sendMessage(ChatColor.GREEN + "Removed " + displayName + " from full trusts");
                } else
                    player.sendMessage(ChatColor.RED + "There was an error in removing " + displayName + " from your full trusts");
                Realm.getRealm(player.getName()).fullTrusts.remove(displayName);

            } else if (inventoryName.startsWith(MANAGE_PORTIONS.title)) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

                if (displayName.equals(MANAGE_PORTIONS.displayName("back"))) {
                    player.closeInventory();
                    player.openInventory(OPTIONS.getInventory());
                } else if (displayName.equals(MANAGE_PORTIONS.displayName("new"))) {
                    //data.put(player.getName(), new Object[]{CREATE_PORTION});
                    data.put(player.getName(), new Function(CREATE_PORTION, player.getName()));
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "The next thing you type will become your new portion's name");
                } else {
                    player.closeInventory();
                    player.openInventory(PORTION.getInventory(player.getName(), displayName));
                }
            } else if (inventoryName.startsWith(PORTION.title)) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

                if (displayName.equals(PORTION.displayName("back"))) {
                    player.closeInventory();
                    player.openInventory(MANAGE_PORTIONS.getInventory(player.getName()));
                } else if (displayName.equals(PORTION.displayName("addLand"))) {
                    String portionName = inventoryName.substring(PORTION.title.length());
                    //data.put(player.getName(), new Object[]{ADD_LAND_TO_PORTION, player.getName(), portionName});
                    data.put(player.getName(), new Function(ADD_LAND_TO_PORTION, player.getName(), portionName));
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "Select a block to begin your land selection");
                } else if (displayName.equals(PORTION.displayName("removeLand"))) {
                    //data.put(player.getName(), new Object[]{REMOVE_LAND_FROM_PORTION});
                } else if (displayName.equals(PORTION.displayName("trust"))) {

                } else if (displayName.equals(PORTION.displayName("distrust"))) {

                } else if (displayName.equals(PORTION.displayName("rename"))) {

                }
            } else if (inventoryName.startsWith(TRUST_PLAYER.title)) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                    Player player = (Player) event.getWhoClicked();
                    final String name = event.getCurrentItem().getItemMeta()
                            .getDisplayName();
                    if (name.equals(player.getName())) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "You cannot trust yourself");
                    } else if (Main.trustworthy.contains(name)) {
                        //data.put(player.getName(), new String[]{name, "-0"});
                        player.closeInventory();
                        //player.openInventory(PORTION_SELECT.getInventory(new Object[]{player.getName()}));
                    } else
                        player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        //String inventoryName = event.getInventory().getName();
        //if (inventoryName != null && inventoryName != "")
            /*if (inventoryName.equals(TRUST_PLAYER.title) || inventoryName.equals(PORTION_SELECT.title) || inventoryName.equals(CONFIRM.title)) {
                String[] data = EventListener.data.get(event.getPlayer().getName());
                if (data != null) {
                    String confirm = data[data.length - 1];
                    if (!confirm.equals("-0"))
                        EventListener.data.remove(event.getPlayer().getName());
                }
            }*/
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (data.containsKey(event.getPlayer().getName())) {
            Function function = data.get(event.getPlayer().getName());
            if (function.type.chatBased) {
                String message = event.getMessage();
                function.param(message);
                function.run();
                event.getPlayer().sendMessage(function.message());
                data.remove(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Main.trustworthy.contains(event.getPlayer().getName())) {
            Main.trustworthy.add(event.getPlayer().getName());

            String trustworthy = "";
            for (String name : Main.trustworthy) {
                trustworthy += name;
                trustworthy += ";";
            }

            Main.plugin.getConfig().set("trustworthy", trustworthy);
            Main.plugin.saveConfig();

            //TRUST_PLAYER.updateGUI(TRUST_PLAYER.ID);
        }
    }
}
