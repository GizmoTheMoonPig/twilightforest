package twilightforest.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import twilightforest.TwilightForestMod;
import twilightforest.client.renderer.entity.UrGhastRenderer;
import twilightforest.client.state.TFGhastRenderState;

public class UrGhastModel extends TFGhastModel implements TrophyBlockModel {

	private final ModelPart[][] tentacles = new ModelPart[9][4];
	private final ModelPart body;

	public UrGhastModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");

		for (int i = 0; i < this.tentacles.length; i++) {
			this.tentacles[i][0] = this.body.getChild("tentacle_" + i);
			this.tentacles[i][1] = this.tentacles[i][0].getChild("tentacle_" + i + "_extension");
			this.tentacles[i][2] = this.tentacles[i][1].getChild("tentacle_" + i + "_extension_2");
			this.tentacles[i][3] = this.tentacles[i][2].getChild("tentacle_" + i + "_tip");
		}
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		var body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F),
			PartPose.offset(0.0F, 8.0F, 0.0F));

		for (int i = 0; i < 9; ++i) {
			makeTentacle(body, "tentacle_" + i, i);
		}

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	protected static void makeTentacle(PartDefinition parent, String name, int iteration) {

		var tentacleBase = parent.addOrReplaceChild(name, CubeListBuilder.create()
				.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F),
			switch (iteration) {
				case 0 -> PartPose.offset(4.5F, 7.0F, 4.5F);
				case 1 -> PartPose.offset(-4.5F, 7.0F, 4.5F);
				case 2 -> PartPose.offset(0.0F, 7.0F, 0.0F);
				case 3 -> PartPose.offset(5.5F, 7.0F, -4.5F);
				case 4 -> PartPose.offset(-5.5F, 7.0F, -4.5F);
				case 5 -> PartPose.offsetAndRotation(-7.5F, 3.5F, -1.0F, 0.0F, 0.0F, Mth.PI / 4.0F);
				case 6 -> PartPose.offsetAndRotation(-7.5F, -1.5F, 3.5F, 0.0F, 0.0F, Mth.PI / 3.0F);
				case 7 -> PartPose.offsetAndRotation(7.5F, 3.5F, -1.0F, 0.0F, 0.0F, -Mth.PI / 4.0F);
				case 8 -> PartPose.offsetAndRotation(7.5F, -1.5F, 3.5F, 0.0F, 0.0F, -Mth.PI / 3.0F);
				default -> {
					TwilightForestMod.LOGGER.warn("Out of bounds with Ur-Ghast Trophy limb creation: Iteration {}", iteration);
					yield PartPose.ZERO;
				}
			});

		var tentacleExtension = tentacleBase.addOrReplaceChild(name + "_extension", CubeListBuilder.create()
				.texOffs(0, 3)
				.addBox(-1.5F, 1.0F, -1.5F, 3.0F, 4.0F, 3.0F),
			PartPose.offset(0.0F, 4.0F, 0.0F));

		var tentacleExtension2 = tentacleExtension.addOrReplaceChild(name + "_extension_2", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-1.5F, 1.0F, -1.5F, 3.0F, 4.0F, 3.0F),
			PartPose.offset(0.0F, 4.0F, 0.0F));

		tentacleExtension2.addOrReplaceChild(name + "_tip", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-1.5F, 1.0F, -1.5F, 3.0F, 4.0F, 3.0F),
			PartPose.offset(0.0F, 4.0F, 0.0F));

	}

	@Override
	public void setupAnim(TFGhastRenderState state) {
		super.setupAnim(state);

		// wave tentacles
		this.waveTentacles(state.walkAnimationSpeed, state.ageInTicks);
	}

	private void waveTentacles(float limbSwingAmount, float ageInTicks) {
		for (int i = 0; i < this.tentacles.length; ++i) {

			float wiggle = Math.min(limbSwingAmount, 0.6F);

			float time = (ageInTicks + (i * 9.0F)) / 2.0F;

			this.tentacles[i][0].xRot = (Mth.cos(time * 0.6662F) - Mth.PI / 3.0F) * wiggle;
			this.tentacles[i][1].xRot = Mth.cos(time * 0.7774F) * 1.2F * wiggle;
			this.tentacles[i][2].xRot = Mth.cos(time * 0.8886F + Mth.PI / 2.0F) * 1.4F * wiggle;
			this.tentacles[i][3].xRot = Mth.cos(time * 0.9998F + Mth.PI / 4.0F) * 1.6F * wiggle;

			this.tentacles[i][0].xRot = 0.2F + Mth.cos(time * 0.3335F) * 0.15F;
			this.tentacles[i][1].xRot = 0.1F + Mth.cos(time * 0.4445F) * 0.20F;
			this.tentacles[i][2].xRot = 0.1F + Mth.cos(time * 0.5555F) * 0.25F;
			this.tentacles[i][3].xRot = 0.1F + Mth.cos(time * 0.6665F) * 0.30F;

			float yTwist = 0.4F;

			this.tentacles[i][0].yRot = yTwist * Mth.sin(time * 0.3F);
		}
	}

	@Override
	public void setupRotationsForTrophy(float x, float y, float z, float mouthAngle) {
		this.body.yRot = y * Mth.DEG_TO_RAD;
		this.body.xRot = z * Mth.DEG_TO_RAD;
		this.waveTentacles(x, x * 0.2F);
	}

	@Override
	public void renderTrophy(PoseStack stack, MultiBufferSource buffer, int light, int overlay, int color, ItemDisplayContext context) {
		if (context == ItemDisplayContext.NONE) {
			stack.translate(0.0F, -1.0F, 0.0F);
		}
		stack.scale(0.5F, 0.5F, 0.5F);
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(UrGhastRenderer.TEXTURE));
		this.body.render(stack, consumer, light, overlay, color);
	}
}
