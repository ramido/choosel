package org.thechiselgroup.choosel.core.client.views.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.transform.Transformer;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotMappingUIModel;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverUIController;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

//Slot name is [________] + [uiControllerWidget]
public class DefaultSlotControl extends SlotControl {

    private ViewItemValueResolverUIController uiController;

    private SlotMappingUIModel uiModel;

    private ListBoxControl<ViewItemValueResolverFactory> resolverFactorySelector;

    public DefaultSlotControl(ViewItemValueResolverUIController uiController,
            SlotMappingUIModel uiModel) {

        super(uiModel.getSlot());
        this.uiController = uiController;
        this.uiModel = uiModel;
    }

    // TODO might want to add a check that the current resolver is contained, or
    // for errors or something
    @Override
    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();

        Label slotNameLabel = new Label(getSlot().getName() + " is ");

        updateFactorySelector();

        panel.add(slotNameLabel);
        panel.add(resolverFactorySelector.asWidget());
        panel.add(uiController.asWidget());

        return panel;
    }

    private List<ViewItemValueResolverFactory> getFactoryList() {
        List<ViewItemValueResolverFactory> result = new ArrayList<ViewItemValueResolverFactory>();
        Collection<ViewItemValueResolverFactory> allowableResolverFactories = uiModel
                .getAllowableResolverFactories();

        for (ViewItemValueResolverFactory factrory : allowableResolverFactories) {
            result.add(factrory);
        }

        return result;
    }

    private void updateFactorySelector() {
        resolverFactorySelector = new ListBoxControl<ViewItemValueResolverFactory>(
                new ExtendedListBox(false),
                new Transformer<ViewItemValueResolverFactory, String>() {
                    @Override
                    public String transform(ViewItemValueResolverFactory factory) {
                        return factory.getLabel();
                    }
                });

        resolverFactorySelector.setValues(getFactoryList());
        resolverFactorySelector.setSelectedValue(uiModel.getCurrentFactory());
    }

    @Override
    public void updateOptions(LightweightCollection<ViewItem> viewItems) {
        updateFactorySelector();
        uiController.update(viewItems);
    }

}
