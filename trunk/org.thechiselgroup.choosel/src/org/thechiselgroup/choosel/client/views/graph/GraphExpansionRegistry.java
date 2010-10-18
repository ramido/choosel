package org.thechiselgroup.choosel.client.views.graph;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public interface GraphExpansionRegistry {

    GraphNodeExpander getAutomaticExpander(String category);

    List<NodeMenuEntry> getNodeMenuEntries(String category);

    Set<Entry<String, List<NodeMenuEntry>>> getNodeMenuEntriesByCategory();

    void putAutomaticExpander(String category, GraphNodeExpander expander);

    void putNodeMenuEntry(String category, NodeMenuEntry nodeMenuEntry);

    void putNodeMenuEntry(String category, String label,
            GraphNodeExpander expander);

}