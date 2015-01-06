package com.sucy.skill.api.player;

import com.rit.sucy.config.FilterType;
import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.*;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;
import com.sucy.skill.data.GroupSettings;
import com.sucy.skill.language.ErrorNodes;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public final class PlayerData
{
    private final HashMap<String, PlayerClass>   classes = new HashMap<String, PlayerClass>();
    private final HashMap<String, PlayerSkill>   skills  = new HashMap<String, PlayerSkill>();
    private final HashMap<Material, PlayerSkill> binds   = new HashMap<Material, PlayerSkill>();

    private OfflinePlayer player;
    private double        mana;
    private double        maxMana;

    public PlayerData(OfflinePlayer player)
    {
        this.player = player;
        for (String group : SkillAPI.getGroups())
        {
            GroupSettings settings = SkillAPI.getSettings().getGroupSettings(group);
            RPGClass rpgClass = settings.getDefault();

            if (rpgClass != null && settings.getPermission() == null)
            {
                setClass(rpgClass);
            }
        }
    }

    public Player getPlayer()
    {
        return player.getPlayer();
    }

    public String getPlayerName()
    {
        return player.getName();
    }

    public UUID getUUID()
    {
        return player.getUniqueId();
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                      Skills                       //
    //                                                   //
    ///////////////////////////////////////////////////////

    public boolean hasSkill(String name)
    {
        if (name == null) return false;
        return skills.containsKey(name.toLowerCase());
    }

    public PlayerSkill getSkill(String name)
    {
        if (name == null)
        {
            return null;
        }
        return skills.get(name.toLowerCase());
    }

    public Collection<PlayerSkill> getSkills()
    {
        return skills.values();
    }

    public int getSkillLevel(String name)
    {
        PlayerSkill skill = getSkill(name);
        return skill == null ? 0 : skill.getLevel();
    }

    public void giveSkill(Skill skill)
    {
        giveSkill(skill, null);
    }

    public void giveSkill(Skill skill, PlayerClass parent)
    {
        String key = skill.getName().toLowerCase();
        if (!skills.containsKey(key))
        {
            skills.put(key, new PlayerSkill(this, skill, parent));
        }
    }

    public boolean upgradeSkill(Skill skill)
    {
        // Cannot be null
        if (skill == null)
        {
            return false;
        }

        // Must be a valid available skill
        PlayerSkill data = skills.get(skill.getName().toLowerCase());
        if (data == null)
        {
            return false;
        }

        // Must meet any skill requirements
        if (skill.getSkillReq() != null)
        {
            PlayerSkill req = skills.get(skill.getSkillReq().toLowerCase());
            if (req == null || req.getLevel() < skill.getSkillReqLevel())
            {
                return false;
            }
        }

        int level = data.getPlayerClass().getLevel();
        int cost = skill.getCost(data.getLevel());
        if (!data.isMaxed() && level >= skill.getLevelReq(data.getLevel()) && data.getPlayerClass().getPoints() >= cost)
        {
            // Upgrade event
            PlayerSkillUpgradeEvent event = new PlayerSkillUpgradeEvent(this, data, cost);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
            {
                return false;
            }

            // Apply upgrade
            data.getPlayerClass().usePoints(cost);
            data.addLevels(1);

            // Passive calls
            Player player = getPlayer();
            if (player != null && skill instanceof PassiveSkill)
            {
                if (data.getLevel() == 1)
                {
                    ((PassiveSkill) skill).initialize(player, data.getLevel());
                }
                else
                {
                    ((PassiveSkill) skill).update(player, data.getLevel() - 1, data.getLevel());
                }
            }

            // Unlock event
            if (data.getLevel() == 1)
            {
                Bukkit.getPluginManager().callEvent(new PlayerSkillUnlockEvent(this, data));
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean downgradeSkill(Skill skill)
    {
        // Cannot be null
        if (skill == null)
        {
            return false;
        }

        // Must be a valid available skill
        PlayerSkill data = skills.get(skill.getName().toLowerCase());
        if (data == null)
        {
            return false;
        }

        // Must not be required by another skill
        for (PlayerSkill s : skills.values())
        {
            if (s.getData().getSkillReq().equalsIgnoreCase(skill.getName()) && data.getLevel() <= s.getData().getSkillReqLevel())
            {
                return false;
            }
        }

        int cost = skill.getCost(data.getLevel() - 1);
        if (data.getLevel() > 0)
        {
            // Upgrade event
            PlayerSkillDowngradeEvent event = new PlayerSkillDowngradeEvent(this, data, cost);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
            {
                return false;
            }

            // Apply upgrade
            data.getPlayerClass().givePoints(cost, PointSource.REFUND);
            data.addLevels(-1);

            // Passive calls
            Player player = getPlayer();
            if (player != null && skill instanceof PassiveSkill)
            {
                if (data.getLevel() == 0)
                {
                    ((PassiveSkill) skill).stopEffects(player, 1);
                }
                else
                {
                    ((PassiveSkill) skill).update(player, data.getLevel() + 1, data.getLevel());
                }
            }

            // Clear bindings
            if (data.getLevel() == 0)
            {
                clearBinds(skill);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public void showSkills()
    {
        showSkills(getPlayer());
    }

    public boolean showSkills(Player player)
    {
        // Cannot show an invalid player, and cannot show no skills
        if (player == null || classes.size() == 0 || skills.size() == 0)
        {
            return false;
        }

        // Show skill tree of only class
        if (classes.size() == 1)
        {
            PlayerClass playerClass = classes.get(classes.keySet().toArray(new String[1])[0]);
            if (playerClass.getData().getSkills().size() == 0)
            {
                return false;
            }

            player.openInventory(playerClass.getData().getSkillTree().getInventory(this));
            return true;
        }

        // Show list of classes that have skill trees
        else
        {
            return true;
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Classes                       //
    //                                                   //
    ///////////////////////////////////////////////////////

    public Collection<PlayerClass> getClasses()
    {
        return classes.values();
    }

    public PlayerClass getClass(String group)
    {
        return classes.get(group);
    }

    public void setClass(RPGClass rpgClass)
    {

        PlayerClass c = classes.get(rpgClass.getGroup());
        if (c != null)
        {
            for (Skill skill : c.getData().getSkills())
            {
                skills.remove(skill.getName().toLowerCase());
            }
        }

        classes.put(rpgClass.getGroup(), new PlayerClass(this, rpgClass));
    }

    public boolean isExactClass(RPGClass rpgClass)
    {
        return rpgClass != null && classes.get(rpgClass.getGroup()).getData() == rpgClass;
    }

    public boolean isClass(RPGClass rpgClass)
    {
        if (rpgClass == null)
        {
            return false;
        }

        RPGClass temp = classes.get(rpgClass.getGroup()).getData();
        while (temp != null)
        {
            if (temp == rpgClass)
            {
                return true;
            }
            temp = temp.getParent();
        }

        return false;
    }

    public boolean canProfess(RPGClass rpgClass)
    {
        if (classes.containsKey(rpgClass.getGroup()))
        {
            PlayerClass current = classes.get(rpgClass.getGroup());
            return rpgClass.getParent() == current.getData() && current.getData().getMaxLevel() <= current.getLevel();
        }
        else
        {
            return !rpgClass.hasParent();
        }
    }

    public boolean profess(RPGClass rpgClass)
    {
        if (rpgClass != null && canProfess(rpgClass))
        {
            PlayerClass current = classes.get(rpgClass.getGroup());
            if (current == null)
            {
                current = new PlayerClass(this, rpgClass);
                classes.put(rpgClass.getGroup(), current);
            }
            else
            {
                boolean reset = SkillAPI.getSettings().getGroupSettings(rpgClass.getGroup()).isProfessReset();
                if (reset)
                {
                    for (Skill skill : current.getData().getSkills())
                    {
                        skills.remove(skill.getName());
                    }
                }
                current.setClassData(rpgClass, reset);
            }
            for (Skill skill : rpgClass.getSkills())
            {
                if (!skills.containsKey(skill.getKey()))
                {
                    skills.put(skill.getKey(), new PlayerSkill(this, skill, current));
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Gives experience to the player from the given source
     *
     * @param amount amount of experience to give
     * @param source source of the experience
     */
    public void giveExp(double amount, ExpSource source)
    {
        for (PlayerClass playerClass : classes.values())
        {
            playerClass.giveExp(amount, source);
        }
    }

    /**
     * Gives levels to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    public void giveLevels(int amount, ExpSource source)
    {
        for (PlayerClass playerClass : classes.values())
        {
            if (playerClass.getData().receivesExp(source))
            {
                playerClass.giveLevels(amount);
            }
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                       Mana                        //
    //                                                   //
    ///////////////////////////////////////////////////////

    public double getMana()
    {
        return mana;
    }

    public boolean hasMana(double amount)
    {
        return mana >= amount;
    }

    public double getMaxMana()
    {
        return maxMana;
    }

    public void regenMana()
    {
        double amount = 0;
        for (PlayerClass c : classes.values())
        {
            if (c.getData().hasManaRegen())
            {
                amount += c.getData().getManaRegen();
            }
        }
        if (amount > 0)
        {
            giveMana(amount, ManaSource.REGEN);
        }
    }

    public void giveMana(double amount)
    {
        giveMana(amount, ManaSource.SPECIAL);
    }

    public void giveMana(double amount, ManaSource source)
    {
        PlayerManaGainEvent event = new PlayerManaGainEvent(this, amount, source);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled())
        {
            mana += event.getAmount();
            if (mana > maxMana)
            {
                mana = maxMana;
            }
        }
    }

    public void useMana(double amount)
    {
        useMana(amount, ManaCost.SPECIAL);
    }

    public void useMana(double amount, ManaCost cost)
    {
        PlayerManaLossEvent event = new PlayerManaLossEvent(this, amount, cost);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled())
        {
            mana -= event.getAmount();
            if (mana < 0)
            {
                mana = 0;
            }
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Skill Binding                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    public PlayerSkill getBoundSkill(Material mat)
    {
        return binds.get(mat);
    }

    public HashMap<Material, PlayerSkill> getBinds()
    {
        return binds;
    }

    public boolean isBound(Material mat)
    {
        return binds.containsKey(mat);
    }

    public boolean bind(Material mat, PlayerSkill skill)
    {
        // Make sure the skill is owned by the player
        if (skill != null && skill.getPlayerData() != this)
        {
            throw new IllegalArgumentException("That skill does not belong to this player!");
        }

        PlayerSkill bound = getBoundSkill(mat);
        if (bound != skill)
        {
            // Apply the binding
            if (skill == null)
            {
                binds.remove(mat);
            }
            else
            {
                binds.put(mat, skill);
            }

            // Update the old skill's bind
            if (bound != null)
            {
                bound.setBind(null);
            }

            // Update the new skill's bind
            if (skill != null)
            {
                skill.setBind(mat);
            }

            return true;
        }

        // The skill was already bound
        else
        {
            return false;
        }
    }

    public boolean clearBind(Material mat)
    {
        return binds.remove(mat) != null;
    }

    public void clearBinds(Skill skill)
    {
        for (Material key : binds.keySet())
        {
            PlayerSkill bound = binds.get(key);
            if (bound.getData() == skill)
            {
                binds.remove(key);
            }
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Functions                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    public boolean cast(String skillName)
    {
        return cast(skills.get(skillName.toLowerCase()));
    }

    public boolean cast(PlayerSkill skill)
    {
        // Invalid skill
        if (skill == null)
        {
            throw new IllegalArgumentException("Skill cannot be null");
        }

        SkillStatus status = skill.getStatus();
        int level = skill.getLevel();
        double cost = skill.getData().getManaCost(level);

        // Not unlocked
        if (level <= 0)
        {
            return false;
        }

        // On Cooldown
        if (status == SkillStatus.ON_COOLDOWN)
        {
            SkillAPI.getLanguage().sendMessage(
                    ErrorNodes.COOLDOWN,
                    getPlayer(),
                    FilterType.COLOR,
                    RPGFilter.COOLDOWN.setReplacement(skill.getCooldown() + ""),
                    RPGFilter.SKILL.setReplacement(skill.getData().getName())
            );
        }

        // Not enough mana
        else if (status == SkillStatus.MISSING_MANA)
        {
            SkillAPI.getLanguage().sendMessage(
                    ErrorNodes.MANA,
                    getPlayer(),
                    FilterType.COLOR,
                    RPGFilter.SKILL.setReplacement(skill.getData().getName()),
                    RPGFilter.MANA.setReplacement(getMana() + ""),
                    RPGFilter.COST.setReplacement((int) Math.ceil(cost) + ""),
                    RPGFilter.MISSING.setReplacement((int) Math.ceil(cost - getMana()) + "")
            );
        }

        // Skill Shots
        else if (skill.getData() instanceof SkillShot)
        {

            Player p = getPlayer();
            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill, p);
            Bukkit.getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled())
            {
                try
                {
                    if (((SkillShot) skill.getData()).cast(p, level))
                    {
                        skill.getData().sendMessage(p, SkillAPI.getSettings().getMessageRadius());
                        skill.startCooldown();
                        if (SkillAPI.getSettings().isManaEnabled())
                        {
                            useMana(cost, ManaCost.SKILL_CAST);
                        }
                        return true;
                    }
                }
                catch (Exception ex)
                {
                    Bukkit.getLogger().severe("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
                    ex.printStackTrace();
                }
            }
        }

        // Target Skills
        else if (skill.getData() instanceof TargetSkill)
        {

            Player p = getPlayer();
            LivingEntity target = TargetHelper.getLivingTarget(p, skill.getData().getRange(level));

            // Must have a target
            if (target == null)
            {
                return false;
            }

            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill, p);
            Bukkit.getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled())
            {
                try
                {
                    if (((TargetSkill) skill.getData()).cast(p, target, level, Protection.isAlly(p, target)))
                    {
                        skill.getData().sendMessage(p, SkillAPI.getSettings().getMessageRadius());
                        skill.startCooldown();
                        if (SkillAPI.getSettings().isManaEnabled())
                        {
                            useMana(cost, ManaCost.SKILL_CAST);
                        }
                        return true;
                    }
                }
                catch (Exception ex)
                {
                    Bukkit.getLogger().severe("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
                    ex.printStackTrace();
                }
            }
        }

        return false;
    }
}
