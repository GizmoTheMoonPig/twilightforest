package twilightforest.data.helpers;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public abstract class ItemModelBuilders extends ItemModelGenerators {

	public ItemModelBuilders(ItemModelOutput output, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
		super(output, modelOutput);
	}

	@Override
	public abstract void run();
}
