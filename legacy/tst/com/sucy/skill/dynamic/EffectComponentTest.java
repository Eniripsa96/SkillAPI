/**
 * SkillAPI
 * com.sucy.skill.dynamic.EffectComponentTest
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
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
package com.sucy.skill.dynamic;

import com.google.common.collect.ImmutableSet;
import com.sucy.skill.util.FileReader;
import org.bukkit.entity.LivingEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EffectComponentTest {
    @Spy
    EffectComponent effectComponent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFilter() {

        LivingEntity caster = mock(LivingEntity.class);
        when(caster.getName()).thenReturn("Dude");

        DynamicSkill.getCastData(caster).put("yup", 12);

        String result = effectComponent.filter(caster, caster, "Hey {nope} {yup} {player} {yup} there");
        Assert.assertEquals(result, "Hey {nope} 12 Dude 12 there");
    }

    @Test
    public void testTriggerKeys() throws Exception {
        final String[] lines = FileReader.readText("editor/js/component.js").split("\r?\n");
        final Set<String> keys = new HashSet<>(parseKeys("Trigger", lines));
        assertTrue(keys.remove("Cast"));
        assertTrue(keys.remove("Initialize"));
        assertTrue(keys.remove("Cleanup"));
        keys.forEach(key -> assertNotNull(key + " isn't registered properly", ComponentRegistry.getTrigger(key)));
    }

    @Test
    public void testComponentKeys() throws Exception {
        final Map<ComponentType, Set<String>> map = parseKeys();
        map.forEach((type, keys) -> keys.forEach(key -> ComponentRegistry.getComponent(type, key)));
    }

    private Map<ComponentType, Set<String>> parseKeys() throws Exception {
        final String[] lines = FileReader.readText("editor/js/component.js").split("\r?\n");
        final EnumMap<ComponentType, Set<String>> map = new EnumMap<>(ComponentType.class);
        map.put(ComponentType.TARGET, parseKeys("Target", lines));
        map.put(ComponentType.MECHANIC, parseKeys("Mechanic", lines));
        map.put(ComponentType.CONDITION, parseKeys("Condition", lines));
        return map;
    }

    private Set<String> parseKeys(final String category, final String[] lines) {
        int i = 0;
        final String text = "var " + category + " = {";
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        while (!lines[i].equals(text)) { i++; }
        while (!lines[++i].equals("};")) {
            final String line = lines[i];
            int start = line.indexOf('\'');
            int end = line.indexOf('\'', start + 1);
            builder.add(line.substring(start + 1, end));
        }
        return builder.build();
    }
}
