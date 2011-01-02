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
package org.thechiselgroup.choosel.protovis.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProtovisGWTExamples implements EntryPoint {

    private List<ProtovisExample> examples;

    private Label visualizationTitle;

    private Anchor protovisExampleLink;

    private Anchor sourceCodeLink;

    private ListBox selectorList;

    public <T extends Widget & ProtovisExample> void addExample(
            VerticalPanel container, T example) {

        container.add(new HTML("<b><a target='_blank' href='"
                + example.getProtovisExampleURL() + "'>" + example.toString()
                + "</a></b>"));
        container.add(example);
    }

    private void initExampleVisualizations() {
        examples = new ArrayList<ProtovisExample>();

        examples.add(new AreaChartExample());
        examples.add(new BarChartExample());
        examples.add(new PieChartExample());
        examples.add(new ScatterplotExample());
        examples.add(new LineChartExample());
        examples.add(new StackedChartExample());
        examples.add(new GroupedChartExample());
        examples.add(new AndersonsFlowersExample());
        examples.add(new BeckersBarleyExample());
        examples.add(new StreamgraphExample());
        examples.add(new BulletChartExample());
        examples.add(new CandlestickChartExample());
        examples.add(new BurtinAntibioticsExample());
        examples.add(new PlayfairsWheatExample());
        examples.add(new GasAndDrivingExample());
        examples.add(new SeattleWeatherExample());
        examples.add(new BoxAndWhiskerPlotExample());
    }

    private void initProtovisExampleLink() {
        protovisExampleLink = new Anchor("Original Protovis Example");
        protovisExampleLink.setTarget("_blank");
        RootPanel.get("protovisExampleLink").add(protovisExampleLink);
    }

    private void initSourceCodeLink() {
        sourceCodeLink = new Anchor("Protovis/GWT Example Source Code");
        sourceCodeLink.setTarget("_blank");
        RootPanel.get("sourceCodeLink").add(sourceCodeLink);
    }

    private void initVisualizationSelector() {
        selectorList = new ListBox();
        selectorList.setVisibleItemCount(20);
        for (int i = 0; i < examples.size(); i++) {
            selectorList.addItem(examples.get(i).toString());
        }
        selectorList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                update();
            }
        });
        selectorList.setSelectedIndex(0);

        RootPanel.get("visualizationSelector").add(selectorList);
    }

    private void initVisualizationTitleLabel() {
        visualizationTitle = new Label();
        RootPanel.get("visualizationTitle").add(visualizationTitle);
    }

    public void onModuleLoad() {
        initExampleVisualizations();
        initVisualizationTitleLabel();
        initProtovisExampleLink();
        initSourceCodeLink();
        initVisualizationSelector();

        update();
    }

    private void update() {
        int i = selectorList.getSelectedIndex();
        ProtovisExample example = examples.get(i);

        visualizationTitle.setText(example.toString());
        protovisExampleLink.setHref(example.getProtovisExampleURL());
        sourceCodeLink
                .setHref("http://code.google.com/p/choosel/source/browse/trunk/"
                        + "org.thechiselgroup.choosel.protovis/examples/"
                        + "org/thechiselgroup/choosel/protovis/client/"
                        + example.getSourceCodeFile());

        RootPanel rootPanel = RootPanel.get("visualization");
        if (rootPanel.getWidgetCount() > 0) {
            rootPanel.remove(0);
        }
        rootPanel.add(example.asWidget());
    }
}