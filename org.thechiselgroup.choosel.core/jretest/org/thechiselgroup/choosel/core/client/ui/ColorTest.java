package org.thechiselgroup.choosel.core.client.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ColorTest {

    @Test
    public void hex000000() {
        assertEquals(new Color(0, 0, 0), new Color("#000000"));
    }

    @Test
    public void hex000083() {
        assertEquals(new Color(0, 0, 131), new Color("#000083"));
    }

    @Test
    public void hex0000ff() {
        assertEquals(new Color(0, 0, 255), new Color("#0000ff"));
    }

    @Test
    public void hex004ac0() {
        assertEquals(new Color(0, 74, 192), new Color("#004ac0"));
    }

    @Test
    public void hex00Cf00() {
        assertEquals(new Color(0, 207, 0), new Color("#00Cf00"));
    }

    @Test
    public void hex00ff00() {
        assertEquals(new Color(0, 255, 0), new Color("#00ff00"));
    }

    @Test
    public void hex2a0000() {
        assertEquals(new Color(42, 0, 0), new Color("#2a0000"));
    }

    @Test
    public void hex3100d6() {
        assertEquals(new Color(49, 0, 214), new Color("#3100d6"));
    }

    @Test
    public void hex3f6d00() {
        assertEquals(new Color(63, 109, 0), new Color("#3f6d00"));
    }

    @Test
    public void hex789abc() {
        assertEquals(new Color(120, 154, 188), new Color("#789abc"));
    }

    @Test
    public void hexff0000() {
        assertEquals(new Color(255, 0, 0), new Color("#ff0000"));
    }

    @Test
    public void hexffffff() {
        assertEquals(new Color(255, 255, 255), new Color("#ffffff"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHex00_000() {
        new Color("00-000");
        fail("Should have thrown IllegalArgumentException: hex contains invalid character");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHex00_500() {
        new Color("#00-500");
        fail("Should have thrown IllegalArgumentException: hex contains invalid character");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHex00g000() {
        new Color("#00g000");
        fail("Should have thrown IllegalArgumentException: hex contains invalid character");
    }

}