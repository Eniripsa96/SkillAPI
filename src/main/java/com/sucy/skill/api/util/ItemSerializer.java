/**
 * SkillAPI
 * com.sucy.skill.api.util.ItemSerializer
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.api.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.rit.sucy.reflect.Reflection;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Based on the thread https://bukkit.org/threads/help-with-serialized-nbttagcompounds.116335/
public class ItemSerializer {

    private static boolean initialized = false;

    private static Constructor<?> nbtTagListConstructor;
    private static Constructor<?> nbtTagCompoundConstructor;
    private static Constructor<?> craftItemConstructor;
    private static Constructor<?> craftItemNMSConstructor;
    private static Constructor<?> nmsItemConstructor;

    private static Method itemStack_save;
    private static Method nbtTagList_add;
    private static Method nbtTagList_size;
    private static Method nbtTagList_get;
    private static Method nbtCompressedStreamTools_write;
    private static Method nbtCompressedStreamTools_read;
    private static Method nbtTagCompound_set;
    private static Method nbtTagCompound_getList;
    private static Method nbtTagCompound_isEmpty;

    private static Field craftItemStack_getHandle;

    private static void initialize() {
        if (initialized)
            return;

        initialized = true;

        try {
            String nms = Reflection.getNMSPackage();
            String craft = Reflection.getCraftPackage();

            Class<?> craftItemStack = Class.forName(craft + "inventory.CraftItemStack");
            Class<?> nmsItemStack = Class.forName(nms + "ItemStack");
            craftItemConstructor = craftItemStack.getDeclaredConstructor(ItemStack.class);
            craftItemConstructor.setAccessible(true);
            craftItemNMSConstructor = craftItemStack.getDeclaredConstructor(nmsItemStack);
            craftItemNMSConstructor.setAccessible(true);
            craftItemStack_getHandle = craftItemStack.getDeclaredField("handle");
            craftItemStack_getHandle.setAccessible(true);

            Class<?> nbtTagCompound = Class.forName(nms + "NBTTagCompound");
            Class<?> nbtTagList = Class.forName(nms + "NBTTagList");
            Class<?> nbtBase = Class.forName(nms + "NBTBase");
            Class<?> nbtCompressedStreamTools = Class.forName(nms + "NBTCompressedStreamTools");
            nmsItemConstructor = nmsItemStack.getDeclaredConstructor(nbtTagCompound);
            nmsItemConstructor.setAccessible(true);
            nbtTagCompoundConstructor = nbtTagCompound.getConstructor();
            nbtTagListConstructor = nbtTagList.getConstructor();
            nbtTagCompound_set = nbtTagCompound.getDeclaredMethod("set", String.class, nbtBase);
            nbtTagCompound_getList = nbtTagCompound.getDeclaredMethod("getList", String.class, int.class);
            nbtTagCompound_isEmpty = nbtTagCompound.getDeclaredMethod("isEmpty");
            itemStack_save = nmsItemStack.getDeclaredMethod("save", nbtTagCompound);
            nbtTagList_add = nbtTagList.getDeclaredMethod("add", nbtBase);
            nbtTagList_size = nbtTagList.getDeclaredMethod("size");
            nbtTagList_get = nbtTagList.getDeclaredMethod("get", int.class);
            nbtCompressedStreamTools_write = nbtCompressedStreamTools.getDeclaredMethod("a", nbtTagCompound, DataOutput.class);
            nbtCompressedStreamTools_read = nbtCompressedStreamTools.getDeclaredMethod("a", DataInputStream.class);
        }
        catch (Exception ex) {
            System.out.println("Server doesn't support NBT serialization - resorting to a less complete implementation");
        }
    }

    public static String toBase64(ItemStack[] items) {
        if (items == null) return null;

        initialize();
        if (nbtCompressedStreamTools_read == null) {
            return basicSerialize(items);
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutput = new DataOutputStream(outputStream);
            Object itemList = nbtTagListConstructor.newInstance();

            // Save every element in the list
            for (ItemStack item : items) {
                Object outputObject = nbtTagCompoundConstructor.newInstance();
                Object craft = getCraftVersion(item);

                // Convert the item stack to a NBT compound
                if (craft != null)
                    itemStack_save.invoke(craftItemStack_getHandle.get(craft), outputObject);
                nbtTagList_add.invoke(itemList, outputObject);
            }

            Object wrapper = nbtTagCompoundConstructor.newInstance();
            nbtTagCompound_set.invoke(wrapper, "i", itemList);

            nbtCompressedStreamTools_write.invoke(null, wrapper, dataOutput);

            // Serialize that array
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static ItemStack[] fromBase64(String data) {
        if (data == null) return null;

        initialize();
        if (data.indexOf(';') >= 0) {
            return basicDeserialize(data);
        }
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            Object wrapper = nbtCompressedStreamTools_read.invoke(null, dataInputStream);
            Object itemList = nbtTagCompound_getList.invoke(wrapper, "i", 10);
            ItemStack[] items = new ItemStack[(Integer)nbtTagList_size.invoke(itemList)];

            for (int i = 0; i < items.length; i++) {
                Object inputObject = nbtTagList_get.invoke(itemList, i);

                // IsEmpty
                if (!(Boolean)nbtTagCompound_isEmpty.invoke(inputObject)) {
                    items[i] = (ItemStack)craftItemNMSConstructor.newInstance(nmsItemConstructor.newInstance(inputObject));
                }
            }

            // Serialize that array
            return items;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Object getCraftVersion(ItemStack stack) throws Exception {
        if (stack == null)
            return null;
        else if (stack.getClass() == ItemStack.class)
            return craftItemConstructor.newInstance(stack);
        else
            return stack;
    }

    private static String basicSerialize(ItemStack[] items)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(items.length);
        builder.append(';');
        for (int i = 0; i < items.length; i++)
        {
            ItemStack is = items[i];
            if (is != null)
            {
                builder.append(i);
                builder.append('#');

                String isType = String.valueOf(is.getType().getId());
                builder.append("t@");
                builder.append(isType);

                if (is.getDurability() != 0)
                {
                    String isDurability = String.valueOf(is.getDurability());
                    builder.append(":d@");
                    builder.append(isDurability);
                }

                if (is.getAmount() != 1)
                {
                    String isAmount = String.valueOf(is.getAmount());
                    builder.append(":a@");
                    builder.append(isAmount);
                }

                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0)
                {
                    for (Map.Entry<Enchantment,Integer> ench : isEnch.entrySet())
                    {
                        builder.append(":e@");
                        builder.append(ENCHANT_IDS.get(ench.getKey().getName()));
                        builder.append('@');
                        builder.append(ench.getValue());
                    }
                }

                ItemMeta meta = is.getItemMeta();
                if (meta.hasDisplayName()) {
                    builder.append(":n@");
                    builder.append(meta.getDisplayName().replaceAll("[:@#;]", ""));
                }

                if (meta.hasLore()) {
                    for (String line : meta.getLore()) {
                        builder.append(":l@");
                        builder.append(line.replaceAll("[:;@#]", ""));
                    }
                }

                builder.append(';');
            }
        }
        return builder.toString();
    }

    private static ItemStack[] basicDeserialize(String invString)
    {
        String[] serializedBlocks = invString.split(";");
        if (serializedBlocks.length == 0)
            return null;
        String invInfo = serializedBlocks[0];
        ItemStack[] deserializedInventory = new ItemStack[Integer.valueOf(invInfo)];

        for (int i = 1; i <= deserializedInventory.length && i < serializedBlocks.length; i++)
        {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);

            if (stackPosition >= deserializedInventory.length)
            {
                continue;
            }

            ItemStack is = null;
            Boolean createdItemStack = false;

            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack)
            {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t"))
                {
                    int id = Integer.valueOf(itemAttribute[1]);
                    if (id >= 2256) id -= 2267 - Material.values().length;
                    final Material mat = Material.values()[id];
                    is = new ItemStack(mat);
                    createdItemStack = true;
                }
                else if (itemAttribute[0].equals("d") && createdItemStack)
                {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("a") && createdItemStack)
                {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("e") && createdItemStack)
                {
                    final String name = ENCHANT_IDS.inverse().getOrDefault(Integer.valueOf(itemAttribute[1]), "OXYGEN");
                    is.addUnsafeEnchantment(Enchantment.getByName(name), Integer.valueOf(itemAttribute[2]));
                }
                else if (itemAttribute[0].equals("n") && createdItemStack)
                {
                    ItemMeta meta = is.getItemMeta();
                    meta.setDisplayName(itemAttribute[1]);
                    is.setItemMeta(meta);
                }
                else if (itemAttribute[0].equals("l") && createdItemStack)
                {
                    ItemMeta meta = is.getItemMeta();
                    List<String> lore = meta.getLore();
                    if (lore == null) lore = new ArrayList<>();
                    lore.add(itemAttribute[1]);
                    meta.setLore(lore);
                    is.setItemMeta(meta);
                }
            }
            deserializedInventory[stackPosition] = is;
        }

        return deserializedInventory;
    }

    private static final BiMap<String, Integer> ENCHANT_IDS = ImmutableBiMap.<String, Integer>builder()
            .put("PROTECTION_ENVIRONMENTAL", 0)
            .put("PROTECTION_FIRE", 1)
            .put("PROTECTION_FALL", 2)
            .put("PROTECTION_EXPLOSIONS", 3)
            .put("PROTECTION_PROJECTILE", 4)
            .put("OXYGEN", 5)
            .put("WATER_WORKER", 6)
            .put("THORNS", 7)
            .put("DEPTH_STRIDER", 8)
            .put("FROST_WALKER", 9)
            .put("BINDING_CURSE", 10)
            .put("DAMAGE_ALL", 16)
            .put("DAMAGE_UNDEAD", 17)
            .put("DAMAGE_ARTHROPODS", 18)
            .put("KNOCKBACK", 19)
            .put("FIRE_ASPECT", 20)
            .put("LOOT_BONUS_MOBS", 21)
            .put("SWEEPING_EDGE", 22)
            .put("DIG_SPEED", 32)
            .put("SILK_TOUCH", 33)
            .put("DURABILITY", 34)
            .put("LOOT_BONUS_BLOCKS", 35)
            .put("ARROW_DAMAGE", 48)
            .put("ARROW_KNOCKBACK", 49)
            .put("ARROW_FIRE", 50)
            .put("ARROW_INFINITE", 51)
            .put("LUCK", 61)
            .put("LURE", 62)
            .put("MENDING", 70)
            .put("VANISHING_CURSE", 71)
            .put("LOYALTY", 80)
            .put("IMPALING", 81)
            .put("RIPTIDE", 82)
            .put("CHANNELING", 83)
            .build();
}
