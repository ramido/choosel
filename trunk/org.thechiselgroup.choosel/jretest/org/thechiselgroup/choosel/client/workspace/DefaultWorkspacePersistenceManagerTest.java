/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.client.workspace;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.DelegatingResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.UnmodifiableResourceSet;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.windows.WindowPanel;
import org.thechiselgroup.choosel.client.workspace.DefaultWorkspacePersistenceManager;
import org.thechiselgroup.choosel.client.workspace.Workspace;
import org.thechiselgroup.choosel.client.workspace.WorkspaceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspaceSavingState;
import org.thechiselgroup.choosel.client.workspace.dto.WorkspaceDTO;
import org.thechiselgroup.choosel.client.workspace.service.WorkspacePersistenceServiceAsync;
import org.thechiselgroup.choosel.client.workspace.service.WorkspaceSharingServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DefaultWorkspacePersistenceManagerTest {

    private static interface TestPersistableWindowContent extends
	    WindowContent, Persistable {

    }

    private static final String CONTENT_TYPE = "content";

    private static final String TEST_WORKSPACE_NAME = "test-workspace-name";

    @Mock
    private Desktop desktop;

    @Mock
    private WorkspacePersistenceServiceAsync persistenceService;

    @Mock
    private ResourceManager resourceManager;

    private ResourceSetFactory resourceSetFactory;

    @Mock
    private View restoredView;

    @Mock
    private AsyncCallback<Void> saveCallback;

    @Mock
    private WorkspaceSharingServiceAsync sharingService;

    private DefaultWorkspacePersistenceManager underTest;

    @Mock
    private TestPersistableWindowContent windowContent;

    @Mock
    private WindowContentFactory viewFactory;

    @Mock
    private WindowPanel window;

    private List<WindowPanel> windows;

    private Workspace workspace;

    @Mock
    private WorkspaceManager workspaceManager;

    private WorkspaceDTO doSave() {
	underTest.saveWorkspace(saveCallback);

	ArgumentCaptor<WorkspaceDTO> argument = ArgumentCaptor
		.forClass(WorkspaceDTO.class);
	verify(persistenceService, times(1)).saveWorkspace(argument.capture(),
		any(AsyncCallback.class));

	return argument.getValue();
    }

    @Test
    public void saveAndRestoreUnmodifiableSet() {
	// use string buffer so its modifiable
	final StringBuffer id = new StringBuffer();

	DefaultResourceSet delegate = createResources(1, 2);
	final ResourceSet unmodifiableSet = new UnmodifiableResourceSet(
		delegate);

	when(windowContent.save(any(ResourceSetCollector.class))).thenAnswer(
		new Answer<Memento>() {
		    @Override
		    public Memento answer(InvocationOnMock invocation)
			    throws Throwable {

			ResourceSetCollector collector = (ResourceSetCollector) invocation
				.getArguments()[0];

			id.append(collector.storeResourceSet(unmodifiableSet));

			return new Memento();
		    }
		});

	when(resourceManager.getByUri(createResource(1).getUri())).thenReturn(
		createResource(1));
	when(resourceManager.getByUri(createResource(2).getUri())).thenReturn(
		createResource(2));

	WorkspaceDTO dto = doSave();
	doLoad(dto);

	// check correct restore -- how
	ArgumentCaptor<ResourceSetAccessor> argument = ArgumentCaptor
		.forClass(ResourceSetAccessor.class);
	verify(restoredView, times(1)).restore(any(Memento.class),
		argument.capture());

	ResourceSet resourceSet = argument.getValue().getResourceSet(
		Integer.parseInt(id.toString()));
	assertEquals(true, resourceSet instanceof UnmodifiableResourceSet);
	assertEquals(true, delegate
		.containsEqualResources(((DelegatingResourceSet) resourceSet)
			.getDelegate()));
    }

    @Test
    public void useWindowOffsetWidth() {
	int height = 100;
	int width = 200;

	when(window.getOffsetWidth()).thenReturn(width);
	when(window.getOffsetHeight()).thenReturn(height);

	WorkspaceDTO dto = doSave();
	doLoad(dto);

	verify(desktop).createWindow(eq(restoredView), eq(0), eq(0), eq(width),
		eq(height));
    }

    private Workspace doLoad(WorkspaceDTO dto) {
	return underTest.loadWorkspace(dto);
    }

    @Test
    public void saveWindow() {
	WorkspaceDTO resultDTO = doSave();
	assertEquals(1, resultDTO.getWindows().length);
    }

    @Before
    public void setUp() {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	resourceSetFactory = new DefaultResourceSetFactory();

	workspace = spy(new Workspace());
	workspace.setName(TEST_WORKSPACE_NAME);

	underTest = new DefaultWorkspacePersistenceManager(workspaceManager,
		desktop, persistenceService, viewFactory, resourceManager,
		resourceSetFactory, sharingService);

	when(workspaceManager.getWorkspace()).thenReturn(workspace);
	when(window.getViewContent()).thenReturn(windowContent);
	when(windowContent.getContentType()).thenReturn(CONTENT_TYPE);
	when(viewFactory.createWindowContent(CONTENT_TYPE)).thenReturn(
		restoredView);

	windows = new ArrayList<WindowPanel>();
	windows.add(window);
	when(desktop.getWindows()).thenReturn(windows);
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

    @Test
    public void updateIdAfterSave() {
	underTest.saveWorkspace(saveCallback);

	ArgumentCaptor<AsyncCallback> argument = ArgumentCaptor
		.forClass(AsyncCallback.class);
	verify(persistenceService, times(1)).saveWorkspace(
		any(WorkspaceDTO.class), argument.capture());
	AsyncCallback<Long> callback = argument.getValue();

	Long value = new Long(15);
	callback.onSuccess(value);

	assertEquals(value, workspace.getId());
	assertEquals(false, workspace.isNew());
    }

    @Test
    public void changeWorkspaceSavingState() {
	underTest.saveWorkspace(saveCallback);

	verify(workspace).setSavingState(WorkspaceSavingState.SAVING);

	ArgumentCaptor<AsyncCallback> argument = ArgumentCaptor
		.forClass(AsyncCallback.class);
	verify(persistenceService, times(1)).saveWorkspace(
		any(WorkspaceDTO.class), argument.capture());
	AsyncCallback<Long> callback = argument.getValue();

	Long value = new Long(15);
	callback.onSuccess(value);

	verify(workspace).setSavingState(WorkspaceSavingState.SAVED);
    }
}
