package org.thechiselgroup.choosel.client.resources;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourceTest {

    @Test
    public void getType1() {
	assertEquals("type", new Resource("type://test").getType());
    }

    @Test
    public void getType2() {
	assertEquals("beta", new Resource("beta://stuff/").getType());
    }

}
