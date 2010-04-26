package org.thechiselgroup.biomixer.client;

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.*;

import org.thechiselgroup.biomixer.client.domain.ncbo.NCBOSearchWindowContent;
import org.thechiselgroup.choosel.client.ChooselWindowContentProducerProvider;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BioMixerWindowContentProducerProvider extends
	ChooselWindowContentProducerProvider {

    @Inject
    protected NCBOSearchWindowContent nCBOSearchViewContent;

    @Inject
    public BioMixerWindowContentProducerProvider(
	    @Named(AVATAR_FACTORY_SET) ResourceSetAvatarFactory userSetsDragAvatarFactory,
	    @Named(AVATAR_FACTORY_TYPE) ResourceSetAvatarFactory typesDragAvatarFactory,
	    @Named(AVATAR_FACTORY_ALL_RESOURCES) ResourceSetAvatarFactory allResourcesDragAvatarFactory,
	    @Named(AVATAR_FACTORY_SELECTION) ResourceSetAvatarFactory selectionDragAvatarFactory,
	    @Named(AVATAR_FACTORY_SELECTION_DROP) ResourceSetAvatarFactory dropTargetFactory,
	    @Named(HOVER_MODEL) ResourceSet hoverModel,
	    ResourceSetFactory resourceSetFactory,
	    @Named(LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
	    ResourceCategorizer categorizer,
	    CategoryLabelProvider labelProvider,
	    @Named(DROP_TARGET_MANAGER_VIEW_CONTENT) ResourceSetAvatarDropTargetManager contentDropTargetManager) {

	super(userSetsDragAvatarFactory, typesDragAvatarFactory,
		allResourcesDragAvatarFactory, selectionDragAvatarFactory,
		dropTargetFactory, hoverModel, resourceSetFactory,
		selectionModelLabelFactory, categorizer, labelProvider,
		contentDropTargetManager);

	windowContentFactories.put("ncbo-search", new WindowContentFactory() {
	    @Override
	    public WindowContent createWindowContent() {
		return nCBOSearchViewContent;
	    }
	});
    }
}
