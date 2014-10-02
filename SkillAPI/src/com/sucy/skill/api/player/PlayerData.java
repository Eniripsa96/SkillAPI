package com.sucy.skill.api.player;

import com.rit.sucy.config.FilterType;
import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.enums.SkillStatus;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.event.PlayerManaGainEvent;
import com.sucy.skill.api.event.PlayerManaLossEvent;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;

public final class PlayerData
{

    private final HashMap<String, PlayerClass>   classes = new HashMap<String, PlayerClass>();
    private final HashMap<String, PlayerSkill>   skills  = new HashMap<String, PlayerSkill>();
    private final HashMap<Material, PlayerSkill> binds   = new HashMap<Material, PlayerSkill>();

    private VersionPlayer player;
    private SkillAPI      api;
    private double        mana;
    private double        maxMana;

    public PlayerData(SkillAPI api, VersionPlayer player)
    {
        if (api.getPlayerData(player) != null)
        {
            throw new IllegalArgumentException("Tried to create duplicate data for a player");
        }
        this.api = api;
        this.player = player;
    }

    public SkillAPI getAPI()
    {
        return api;
    }

    public Player getPlayer()
    {
        return player.getPlayer();
    }

    public VersionPlayer getVersionPlayer()
    {
        return player;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                      Skills                       //
    //                                                   //
    ///////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Classes                       //
    //                                                   //
    ///////////////////////////////////////////////////////

    public Collection<PlayerClass> getClasses()
    {
        return classes.values();
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
            return rpgClass.getParent() == current.getData() && current.getData().getProfessLevel() <= current.getLevel();
        }
        else
        {
            return !rpgClass.hasParent();
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
        getAPI().getServer().getPluginManager().callEvent(event);

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
        getAPI().getServer().getPluginManager().callEvent(event);

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

    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Functions                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    public boolean cast(String skillName)
    {
        PlayerSkill skill = skills.get(skillName.toLowerCase());

        // Invalid skill
        if (skill == null)
        {
            throw new IllegalArgumentException("Player does not have the skill: " + skillName);
        }

        SkillStatus stats = skill.getStatus();
        int level = skill.getLevel();
        double cost = skill.getData().getManaCost(level);

        // Not unlocked
        if (level <= 0)
        {
            return false;
        }

        // On Cooldown
        if (stats == SkillStatus.ON_COOLDOWN)
        {
            getAPI().getLanguage().sendMessage(
                    OtherNodes.ON_COOLDOWN,
                    getPlayer(),
                    FilterType.COLOR,
                    RPGFilter.COOLDOWN.setReplacement(skill.getCooldown() + ""),
                    RPGFilter.SKILL.setReplacement(skill.getData().getName())
            );
        }

        // Not enough mana
        else if (stats == SkillStatus.MISSING_MANA)
        {
            getAPI().getLanguage().sendMessage(
                    OtherNodes.NO_MANA,
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
            getAPI().getServer().getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled())
            {
                try
                {
                    if (((SkillShot) skill.getData()).cast(p, level))
                    {
                        skill.getData().sendMessage(getAPI().getLanguage(), p, getAPI().getSettings().getMessageRadius());
                        skill.startCooldown();
                        if (getAPI().getSettings().isManaEnabled())
                        {
                            useMana(cost, ManaCost.SKILL_CAST);
                        }
                        return true;
                    }
                }
                catch (Exception ex)
                {
                    getAPI().getLogger().severe("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
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
            getAPI().getServer().getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled())
            {
                try
                {
                    if (((TargetSkill) skill.getData()).cast(p, target, level, Protection.isAlly(p, target)))
                    {
                        skill.getData().sendMessage(getAPI().getLanguage(), p, getAPI().getSettings().getMessageRadius());
                        skill.startCooldown();
                        if (getAPI().getSettings().isManaEnabled())
                        {
                            useMana(cost, ManaCost.SKILL_CAST);
                        }
                        return true;
                    }
                }
                catch (Exception ex)
                {
                    getAPI().getLogger().severe("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
                    ex.printStackTrace();
                }
            }
        }

        return false;
    }
}
