/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.util.callbacks;

import org.thechiselgroup.choosel.core.client.util.Transformer;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class TransformingAsyncCallback<S, T> implements AsyncCallback<S> {

    // NOTE: allows creation without specifying generics twice
    public static <S, T> TransformingAsyncCallback<S, T> create(
            AsyncCallback<T> callback, Transformer<S, T> transformer) {

        return new TransformingAsyncCallback<S, T>(callback, transformer);
    }

    private final AsyncCallback<T> callback;

    private final Transformer<S, T> transformer;

    protected TransformingAsyncCallback(AsyncCallback<T> callback,
            Transformer<S, T> transformer) {

        assert transformer != null;
        assert callback != null;

        this.callback = callback;
        this.transformer = transformer;
    }

    @Override
    public void onFailure(Throwable caught) {
        callback.onFailure(caught);
    }

    @Override
    public void onSuccess(S result) {
        try {
            callback.onSuccess(transformer.transform(result));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}