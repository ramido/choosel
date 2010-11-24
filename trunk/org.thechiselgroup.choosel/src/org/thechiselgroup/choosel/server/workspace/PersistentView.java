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
package org.thechiselgroup.choosel.server.workspace;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.workspace.dto.ResourceSetDTO;
import org.thechiselgroup.choosel.client.workspace.dto.WindowDTO;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class PersistentView {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent(serialized = "true")
    private String title;

    @Persistent(serialized = "true")
    private String contentType;

    @Persistent(serialized = "true")
    private Resource[] resources;

    @Persistent(serialized = "true")
    private ResourceSetDTO[] resourceSets;

    @Persistent(serialized = "true")
    private WindowDTO[] windows;

    @Persistent(serialized = "true")
    private Memento viewState;

    public String getContentType() {
        return contentType;
    }

    public Long getId() {
        return id;
    }

    public Resource[] getResources() {
        return resources;
    }

    public ResourceSetDTO[] getResourceSets() {
        return resourceSets;
    }

    public String getTitle() {
        return title;
    }

    public Memento getViewState() {
        return viewState;
    }

    public WindowDTO[] getWindows() {
        return windows;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public void setResourceSets(ResourceSetDTO[] resourceSets) {
        this.resourceSets = resourceSets;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setViewState(Memento viewState) {
        this.viewState = viewState;
    }

}