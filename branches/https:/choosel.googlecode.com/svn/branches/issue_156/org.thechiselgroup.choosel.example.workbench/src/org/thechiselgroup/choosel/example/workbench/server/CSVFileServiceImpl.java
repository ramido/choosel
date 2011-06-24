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
package org.thechiselgroup.choosel.example.workbench.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.example.workbench.client.services.CSVFileService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class CSVFileServiceImpl extends RemoteServiceServlet implements
        CSVFileService {

    @Override
    public Set<Resource> getCSVResources(String filePath, String fileName)
            throws Exception {

        ServletContext servletContext = getServletContext();
        InputStream resourceAsStream = servletContext
                .getResourceAsStream(filePath + fileName);

        if (resourceAsStream == null) {
            throw new Exception(
                    "File cannot be found.  Please ensure that the file is in the data folder");
        }

        InputStreamReader reader = new InputStreamReader(resourceAsStream);

        BufferedReader in = new BufferedReader(reader);
        int count = 1;

        try {
            String[] fieldNames = in.readLine().trim().split(",");
            Set<Resource> resources = new HashSet<Resource>();

            String line = in.readLine();
            while (line != null) {
                String[] fieldValues = line.trim().split(",");

                // TODO maybe should be if else
                assert (fieldNames.length == fieldValues.length);

                Resource resource = new Resource("csv:" + count++);
                for (int i = 0; i < fieldValues.length; i++) {

                    resource.putValue(fieldNames[i].trim(),
                            fieldValues[i].trim());
                }

                resources.add(resource);
                line = in.readLine();
            }

            return resources;
        } finally {
            in.close();
        }
    }

}
