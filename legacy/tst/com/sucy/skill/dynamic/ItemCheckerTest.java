package com.sucy.skill.dynamic;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.ItemCheckerTest
 */
public class ItemCheckerTest {

    @Test
    public void testLore() {
        final String pattern = "Test prefix ([+-]?[0-9]+([.,][0-9]+)?) and suffix";
        final Pattern compiled = Pattern.compile(pattern);
        final Matcher matcher = compiled.matcher("Test prefix -123,456 and suffix");
        Assert.assertEquals(matcher.find(), true);
        System.out.println(matcher.group(1));
    }
}