package org.thechiselgroup.chooselexample.client.services;

import java.util.Set;

import org.thechiselgroup.choosel.client.resources.Resource;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CSVFileServiceAsync {
	void getCSVResources(String filePath, String fileName,
			AsyncCallback<Set<Resource>> callback) throws Exception;
}
