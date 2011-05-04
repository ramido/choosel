package org.thechiselgroup.choosel.core.client.ui;

import static org.junit.Assert.*;

import org.junit.Test;

public class ColorTest {

    @Test
    public void blueGreenHex() {
        testColor(0, 74, 192, 1.0, new Color("#004Ac0"));
    }

    @Test
    public void blueHex() {
        testColor(0, 0, 131, 1.0, new Color("#000083"));
    }

    @Test
    public void fullblueHex() {
        testColor(0, 0, 255, 1.0, new Color("#0000ff"));
    }

    @Test
    public void fullgreenHex() {
        testColor(0, 255, 0, 1.0, new Color("#00ff00"));
    }

    @Test
    public void fullredblueGreen() {
        testColor(255, 255, 255, 1.0, new Color("#ffffff"));
    }

    @Test
    public void fullredHex() {
        testColor(255, 0, 0, 1.0, new Color("#ff0000"));
    }

    @Test
    public void greenHex() {
        testColor(0, 207, 0, 1.0, new Color("#00Cf00"));
    }

    @Test
    public void invalidInput() {
        try {
            Color color = new Color("00-000");
            fail("Colour should have been invalid character");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void noColorHex() {
        testColor(0, 0, 0, 1.0, new Color("#000000"));
    }

    @Test
    public void outOfRange() {
        try {
            Color color = new Color("#00g000");
            fail("Hex shoudl have been out of range");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void redBlueGreen() {
        testColor(120, 154, 188, 1.0, new Color("#789abc"));
    }

    @Test
    public void redBlueHex() {
        testColor(49, 0, 214, 1.0, new Color("#3100d6"));
    }

    @Test
    public void redGreenHex() {
        testColor(63, 109, 0, 1.0, new Color("#3f6d00"));
    }

    @Test
    public void redHex() {
        testColor(42, 0, 0, 1.0, new Color("#2a0000"));
    }

    private void testColor(int red, int green, int blue, double alpha,
            Color underTest) {
        assertEquals(red, underTest.getRed());
        assertEquals(blue, underTest.getBlue());
        assertEquals(green, underTest.getGreen());
        assertEquals(alpha, underTest.getAlpha(), 0.0);
    }

}
