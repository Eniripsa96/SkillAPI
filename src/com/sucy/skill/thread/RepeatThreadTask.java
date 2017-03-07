/**
 * SkillAPI
 * com.sucy.skill.thread.RepeatThreadTask
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
package com.sucy.skill.thread;

/**
 * A thread task that continually runs in the background
 */
public abstract class RepeatThreadTask implements IThreadTask
{
    private int interval;
    private int time;

    protected boolean expired;

    /**
     * Sets up the task with an initial delay and an interval
     *
     * @param delay    delay before first run
     * @param interval delay between subsequent runs
     */
    public RepeatThreadTask(int delay, int interval)
    {
        this.interval = Math.max(interval, 1);
        this.time = -delay;
        expired = false;
    }

    /**
     * Ticks the task, running periodically depending on the interval
     *
     * @return true if expired
     */
    @Override
    public boolean tick()
    {
        if (++time % interval == 0 && time > 0)
        {
            run();
            time = 0;
        }
        return expired;
    }
}
