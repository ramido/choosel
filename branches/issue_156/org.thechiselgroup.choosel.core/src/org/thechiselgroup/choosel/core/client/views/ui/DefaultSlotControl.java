package org.thechiselgroup.choosel.core.client.views.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.transform.Transformer;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfigurationUIModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverUIController;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

//Slot name is [________] + [uiControllerWidget]
public class DefaultSlotControl extends SlotControl {

    private ViewItemValueResolverUIController uiController;

    private ListBoxControl<ViewItemValueResolverFactory> resolverFactorySelector;

    private ChangeHandler factorySelectedChangeHandler;

    // TODO I don't like how I need this here just to get the viewItems. I feel
    // like this class should not know about view items. It could always keep
    // track of updates, but then it is possible to introduce inconsistencies
    private SlotMappingConfigurationUIModel configuration;

    public DefaultSlotControl(Slot slot,
            SlotMappingConfigurationUIModel configurationUIModel,
            ViewItemValueResolverUIController uiController) {

        super(slot);
        this.uiController = uiController;
        this.configuration = configurationUIModel;

        /**
         * Only responsible for changing the selected resolver, the change in UI
         * will handled elsewhere
         */
        factorySelectedChangeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                ViewItemValueResolverFactory resolverFactory = resolverFactorySelector
                        .getSelectedValue();

                configuration.getSlotMappingUIModel(getSlot()).setResolver(
                        resolverFactory.create(configuration.getViewItems()));
            }
        };
    }

    // TODO add special UI stuff for resolvers that are in error
    @Override
    public Widget asWidget() {
        updateFactorySelector();

        VerticalPanel panel = new VerticalPanel();

        Label slotNameLabel = new Label(getSlot().getName() + " is ");

        panel.add(slotNameLabel);
        panel.add(resolverFactorySelector.asWidget());
        panel.add(uiController.asWidget());

        return panel;
    }

    private List<ViewItemValueResolverFactory> getFactoryList() {
        List<ViewItemValueResolverFactory> result = new ArrayList<ViewItemValueResolverFactory>();
        Collection<ViewItemValueResolverFactory> allowableResolverFactories = configuration
                .getSlotMappingUIModel(getSlot())
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

        resolverFactorySelector.setChangeHandler(factorySelectedChangeHandler);

        resolverFactorySelector.setValues(getFactoryList());
        resolverFactorySelector.setSelectedValue(configuration
                .getSlotMappingUIModel(getSlot()).getCurrentFactory());
    }

    @Override
    public void updateOptions(LightweightCollection<ViewItem> viewItems) {
        updateFactorySelector();
        uiController.update(viewItems);
    }
}
