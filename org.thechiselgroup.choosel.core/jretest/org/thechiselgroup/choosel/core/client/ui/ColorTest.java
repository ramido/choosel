package org.thechiselgroup.choosel.core.client.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ColorTest {

    @Test
    public void hex000000() {
        testColor(0, 0, 0, 1.0, new Color("#000000"));
    }

    @Test
    public void hex0000ff() {
        testColor(0, 0, 255, 1.0, new Color("#0000ff"));
    }

    @Test
    public void hex004ac0() {
        testColor(0, 74, 192, 1.0, new Color("#004ac0"));
    }

    @Test
    public void hex00Cf00() {
        testColor(0, 207, 0, 1.0, new Color("#00Cf00"));
    }

    @Test
    public void hex2a0000() {
        testColor(42, 0, 0, 1.0, new Color("#2a0000"));
    }

    @Test
    public void hex3100d6() {
        testColor(49, 0, 214, 1.0, new Color("#3100d6"));
    }

    @Test
    public void hex3f6d00() {
        testColor(63, 109, 0, 1.0, new Color("#3f6d00"));
    }

    @Test
    public void hex789abc() {
        testColor(120, 154, 188, 1.0, new Color("#789abc"));
    }

    @Test
    public void hexff0000() {
        testColor(255, 0, 0, 1.0, new Color("#ff0000"));
    }

    @Test
    public void hext00ff00() {
        testColor(0, 255, 0, 1.0, new Color("#00ff00"));
    }

    @Test
    public void hextffffff() {
        testColor(255, 255, 255, 1.0, new Color("#ffffff"));
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

    @Test
    public void semiBlueHex() {
        testColor(0, 0, 131, 1.0, new Color("#000083"));
    }

    private void testColor(int red, int green, int blue, double alpha,
            Color underTest) {
        assertEquals(red, underTest.getRed());
        assertEquals(blue, underTest.getBlue());
        assertEquals(green, underTest.getGreen());
        assertEquals(alpha, underTest.getAlpha(), 0.0);
    }

}
