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
package org.thechiselgroup.choosel.core.client.visualization.resolvers;

import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolverContext;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem.Status;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem.Subset;
import org.thechiselgroup.choosel.core.client.visualization.model.predicates.VisualItemPredicate;

public class ViewItemStatusResolver extends AbstractBasicVisualItemValueResolver {

    public static class StatusRule implements VisualItemPredicate {

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
        public boolean matches(VisualItem viewItem) {
            return viewItem.isStatus(subset, status);
        }

    }

    private final Object defaultValue;

    private final StatusRule[] rules;

    public ViewItemStatusResolver(Object defaultValue, StatusRule... rules) {
        assert defaultValue != null;
        assert rules != null;

        this.rules = rules;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean canResolve(VisualItem viewItem,
            VisualItemValueResolverContext context) {

        return true;
    }

    @Override
    public Object resolve(VisualItem viewItem,
            VisualItemValueResolverContext context) {
        assert viewItem != null;

        for (StatusRule rule : rules) {
            if (rule.matches(viewItem)) {
                return rule.value;
            }
        }

        return defaultValue;
    }
}