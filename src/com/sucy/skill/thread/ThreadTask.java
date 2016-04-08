/**
 * SkillAPI
 * com.sucy.skill.thread.ThreadTask
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
 * A task that runs ones and is finished
 */
public abstract class ThreadTask implements IThreadTask
{
    private int time;

    /**
     * Sets up the task to run on the next thread iteration
     */
    public ThreadTask()
    {
        this(0);
    }

    /**
     * Sets up the task to run after a delay
     *
     * @param delay delay in ticks
     */
    public ThreadTask(int delay)
    {
        time = delay;
    }

    /**
     * Ticks the task, running it if applicable
     *
     * @return true after the task runs, false beforehand
     */
    @Override
    public boolean tick()
    {
        if (--time <= 0)
        {
            run();
            return true;
        }
        return false;
    }
}
