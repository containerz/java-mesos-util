package net.elodina.mesos.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RangeTest {
    @Test
    public void init() {
        new Range("30");
        new Range("30..31");
        new Range(30);
        new Range(30, 31);

        // empty
        try { new Range(""); fail(); }
        catch (IllegalArgumentException e) {}

        // non int
        try { new Range("abc"); fail(); }
        catch (IllegalArgumentException e) {}

        // non int first
        try { new Range("abc..30"); fail(); }
        catch (IllegalArgumentException e) {}

        // non int second
        try { new Range("30..abc"); fail(); }
        catch (IllegalArgumentException e) {}

        // inverted range
        try { new Range("10..0"); fail(); }
        catch (IllegalArgumentException e) {}
    }

    @Test
    public void start_end() {
        assertEquals(0, new Range("0").start());
        assertEquals(0, new Range("0..10").start());
        assertEquals(10, new Range("0..10").end());
    }

    @Test
    public void overlap() {
        // no overlap
        assertNull(new Range(0, 10).overlap(new Range(20, 30)));
        assertNull(new Range(20, 30).overlap(new Range(0, 10)));
        assertNull(new Range(0).overlap(new Range(1)));

        // partial
        assertEquals(new Range(5, 10), new Range(0, 10).overlap(new Range(5, 15)));
        assertEquals(new Range(5, 10), new Range(5, 15).overlap(new Range(0, 10)));

        // includes
        assertEquals(new Range(2, 3), new Range(0, 10).overlap(new Range(2, 3)));
        assertEquals(new Range(2, 3), new Range(2, 3).overlap(new Range(0, 10)));
        assertEquals(new Range(5), new Range(0, 10).overlap(new Range(5)));

        // last point
        assertEquals(new Range(0), new Range(0, 10).overlap(new Range(0)));
        assertEquals(new Range(10), new Range(0, 10).overlap(new Range(10)));
        assertEquals(new Range(0), new Range(0).overlap(new Range(0)));
    }

    @Test
    public void contains() {
        assertTrue(new Range(0).contains(0));
        assertTrue(new Range(0,1).contains(0));
        assertTrue(new Range(0,1).contains(1));

        Range range = new Range(100, 200);
        assertTrue(range.contains(100));
        assertTrue(range.contains(150));
        assertTrue(range.contains(200));

        assertFalse(range.contains(99));
        assertFalse(range.contains(201));
    }

    @Test
    public void split() {
        assertEquals(Collections.<Range>emptyList(), new Range(0).split(0));

        assertEquals(Arrays.asList(new Range(1)), new Range(0, 1).split(0));
        assertEquals(Arrays.asList(new Range(0)), new Range(0, 1).split(1));

        assertEquals(Arrays.asList(new Range(0), new Range(2)), new Range(0, 2).split(1));
        assertEquals(Arrays.asList(new Range(100, 149), new Range(151, 200)), new Range(100, 200).split(150));

        try { new Range(100, 200).split(10); fail(); }
        catch (IllegalArgumentException e) {}

        try { new Range(100, 200).split(210); fail(); }
        catch (IllegalArgumentException e) {}
    }

    @Test
    public void _toString() {
        assertEquals("0", "" + new Range("0"));
        assertEquals("0..10", "" + new Range("0..10"));
        assertEquals("0", "" + new Range("0..0"));
    }
}
