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
package org.thechiselgroup.choosel.client.views.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class NodeMenuEntryRegistry {

    private Map<String, List<NodeMenuEntry>> menuEntriesByCategory = new HashMap<String, List<NodeMenuEntry>>();

    public void putNodeMenuEntry(String category, String label,
	    GraphNodeExpander expander) {

	assert category != null;
	assert label != null;
	assert expander != null;

	putNodeMenuEntry(category, new NodeMenuEntry(label, expander));
    }

    public void putNodeMenuEntry(String category, NodeMenuEntry nodeMenuEntry) {
	assert category != null;
	assert nodeMenuEntry != null;

	if (!menuEntriesByCategory.containsKey(category)) {
	    menuEntriesByCategory.put(category, new ArrayList<NodeMenuEntry>());
	}

	menuEntriesByCategory.get(category).add(nodeMenuEntry);
    }

    public Set<Entry<String, List<NodeMenuEntry>>> getNodeMenuEntriesByType() {
	return menuEntriesByCategory.entrySet();
    }

}
