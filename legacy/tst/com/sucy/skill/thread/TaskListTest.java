package com.sucy.skill.thread;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * SkillAPI © 2017
 * com.sucy.skill.thread.TaskListTest
 */
public class TaskListTest {

    private TaskList subject;

    @Before
    public void setUp() {
        subject = new TaskList();
    }

    @Test
    public void size() throws Exception {
        subject.add(mock(IThreadTask.class));
        assertEquals(subject.size(), 1);
    }

    @Test
    public void clear() throws Exception {
        subject.add(mock(IThreadTask.class));
        subject.clear();
        assertEquals(subject.size(), 0);
    }

    @Test
    public void iterate_empty() {
        for (IThreadTask task : subject) {
            fail();
        }
    }

    @Test
    public void iterate_multiple() {
        List<IThreadTask> tasks = ImmutableList.of(
                mock(IThreadTask.class),
                mock(IThreadTask.class),
                mock(IThreadTask.class)
        );
        for (IThreadTask task : tasks) {
            subject.add(task);
        }
        Iterator<IThreadTask> iterator = tasks.iterator();
        for (IThreadTask task : subject) {
            assertThat(task, sameInstance(iterator.next()));
        }
    }
}