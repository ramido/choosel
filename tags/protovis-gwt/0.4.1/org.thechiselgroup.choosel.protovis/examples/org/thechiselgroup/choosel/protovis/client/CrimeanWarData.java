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
package org.thechiselgroup.choosel.protovis.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class CrimeanWarData {

    private Date date;

    private int wounds;

    private int other;

    private int disease;

    private CrimeanWarData(Date date, int wounds, int other, int disease) {
        this.date = date;
        this.wounds = wounds;
        this.other = other;
        this.disease = disease;
    }

    public Date getDate() {
        return date;
    }

    public int getWounds() {
        return wounds;
    }

    public int getOther() {
        return other;
    }

    public int getDisease() {
        return disease;
    }

    public static CrimeanWarData[] getData() {
        DateTimeFormat format = DateTimeFormat.getFormat("MM/yyyy");
        return new CrimeanWarData[] {
                new CrimeanWarData(format.parse("4/1854"), 0, 110, 110),
                new CrimeanWarData(format.parse("5/1854"), 0, 95, 105),
                new CrimeanWarData(format.parse("6/1854"), 0, 40, 95),
                new CrimeanWarData(format.parse("7/1854"), 0, 140, 520),
                new CrimeanWarData(format.parse("8/1854"), 20, 150, 800),
                new CrimeanWarData(format.parse("9/1854"), 220, 230, 740),
                new CrimeanWarData(format.parse("10/1854"), 305, 310, 600),
                new CrimeanWarData(format.parse("11/1854"), 480, 290, 820),
                new CrimeanWarData(format.parse("12/1854"), 295, 310, 1100),
                new CrimeanWarData(format.parse("1/1855"), 230, 460, 1440),
                new CrimeanWarData(format.parse("2/1855"), 180, 520, 1270),
                new CrimeanWarData(format.parse("3/1855"), 155, 350, 935),
                new CrimeanWarData(format.parse("4/1855"), 195, 195, 560),
                new CrimeanWarData(format.parse("5/1855"), 180, 155, 550),
                new CrimeanWarData(format.parse("6/1855"), 330, 130, 650),
                new CrimeanWarData(format.parse("7/1855"), 260, 130, 430),
                new CrimeanWarData(format.parse("8/1855"), 290, 110, 490),
                new CrimeanWarData(format.parse("9/1855"), 355, 100, 290),
                new CrimeanWarData(format.parse("10/1855"), 135, 95, 245),
                new CrimeanWarData(format.parse("11/1855"), 100, 140, 325),
                new CrimeanWarData(format.parse("12/1855"), 40, 120, 215),
                new CrimeanWarData(format.parse("1/1856"), 0, 160, 160),
                new CrimeanWarData(format.parse("2/1856"), 0, 100, 100),
                new CrimeanWarData(format.parse("3/1856"), 0, 125, 90) };
    }
}