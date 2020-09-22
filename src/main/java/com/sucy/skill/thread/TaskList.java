/**
 * SkillAPI
 * com.sucy.skill.thread.TaskList
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

import java.util.Iterator;

public class TaskList implements Iterable<IThreadTask>, Iterator<IThreadTask>
{
    private Entry iteratee;
    private Entry head;
    private Entry tail;
    private int   size;

    public TaskList()
    {
        head = tail = new Entry();
    }

    public int size()
    {
        return size;
    }

    public void add(IThreadTask task)
    {
        tail.next = new Entry(task);
        tail.next.prev = tail;
        tail = tail.next;
        size++;
    }

    public void clear()
    {
        head.next = null;
        tail = head;
        size = 0;
    }

    @Override
    public Iterator<IThreadTask> iterator()
    {
        iteratee = head;
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return iteratee.next != null;
    }

    @Override
    public IThreadTask next()
    {
        if (iteratee == null || iteratee.next == null) {
            return null;
        }
        iteratee = iteratee.next;
        return iteratee.task;
    }

    @Override
    public void remove()
    {
        if (iteratee != null && iteratee.prev != null)
        {
            if (iteratee == tail)
                tail = iteratee.prev;
            iteratee.prev.next = iteratee.next;
            size--;
        }
    }

    private static class Entry
    {
        public Entry() { }

        public Entry(IThreadTask task)
        {
            this.task = task;
        }

        private IThreadTask task;
        private Entry       prev;
        private Entry       next;
    }
}
