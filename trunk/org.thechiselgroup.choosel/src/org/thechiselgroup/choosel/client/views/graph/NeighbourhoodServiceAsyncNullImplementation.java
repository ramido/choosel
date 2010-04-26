package org.thechiselgroup.choosel.client.views.graph;

import org.thechiselgroup.choosel.client.resources.Resource;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class NeighbourhoodServiceAsyncNullImplementation implements
	NeighbourhoodServiceAsync {

    @Override
    public void getNeighbourhood(final Resource inputConcept,
	    final AsyncCallback<NeighbourhoodServiceResult> callback) {
	callback.onSuccess(new NeighbourhoodServiceResult(inputConcept));
    }
}
