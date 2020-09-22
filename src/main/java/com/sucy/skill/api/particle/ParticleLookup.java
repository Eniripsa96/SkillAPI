/**
 * SkillAPI
 * com.sucy.skill.api.particle.ParticleLookup
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
package com.sucy.skill.api.particle;

import java.util.HashMap;

public class ParticleLookup
{
    private static final HashMap<String, ParticleType> BY_EDITOR = new HashMap<String, ParticleType>();
    private static final HashMap<String, ParticleType> BY_OLD    = new HashMap<String, ParticleType>();

    public static ParticleType find(String key)
    {
        key = key.toLowerCase();
        ParticleType type = BY_EDITOR.get(key);
        if (type == null)
            type = getByName(key);
        if (type == null)
            type = BY_OLD.get(key);
        return type;
    }

    public static ParticleType getByEditor(String editorKey)
    {
        return BY_EDITOR.get(editorKey.toLowerCase());
    }

    public static ParticleType getByOld(String oldName)
    {
        return BY_OLD.get(oldName.toLowerCase());
    }

    public static ParticleType getByName(String name)
    {
        return ParticleType.valueOf(name.toUpperCase().replace(" ", "_"));
    }

    public static void register(ParticleType type)
    {
        BY_EDITOR.put(type.editorKey(), type);
        if (type.oldName() != null)
            BY_OLD.put(type.oldName().toLowerCase(), type);
    }
}
