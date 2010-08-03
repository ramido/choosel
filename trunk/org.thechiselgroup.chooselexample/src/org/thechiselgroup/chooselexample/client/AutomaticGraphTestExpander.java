package org.thechiselgroup.chooselexample.client;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.graph.AbstractAutomaticGraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;

public class AutomaticGraphTestExpander extends
		AbstractAutomaticGraphNodeExpander implements GraphNodeExpander {

	@Override
	public void expand(ResourceItem resourceItem,
			GraphNodeExpansionCallback expansionCallback) {

		// TODO better resource item handling
		Resource resource = resourceItem.getResourceSet().getFirstResource();

		showArcs(resource, expansionCallback, "parent",
				"graph_test_relationship", false);
	}

}
