package org.thechiselgroup.choosel.client.resources;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ResourceTest {

    private String propertyKey;

    private Resource resource;

    // ask lars about following two tests
    @Test
    public void getUriListValueReturnsUriListProperty() {
        UriList uriList = new UriList();
        uriList.add("uri1");
        uriList.add("uri2");
        uriList.add("uri3");

        resource.putValue(propertyKey, uriList);
        assertEquals(true,
                resource.getUriListValue(propertyKey) instanceof UriList);
    }

    @Test
    public void getUriListValueReturnsUriListStringProperty() {
        resource.putValue(propertyKey, "x");
        assertEquals(true,
                resource.getUriListValue(propertyKey) instanceof UriList);
    }

    @Test
    public void isUriListReturnsFalseIfPropertyIsInt() {
        resource.putValue(propertyKey, 5);
        assertEquals(false, resource.isUriList(propertyKey));
    }

    @Test
    public void isUriListReturnsFalseIfPropertyIsString() {
        resource.putValue(propertyKey, "String");
        assertEquals(false, resource.isUriList(propertyKey));
    }

    @Test
    public void isUriListReturnsFalseIfPropertyNotSet() {
        assertEquals(false, resource.isUriList(propertyKey));
    }

    @Test
    public void isUriListReturnsFalseIfPropertyValueIsNull() {
        resource.putValue(propertyKey, null);
        assertEquals(false, resource.isUriList(propertyKey));
    }

    @Test
    public void isUriListReturnsTrueIfPropertyIsEmptyUriList() {
        UriList uriList = new UriList();
        resource.putValue(propertyKey, uriList);

        assertEquals(true, resource.isUriList(propertyKey));
    }

    @Test
    public void isUriListReturnsTrueIfPropertyIsPopulatedUriList() {
        UriList uriList = new UriList();
        uriList.add("uri1");
        uriList.add("uri2");
        uriList.add("uri3");

        resource.putValue(propertyKey, uriList);

        assertEquals(true, resource.isUriList(propertyKey));
    }

    @Before
    public void setUp() {
        propertyKey = "p";
        resource = new Resource("1");
    }
}
