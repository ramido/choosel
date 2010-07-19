package org.thechiselgroup.choosel.client.ui.dnd;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class BlacklistDropTargetCapabilityCheckerTest {

    private static final String RESOURCE_TYPE1 = "resource-type1";

    private static final String VIEW_ID = "view-id";

    private BlacklistDropTargetCapabilityChecker underTest;

    @Test
    public void isInvalidDropIfContainedInBlackList() {
        underTest.disableResourceTypeToViewDrop(VIEW_ID, RESOURCE_TYPE1);

        assertEquals(false, underTest.isValidDrop(VIEW_ID, RESOURCE_TYPE1));
    }

    @Test
    public void isValidDropIfNotContainedInBlackList() {
        assertEquals(true, underTest.isValidDrop(VIEW_ID, RESOURCE_TYPE1));
    }

    @Before
    public void setUp() {
        underTest = new BlacklistDropTargetCapabilityChecker();
    }

}