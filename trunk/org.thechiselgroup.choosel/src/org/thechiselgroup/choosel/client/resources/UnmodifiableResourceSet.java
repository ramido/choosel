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
package org.thechiselgroup.choosel.client.resources;

public class UnmodifiableResourceSet extends DelegatingResourceSet {

    public UnmodifiableResourceSet(ResourceSet delegate) {
        super(delegate);
    }

    @Override
    public void add(Resource i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAll(Iterable<Resource> resources) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isModifiable() {
        return false;
    }

    @Override
    public void remove(Resource i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll(Iterable<Resource> resources) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLabel(String label) {
        throw new UnsupportedOperationException();
    }

}