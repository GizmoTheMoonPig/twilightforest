package twilightforest.data.models;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import twilightforest.data.TFBlockFamilies;
import twilightforest.data.helpers.ItemModelBuilders;

import java.util.function.BiConsumer;

public class ItemModelGenerator extends ItemModelBuilders {
	public ItemModelGenerator(ItemModelOutput output, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
		super(output, modelOutput);
	}

	@Override
	public void run() {

	}
}
