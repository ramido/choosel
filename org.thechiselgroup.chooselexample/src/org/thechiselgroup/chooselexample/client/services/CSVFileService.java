package org.thechiselgroup.chooselexample.client.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.thechiselgroup.choosel.client.resources.Resource;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("csvservice")
public interface CSVFileService extends RemoteService {

    Set<Resource> getCSVResources(String filePath, String fileName) throws Exception;
    
}
