package twilightforest.data.models;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

public class TFBlockModelTemplates extends ModelTemplates {

	public static final ModelTemplate ANTIBUILDER = create("antibuilder", TextureSlot.ALL, TFTextureSlot.ALL_2, TFTextureSlot.ALL_3).extend()
		.parent(ResourceLocation.withDefaultNamespace("block/cube_all"))
		.element(builder -> builder.allFaces((direction, faceBuilder) -> faceBuilder.emissivity(15, 15).texture(TFTextureSlot.ALL_2).cullface(direction)))
		.element(builder -> builder.allFaces((direction, faceBuilder) -> faceBuilder.emissivity(10, 10).texture(TFTextureSlot.ALL_3).cullface(direction))).build();
	public static final ModelTemplate ANTIBUILT_BLOCK = create("antibuilt_block", TextureSlot.ALL, TFTextureSlot.ALL_2).extend()
		.parent(ResourceLocation.withDefaultNamespace("block/cube_all"))
		.element(builder -> builder.allFaces((direction, faceBuilder) -> faceBuilder.emissivity(10, 10).texture(TFTextureSlot.ALL_2).cullface(direction))).build();
	public static final ModelTemplate FORCEFIELD = create("forcefield", TextureSlot.PANE, TextureSlot.PARTICLE).extend().parent(ResourceLocation.withDefaultNamespace("block/cube_all")).ambientOcclusion(false).renderType("translucent").build();

	public static final ModelTemplate CTM_NO_BASE = create("ctm_no_base", TextureSlot.PARTICLE, TFTextureSlot.CTM_OVERLAY, TFTextureSlot.CTM_OVERLAY_CONNECTED).extend().parent(ResourceLocation.withDefaultNamespace("block/block")).build();
	public static final ModelTemplate CTM = create("ctm", TextureSlot.PARTICLE, TFTextureSlot.CTM_BASE, TFTextureSlot.CTM_OVERLAY, TFTextureSlot.CTM_OVERLAY_CONNECTED).extend().parent(ResourceLocation.withDefaultNamespace("block/block")).build();

}
