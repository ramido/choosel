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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.views.filter.ViewItemPredicate;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Status;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

public class ViewItemStatusResolver implements ViewItemValueResolver {

    public static class StatusRule implements ViewItemPredicate {

        public static StatusRule full(Object value, Subset subset) {
            return new StatusRule(value, subset, Status.FULL);
        }

        public static StatusRule fullOrPartial(Object value, Subset subset) {
            return new StatusRule(value, subset, Status.FULL, Status.PARTIAL);
        }

        private Subset subset;

        private Status[] status;

        private Object value;

        public StatusRule(Object value, Subset subset, Status... status) {
            assert value != null;
            assert subset != null;
            assert status != null;

            this.value = value;
            this.subset = subset;
            this.status = status;
        }

        // TODO extract as ViewItemPredicate
        @Override
        public boolean matches(ViewItem viewItem) {
            return viewItem.isStatus(subset, status);
        }

    }

    private final String id;

    private final Object defaultValue;

    private final StatusRule[] rules;

    public ViewItemStatusResolver(String resolverId, Object defaultValue,
            StatusRule... rules) {
        assert defaultValue != null;
        assert rules != null;

        this.rules = rules;
        this.defaultValue = defaultValue;
        this.id = resolverId;
    }

    @Override
    public String getResolverId() {
        return id;
    }

    @Override
    public Object resolve(ViewItem viewItem,
            ViewItemValueResolverContext context) {
        assert viewItem != null;

        for (StatusRule rule : rules) {
            if (rule.matches(viewItem)) {
                return rule.value;
            }
        }

        return defaultValue;
    }

}