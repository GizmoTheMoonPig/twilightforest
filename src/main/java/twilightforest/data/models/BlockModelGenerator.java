package twilightforest.data.models;

import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import twilightforest.data.TFBlockFamilies;
import twilightforest.data.helpers.BlockModelBuilders;
import twilightforest.init.TFBlocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BlockModelGenerator extends BlockModelBuilders {
	public BlockModelGenerator(Consumer<BlockStateGenerator> stateOutput, ItemModelOutput itemOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
		super(stateOutput, itemOutput, modelOutput);
	}

	@Override
	public void run() {
		TFBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach((family) -> this.family(family.getBaseBlock()).generateFor(family));

		this.createTrivialCube(TFBlocks.ANTIBUILT_BLOCK.get());
		this.createTrivialCube(TFBlocks.AURORA_BLOCK.get());
		this.createTrivialCube(TFBlocks.ARCTIC_FUR_BLOCK.get());
	}
}
