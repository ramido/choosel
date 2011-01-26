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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.thechiselgroup.choosel.example.workbench.client.services.ProxyService;
import org.thechiselgroup.choosel.workbench.client.services.ServiceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

//TODO move to choosel framework
@SuppressWarnings("serial")
public class ProxyServiceImpl extends RemoteServiceServlet implements
        ProxyService {

    // TODO instead of returning the plain data, the server
    // could transform different formats (e.g. feeds, csv, owl, xml)
    // to a common denominator
    @Override
    public String fetchURL(String urlString) throws ServiceException {
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    url.openStream()));

            String content = "";
            String line;
            while ((line = reader.readLine()) != null) {
                content += line + "\n"; // TODO use linebreak property
            }
            reader.close();

            return content;

        } catch (MalformedURLException e) {
            throw new ServiceException(e.getMessage());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }

    }
}
