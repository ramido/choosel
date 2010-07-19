package org.thechiselgroup.choosel.client.ui.dnd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlacklistDropTargetCapabilityChecker implements
        DropTargetCapabilityChecker {

    private Map<String, Set<String>> viewIDToResourceTypeBlackList = new HashMap<String, Set<String>>();

    protected void disableResourceTypeToViewDrop(String viewID,
            String resourceType) {

        if (!viewIDToResourceTypeBlackList.containsKey(viewID)) {
            viewIDToResourceTypeBlackList.put(viewID, new HashSet<String>());
        }

        viewIDToResourceTypeBlackList.get(viewID).add(resourceType);

        assert viewIDToResourceTypeBlackList.get(viewID).contains(resourceType);
    }

    @Override
    public boolean isValidDrop(String viewId, String resourceType) {
        if (!viewIDToResourceTypeBlackList.containsKey(viewId)) {
            return true;
        }

        return !viewIDToResourceTypeBlackList.get(viewId)
                .contains(resourceType);
    }
}
