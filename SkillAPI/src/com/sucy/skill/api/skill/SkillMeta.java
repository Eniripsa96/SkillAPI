package com.sucy.skill.api.skill;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import java.util.Hashtable;

/**
 * <p>Metadata to attach to metadatable objects in case extra effects are needed</p>
 * <p>Metadatable objects can be found here in the inheritance diagram:</p>
 * <p>http://jd.bukkit.org/rb/doxygen/d5/d6f/interfaceorg_1_1bukkit_1_1metadata_1_1Metadatable.html</p>
 */
public class SkillMeta implements MetadataValue {

    private static final String META_NAME = "skillMeta";

    private Hashtable<String, Double> attributes = new Hashtable<String, Double>();
    private Player caster;
    private Plugin plugin;
    private ClassSkill skill;

    /**
     * Constructor
     *
     * @param caster caster of the skill
     */
    public SkillMeta(Player caster, ClassSkill skill) {
        this(Bukkit.getPluginManager().getPlugin("SkillAPI"), caster, skill);
    }

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param caster caster of the skill
     */
    public SkillMeta(Plugin plugin, Player caster, ClassSkill skill) {
        this.caster = caster;
        this.plugin = plugin;
        this.skill = skill;
    }

    /**
     * Attaches this metadata to the target
     *
     * @param target target to attach to
     */
    public void attach(Metadatable target) {
        target.setMetadata(META_NAME + skill.getName(), this);
    }

    /**
     * Attaches the metadata to the target using a custom key
     *
     * @param target target to attach to
     * @param key    custom key
     */
    public void attach(Metadatable target, String key) {
        target.setMetadata(META_NAME + key, this);
    }

    /**
     * @return caster of the skill
     */
    public Player getCaster() {
        return caster;
    }

    /**
     * @return skill that added the metadata
     */
    public ClassSkill getSkill() {
        return skill;
    }

    /**
     * Sets an attribute for the metadata
     *
     * @param name  attribute name
     * @param value attribute value
     */
    public void setAttribute(String name, double value) {
        attributes.put(name, value);
    }

    /**
     * Retrieves an attribute value
     *
     * @param name attribute name
     * @return     attribute value or null if not set
     */
    public double getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Checks whether or not the attribute is set
     *
     * @param name name of the attribute
     * @return     true if set, false otherwise
     */
    public boolean hasAttribute(String name) {
        return attributes.contains(name);
    }

    /**
     * @return caster
     */
    @Override
    public Object value() {
        return caster;
    }

    /**
     * @return caster hash code
     */
    @Override
    public int asInt() {
        return caster.hashCode();
    }

    /**
     * @return caster hash code
     */
    @Override
    public float asFloat() {
        return caster.hashCode();
    }

    /**
     * @return caster hash code
     */
    @Override
    public double asDouble() {
        return caster.hashCode();
    }

    /**
     * @return caster hash code
     */
    @Override
    public long asLong() {
        return caster.hashCode();
    }

    /**
     * @return 0
     */
    @Override
    public short asShort() {
        return 0;
    }

    /**
     * @return 0
     */
    @Override
    public byte asByte() {
        return 0;
    }

    /**
     * @return true if caster isn't null
     */
    @Override
    public boolean asBoolean() {
        return caster != null;
    }

    /**
     * @return caster name
     */
    @Override
    public String asString() {
        return caster.getName();
    }

    /**
     * @return plugin or null if not set
     */
    @Override
    public Plugin getOwningPlugin() {
        return plugin;
    }

    /**
     * Does nothing
     */
    @Override
    public void invalidate() {}

    /**
     * Adds the metadata to the target
     *
     * @param caster caster of the skill
     * @param target target to attach to
     * @param skill  skill to attach for
     * @return       SkillMeta that was added
     */
    public static SkillMeta addMeta(Player caster, Metadatable target, ClassSkill skill) {
        SkillMeta meta = new SkillMeta(caster, skill);
        meta.attach(target);
        return meta;
    }

    /**
     * Adds the metadata to the target with a custom key
     *
     * @param caster caster of the skill
     * @param target target to attach to
     * @param skill  skill to attach for
     * @param key    custom key to use
     * @return       SkillMeta that was added
     */
    public static SkillMeta addMeta(Player caster, Metadatable target, ClassSkill skill, String key) {
        SkillMeta meta = new SkillMeta(caster, skill);
        meta.attach(target, key);
        return meta;
    }

    /**
     * Checks if the target has skill meta attached to it
     *
     * @param target target to check
     * @return       true if skill meta is present, false otherwise
     */
    public static boolean hasMeta(Metadatable target, ClassSkill skill) {
        return target != null && target.hasMetadata(META_NAME + skill.getName());
    }

    /**
     * Checks if the target has skill meta attached to it
     *
     * @param target target to check
     * @param key    custom key
     * @return       true if skill meta is present, false otherwise
     */
    public static boolean hasMeta(Metadatable target, String key) {
        return target != null && target.hasMetadata(META_NAME + key);
    }

    /**
     * <p>Retrieves the skill meta from the target</p>
     * <p>If you provided a custom key when adding the metadata, use getMeta(Metadatable, String) instead.</p>
     *
     * @param target target to retrieve meta from
     * @param skill  skill used to attach the metadata
     * @return       meta attached or null if none
     */
    public static SkillMeta getMeta(Metadatable target, ClassSkill skill) {
        if (hasMeta(target, skill)) return (SkillMeta)target.getMetadata(META_NAME + skill.getName()).get(0);
        else return null;
    }

    /**
     * <p>Retrieves the skill meta from the target</p>
     * <p>If you did not use a custom key when adding the metadata, use getMeta(Metadatable, ClassSkill) instead.</p>
     *
     * @param target target to retrieve meta from
     * @param key    custom key
     * @return       meta attached or null if none
     */
    public static SkillMeta getMeta(Metadatable target, String key) {
        if (hasMeta(target, key)) return (SkillMeta)target.getMetadata(META_NAME + key);
        else return null;
    }
}
