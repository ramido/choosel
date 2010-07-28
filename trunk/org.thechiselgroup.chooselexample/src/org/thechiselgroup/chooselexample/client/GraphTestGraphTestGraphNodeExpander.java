package org.thechiselgroup.chooselexample.client;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.views.graph.AbstractGraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;

public class GraphTestGraphTestGraphNodeExpander extends
        AbstractGraphNodeExpander implements GraphNodeExpander {

    @Override
    public void expand(Resource resource,
            GraphNodeExpansionCallback expansionCallback) {

        addResources(expansionCallback, calculateUrisToAdd(resource, "parent"),
                resource);

        showArcs(resource, expansionCallback, "parent",
                "graph_test_relationship", true);

    }

}
