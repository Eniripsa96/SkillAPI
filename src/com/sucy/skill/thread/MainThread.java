/**
 * SkillAPI
 * com.sucy.skill.thread.MainThread
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
 * The main async task for SkillAPI functions
 */
public class MainThread extends Thread
{
    private static final TaskList tasks = new TaskList();

    private long time;

    private boolean enabled;

    /**
     * Sets up the main thread
     */
    public MainThread()
    {
        time = System.currentTimeMillis();
        enabled = true;
        start();
    }

    /**
     * Runs the thread until disabled or interrupted
     */
    @Override
    public void run()
    {
        try
        {
            while (enabled)
            {
                tasks.iterator();
                while (tasks.hasNext())
                    if (tasks.next().tick())
                        tasks.remove();

                long current = System.currentTimeMillis();
                time = Math.max(time + 50, current + 1);
                sleep(time - current);
            }
        }
        catch (Exception ex)
        {
            // Thread ended
        }
    }

    /**
     * Disables the main thread, stopping future runs
     */
    public void disable()
    {
        for (IThreadTask task : tasks)
            task.run();
        tasks.clear();
        enabled = false;
    }

    /**
     * Registers a new task to run
     *
     * @param task task to run
     */
    public static void register(IThreadTask task)
    {
        tasks.add(task);
    }
}
