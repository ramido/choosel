package org.thechiselgroup.choosel.client.workspace.service;

import org.thechiselgroup.choosel.client.services.ServiceException;
import org.thechiselgroup.choosel.client.workspace.dto.ViewDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("view")
public interface ViewPersistenceService extends RemoteService {

    ViewDTO loadView(Long viewId) throws ServiceException;

    Long saveView(ViewDTO view) throws ServiceException;
}
