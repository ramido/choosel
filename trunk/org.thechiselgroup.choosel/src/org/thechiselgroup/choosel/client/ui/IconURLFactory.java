/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.thechiselgroup.choosel.client.ui;

/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
public final class IconURLFactory {

    private static String encode(String text) {
        text = text.replace("@", "@@");
        text = text.replace("\\", "@\\");
        text = text.replace("'", "@'");
        text = text.replace("[", "@[");
        text = text.replace("]", "@]");
        return encodeURI(text);
    }

    public static native String encodeURI(String text) /*-{
                                                          return encodeURIComponent(text);
                                                          }-*/;

    public static String getFlatIconURL(int width, int height, String label,
            String color) {

        String primaryColor = color;
        String shadowColor = "#000000";
        String labelColor = "#000000";
        int labelSize = 0;
        String shape = "circle";
        String shapeCode = (shape == "circle") ? "it" : "itr";

        String baseUrl = "http://chart.apis.google.com/chart?cht=" + shapeCode;
        String iconUrl = baseUrl + "&chs=" + width + "x" + height + "&chco="
                + primaryColor.replace("#", "") + ","
                + shadowColor.replace("#", "") + "ff,ffffff01" + "&chl="
                + encode(label) + "&chx=" + labelColor.replace("#", "") + ","
                + labelSize;

        String resultUrl = iconUrl + "&chf=bg,s,00000000" + "&ext=.png";

        // icon.printImage = iconUrl + "&chof=gif";
        // icon.mozPrintImage = iconUrl + "&chf=bg,s,ECECD8" + "&chof=gif";
        // icon.transparent = iconUrl + "&chf=a,s,ffffff01&ext=.png";

        return resultUrl;
    }

    public static String getFlatIconURL(String label, String color) {
        return getFlatIconURL(20, 20, label, color);
    }

    private IconURLFactory() {
    }

}