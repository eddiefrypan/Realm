package com.eddiefrypan.realm;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Item {
    public static int guiCount = 0;
    public static Item BACK = new Item("back", 0, "Back", Material.OBSIDIAN);

    public final String ID;
    public final int slot;
    public final ItemStack stack;
    public final String name;

    public Item(String ID, int slot, String name, Material material, Object... objects) {
        this.ID = ID;
        this.slot = slot;
        switch (objects.length) {
            case 0:
                stack = new ItemStack(material);
                break;
            case 1:
                stack = new ItemStack(material, (int) objects[0]);
                break;
            case 2:
                stack = new ItemStack(material, (int) objects[0],
                        (short) objects[1]);
                break;
            default:
                stack = new ItemStack(material);
        }

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        this.stack.setItemMeta(meta);
        this.name = name;
    }
}
