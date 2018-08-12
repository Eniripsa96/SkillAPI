package com.sucy.skill.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SkillAPI © 2018
 * com.sucy.skill.util.Lists
 */
public class Lists {
    public static <T> List<T> asList(final T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
