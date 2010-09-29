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
package org.thechiselgroup.choosel.client.importer;

import java.io.Serializable;

import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.views.map.MapViewContentDisplay;

import com.google.gwt.i18n.client.DateTimeFormat;

public class Importer {

    // TODO test
    // TODO pass set of parsers... --> separate step in which parsers are
    // determined
    public ResourceSet createResources(StringTable table) {
        ResourceSet resources = new DefaultResourceSet();
        resources.setLabel("import"); // TODO changeable, inc number

        for (int row = 0; row < table.getRowCount(); row++) {
            // XXX this is a bug because uri's are used for caching
            String uri = "import:" + row; // TODO improved uri generation
            Resource resource = new Resource(uri);

            for (int column = 0; column < table.getColumnCount(); column++) {
                String stringValue = table.getValue(row, column);

                /*
                 * TODO should not be parsed at this point - change once setting
                 * property types possible
                 */
                Serializable value = stringValue;

                // number
                if (stringValue.matches("^[-+]?[0-9]*\\.?[0-9]+")) {
                    value = new Double(stringValue);
                }

                // date
                if (stringValue
                        .matches("^(0[1-9]|[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012]|[1-9])/\\d{4}")) {

                    value = DateTimeFormat.getFormat("dd/MM/yyyy").parse(
                            stringValue);
                }

                // location (long/lat)
                if (stringValue
                        .matches("^[-+]?[0-9]*\\.?[0-9]+\\/[-+]?[0-9]*\\.?[0-9]+")) {

                    Resource r = new Resource();

                    String[] split = stringValue.split("\\/");

                    r.putValue(MapViewContentDisplay.LATITUDE,
                            Double.parseDouble(split[0]));
                    r.putValue(MapViewContentDisplay.LONGITUDE,
                            Double.parseDouble(split[1]));

                    value = r;

                }

                resource.putValue(table.getColumnName(column), value);
            }

            resources.add(resource);
        }

        return resources;
    }

}
