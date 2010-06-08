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
package org.thechiselgroup.choosel.client.resources.ui;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DefaultDetailsWidgetHelper extends DetailsWidgetHelper {

    @Inject
    public DefaultDetailsWidgetHelper(ResourceSetFactory resourceSetFactory,
	    ResourceSetAvatarFactory dragAvatarFactory,
	    ResourceSetAvatarDragController dragController) {
	super(resourceSetFactory, dragAvatarFactory, dragController);
    }

    // TODO use dragAvatarFactory (injection)
    @Override
    public Widget createDetailsWidget(ResourceSet resources,
	    ResourceSetToValueResolver resolver) {

	VerticalPanel verticalPanel = GWT.create(VerticalPanel.class);

	/// XXX broken in resource item change, reactivate and fix
//	for (Resource resource : resources) {
//	    // FIXME use generic way to put in custom widgets
//	    if (resource.getUri().startsWith("tsunami")) {
//		ResourceSetAvatar avatar = new ResourceSetAvatar("Tsunami",
//			"avatar-resourceSet", resources, ResourceSetAvatarType.SET);
//		avatar.setEnabled(true);
//		dragController.setDraggable(avatar, true);
//		verticalPanel.add(avatar);
//		
//		String value = resolver.getValue(resource).toString();
//		HTML html = GWT.create(HTML.class);
//		html.setHTML(value);
//		verticalPanel.add(html);
//	    } else if (resource.getUri().startsWith("earthquake")) {
//		ResourceSetAvatar avatar = new ResourceSetAvatar("Earthquake",
//			"avatar-resourceSet", resources, ResourceSetAvatarType.SET);
//		avatar.setEnabled(true);
//		dragController.setDraggable(avatar, true);
//		verticalPanel.add(avatar);
//		
//		String value = resolver.getValue(resource).toString();
//		HTML html = GWT.create(HTML.class);
//		html.setHTML(value);
//		verticalPanel.add(html);
//	    } else {
//		verticalPanel.add(avatarFactory.createAvatar(resources));
//		
//		String value = resolver.getValue(resource).toString();
//		HTML html = GWT.create(HTML.class);
//		html.setHTML(value);
//		verticalPanel.add(html);
//	    }
//	}
//	

	return verticalPanel;
    }

}
