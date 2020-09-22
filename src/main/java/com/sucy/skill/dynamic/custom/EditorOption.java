package com.sucy.skill.dynamic.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.custom.EditorOption
 */
public class EditorOption {
    public final Type type;
    public final String key;
    public final String name;
    public final String description;
    public final Map<String, String> extra = new HashMap<>();

    public static EditorOption number(final String key, final String name, final String description, final double base, final double scale) {
        final EditorOption option = new EditorOption(Type.NUMBER, key, name, description);
        option.extra.put("base", Double.toString(base));
        option.extra.put("scale", Double.toString(scale));
        return option;
    }

    public static EditorOption text(final String key, final String name, final String description, final String initial) {
        final EditorOption option = new EditorOption(Type.TEXT, key, name, description);
        option.extra.put("default", "\"" + initial + "\"");
        return option;
    }

    public static EditorOption dropdown(final String key, final String name, final String description, final List<String> options) {
        final EditorOption option = new EditorOption(Type.DROPDOWN, key, name, description);
        option.extra.put("options", format(options));
        return option;
    }

    public static EditorOption list(final String key, final String name, final String description, final List<String> options) {
        final EditorOption option = new EditorOption(Type.LIST, key, name, description);
        option.extra.put("options", format(options));
        return option;
    }

    private static String format(final List<String> list) {
        return "[\"" + list.stream().collect(Collectors.joining("\",\"")) + "\"]";
    }

    private EditorOption(final Type type, final String key, final String name, final String description) {
        this.type = type;
        this.key = key;
        this.name = name;
        this.description = description;
    }

    private enum Type {
        NUMBER,
        TEXT,
        DROPDOWN,
        LIST
    }
}
