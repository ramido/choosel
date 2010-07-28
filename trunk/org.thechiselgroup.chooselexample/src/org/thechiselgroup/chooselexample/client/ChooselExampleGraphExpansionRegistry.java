package org.thechiselgroup.chooselexample.client;

import org.thechiselgroup.choosel.client.views.graph.GraphExpansionRegistry;

import com.google.inject.Inject;

public class ChooselExampleGraphExpansionRegistry extends
        GraphExpansionRegistry {

    @Inject
    public ChooselExampleGraphExpansionRegistry() {
        putAutomaticExpander("graphtest", new AutomaticGraphTestExpander());
        putNodeMenuEntry("graphtest", "Graph Test Items",
                new GraphTestGraphTestGraphNodeExpander());
    }

}