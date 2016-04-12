/**
 * SkillAPI
 * com.sucy.skill.api.util.DamageLoreRemover
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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

import com.rit.sucy.reflect.Reflection;
import com.sucy.skill.log.Logger;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * <p>Utility class for removing vanilla damage lore lines from items.</p>
 */
public class DamageLoreRemover
{
    private static Class<?>
        NBT_BASE,
        NBT_COMPOUND,
        NBT_LIST,
        NMS_ITEM,
        CRAFT_ITEM;

    private static Method
        SET,
        SET_TAG,
        SET_BOOL,
        SET_INT,
        GET_TAG,
        AS_CRAFT,
        AS_NMS;

    /**
     * <p>Sets up reflection methods/classes ahead of time so that they don't need to constantly be fetched.</p>
     */
    private static void setup()
    {

        try
        {
            NBT_BASE = Reflection.getNMSClass("NBTBase");
            NBT_COMPOUND = Reflection.getNMSClass("NBTTagCompound");
            NBT_LIST = Reflection.getNMSClass("NBTTagList");
            NMS_ITEM = Reflection.getNMSClass("ItemStack");
            CRAFT_ITEM = Reflection.getCraftClass("inventory.CraftItemStack");

            AS_NMS = CRAFT_ITEM.getMethod("asNMSCopy", ItemStack.class);
            GET_TAG = NMS_ITEM.getMethod("getTag");
            SET = NBT_COMPOUND.getMethod("set", String.class, NBT_BASE);
            SET_TAG = NMS_ITEM.getMethod("setTag", NBT_COMPOUND);
            SET_BOOL = NBT_COMPOUND.getMethod("setBoolean", String.class, boolean.class);
            SET_INT = NBT_COMPOUND.getMethod("setInt", String.class, int.class);
            AS_CRAFT = CRAFT_ITEM.getMethod("asCraftMirror", NMS_ITEM);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to set up reflection for removing damage lores.");
        }
    }

    /**
     * <p>Removes the vanilla damage lore from tools.</p>
     * <p>If you pass in something other than a tool this will do nothing.</p>
     * <p>If there was some problem with setting up the reflection classes, this will
     * also do nothing.</p>
     *
     * @param item tool to remove the lore from
     *
     * @return the tool without the damage lore
     */
    public static ItemStack removeAttackDmg(ItemStack item)
    {
        if (item == null)
        {
            return item;
        }
        if (NBT_BASE == null) setup();
        try
        {
            item = item.clone();
            Object nmsStack = AS_NMS.invoke(null, item);
            Object nbtCompound = GET_TAG.invoke(nmsStack);

            // Disable durability if needed
            if (item.getType().getMaxDurability() > 0
                && item.getDurability() > 0)
            {
                SET_BOOL.invoke(nbtCompound, "Unbreakable", true);
                SET_INT.invoke(nbtCompound, "HideFlags", 4);
            }

            // Remove default NBT displays
            Object nbtTagList = Reflection.getInstance(NBT_LIST);
            SET.invoke(nbtCompound, "AttributeModifiers", nbtTagList);

            // Apply to item
            SET_TAG.invoke(nmsStack, nbtCompound);

            // Return result
            return (ItemStack) AS_CRAFT.invoke(null, nmsStack);
        }
        catch (Exception ex)
        {
            return item;
        }
    }
}
