// Date: 11/5/2012 7:35:56 PM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX
package twilightforest.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import twilightforest.client.JappaPackReloadListener;
import twilightforest.client.state.PinchBeetleRenderState;

public class PinchBeetleModel extends EntityModel<PinchBeetleRenderState> {

	private final ModelPart head;
	private final ModelPart rightLeg1;
	private final ModelPart rightLeg2;
	private final ModelPart rightLeg3;
	private final ModelPart leftLeg1;
	private final ModelPart leftLeg2;
	private final ModelPart leftLeg3;
	private final ModelPart rightPincer;
	private final ModelPart leftPincer;

	public PinchBeetleModel(ModelPart root) {
		super(root);

		this.head = root.getChild("head");

		this.leftPincer = this.head.getChild("left_pincher");
		this.rightPincer = this.head.getChild("right_pincher");

		this.rightLeg1 = root.getChild("right_leg_1");
		this.rightLeg2 = root.getChild("right_leg_2");
		this.rightLeg3 = root.getChild("right_leg_3");

		this.leftLeg1 = root.getChild("left_leg_1");
		this.leftLeg2 = root.getChild("left_leg_2");
		this.leftLeg3 = root.getChild("left_leg_3");
	}

	public static LayerDefinition checkForPack() {
		return JappaPackReloadListener.INSTANCE.isJappaPackLoaded() ? createJappaModel() : create();
	}

	private static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		var head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-4.0F, -4.0F, -6.0F, 8.0F, 6.0F, 6.0F),
			PartPose.offset(0.0F, 19.0F, -5.0F));

		head.addOrReplaceChild("right_antenna", CubeListBuilder.create()
				.texOffs(42, 4)
				.addBox(0.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(1.0F, -3.0F, -5.0F, 0.0F, 1.047198F, -0.296706F));

		head.addOrReplaceChild("left_antenna", CubeListBuilder.create()
				.texOffs(42, 4)
				.addBox(0.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(-1.0F, -3.0F, -5.0F, 0.0F, 2.094395F, 0.296706F));

		head.addOrReplaceChild("right_eye", CubeListBuilder.create()
				.texOffs(15, 12)
				.addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
			PartPose.offset(-3.0F, -2.0F, -5.0F));

		head.addOrReplaceChild("left_eye", CubeListBuilder.create()
				.texOffs(15, 12)
				.addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
			PartPose.offset(3.0F, -2.0F, -5.0F));

		var rightJaw = head.addOrReplaceChild("right_pincher", CubeListBuilder.create()
				.texOffs(40, 6)
				.addBox(-1.0F, -1.0F, -1.5F, 8.0F, 2.0F, 3.0F),
			PartPose.offsetAndRotation(-3.0F, 1.0F, -6.0F, 0.0F, 2.6354471F, 0.0F));

		var rightJawTooth = rightJaw.addOrReplaceChild("right_pincher_top", CubeListBuilder.create()
				.texOffs(40, 10)
				.addBox(-1.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(7.0F, 0.0F, 0.0F, 0.0F, -1.047197F, 0.0F));

		rightJawTooth.addOrReplaceChild("right_tooth_1", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, -0.5235987F, 0.0F));

		rightJawTooth.addOrReplaceChild("right_tooth_2", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 1.5707963F, 0.0F));

		rightJawTooth.addOrReplaceChild("right_tooth_3", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0F, -0.5F, -0F, 2, 1, 1),
			PartPose.offsetAndRotation(3F, 0.0F, 0.0F, 0.0F, 1.5707963F, 0));

		var leftJaw = head.addOrReplaceChild("left_pincher", CubeListBuilder.create()
				.texOffs(40, 6)
				.addBox(-1.0F, -1.0F, -1.5F, 8.0F, 2.0F, 3.0F),
			PartPose.offsetAndRotation(3.0F, 1.0F, -6.0F, 0.0F, 0.5410520F, 0.0F));

		var leftJawTooth = leftJaw.addOrReplaceChild("left_pincher_top", CubeListBuilder.create()
				.texOffs(40, 10)
				.addBox(-1.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(7.0F, 0.0F, 0.0F, 0.0F, 1.047197F, 0.0F));

		leftJawTooth.addOrReplaceChild("left_tooth_1", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(8.0F, 0.0F, -1.0F, 0.0F, 0.5235987F, 0.0F));

		leftJawTooth.addOrReplaceChild("left_tooth_2", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(7.0F, 0.0F, 0.0F, 0.0F, -1.5707963F, 0.0F));

		leftJawTooth.addOrReplaceChild("left_tooth_3", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.0F, -1.5707963F, 0.0F));

		partdefinition.addOrReplaceChild("thorax", CubeListBuilder.create()
				.texOffs(0, 22)
				.addBox(-4.5F, -4.0F, 0.0F, 9.0F, 8.0F, 2.0F),
			PartPose.offset(0.0F, 18.0F, -4.5F));

		partdefinition.addOrReplaceChild("connector_1", CubeListBuilder.create()
				.texOffs(0, 12)
				.addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 1.0F),
			PartPose.offset(0.0F, 18.0F, -3.0F));

		partdefinition.addOrReplaceChild("connector_2", CubeListBuilder.create()
				.texOffs(0, 12)
				.addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 1.0F),
			PartPose.offset(0.0F, 18.0F, -4.0F));

		partdefinition.addOrReplaceChild("rear", CubeListBuilder.create()
				.texOffs(28, 14)
				.addBox(-5.0F, -9.0F, -4.0F, 10.0F, 10.0F, 8.0F),
			PartPose.offsetAndRotation(0.0F, 18.0F, 7.0F, 1.570796F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg_1", CubeListBuilder.create().mirror()
				.texOffs(40, 0)
				.addBox(-9.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(-4.0F, 21.0F, 4.0F, 0.0F, 0.6981317F, -0.3490659F));

		partdefinition.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(-1.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(4F, 21F, 4F, 0.0F, -0.6981317F, 0.3490659F));

		partdefinition.addOrReplaceChild("right_leg_2", CubeListBuilder.create().mirror()
				.texOffs(40, 0)
				.addBox(-9F, -1.0F, -1.0F, 10, 2, 2),
			PartPose.offsetAndRotation(-4F, 21F, -1.0F, 0.0F, 0.2792527F, -0.3490659F));

		partdefinition.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(-1.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(4.0F, 21.0F, -1.0F, 0.0F, -0.2792527F, 0.3490659F));

		partdefinition.addOrReplaceChild("right_leg_3", CubeListBuilder.create().mirror()
				.texOffs(40, 0)
				.addBox(-9.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(-4.0F, 21.0F, -4.0F, 0.0F, -0.2792527F, -0.3490659F));

		partdefinition.addOrReplaceChild("left_leg_3", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(-1.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(4.0F, 21.0F, -4.0F, 0.0F, 0.2792527F, 0.3490659F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	private static LayerDefinition createJappaModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		var head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-4.0F, -3.0F, -6.0F, 8.0F, 6.0F, 6.0F),
			PartPose.offset(0.0F, 19.0F, 0.0F));

		head.addOrReplaceChild("left_antenna", CubeListBuilder.create()
				.texOffs(52, 0)
				.addBox(0.0F, 0.0F, -10.0F, 1.0F, 0.0F, 10.0F),
			PartPose.offsetAndRotation(1.0F, -3.0F, -6.0F, -0.4363323129985824F, -0.4363323129985824F, 0.0F));

		head.addOrReplaceChild("right_antenna", CubeListBuilder.create()
				.texOffs(48, 0).addBox(-1.0F, 0.0F, -10.0F, 1.0F, 0.0F, 10.0F),
			PartPose.offsetAndRotation(-1.0F, -3.0F, -6.0F, -0.4363323129985824F, 0.4363323129985824F, 0.0F));

		head.addOrReplaceChild("left_pincher", CubeListBuilder.create()
				.texOffs(16, 14)
				.addBox(0.0F, 0.0F, -12.0F, 12.0F, 2.0F, 12.0F),
			PartPose.offsetAndRotation(4.0F, 2.0F, -4.0F, 0.08726646259971647F, 0.6108652381980153F, 0.0F));

		head.addOrReplaceChild("right_pincher", CubeListBuilder.create()
				.texOffs(16, 0)
				.addBox(-12.0F, 0.0F, -12.0F, 12.0F, 2.0F, 12.0F),
			PartPose.offsetAndRotation(-4.0F, 2.0F, -4.0F, 0.08726646259971647F, -0.6108652381980153F, 0.0F));

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 28)
				.addBox(-5.0F, -8.0F, -3.0F, 10.0F, 10.0F, 7.0F),
			PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, 1.5707963267948966F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(40, 28)
				.addBox(-10.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(-2.0F, 21.0F, 6.0F, 0.0F, 0.6108652381980153F, -0.17453292519943295F));

		partdefinition.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(40, 32)
				.addBox(-10.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(-2.0F, 21.0F, 4.0F, 0.0F, 0.20943951023931953F, -0.17453292519943295F));

		partdefinition.addOrReplaceChild("right_leg_3", CubeListBuilder.create()
				.texOffs(40, 36)
				.addBox(-10.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(-2.0F, 21.0F, 2.0F, 0.0F, -0.20943951023931953F, -0.17453292519943295F));

		partdefinition.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(40, 42)
				.addBox(0.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(2.0F, 21.0F, 6.0F, 0.0F, -0.6108652381980153F, 0.17453292519943295F));

		partdefinition.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(40, 46)
				.addBox(0.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(2.0F, 21.0F, 4.0F, 0.0F, -0.20943951023931953F, 0.17453292519943295F));

		partdefinition.addOrReplaceChild("left_leg_3", CubeListBuilder.create()
				.texOffs(40, 50)
				.addBox(0.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F),
			PartPose.offsetAndRotation(2.0F, 21.0F, 2.0F, 0.0F, 0.20943951023931953F, 0.17453292519943295F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(PinchBeetleRenderState state) {
		super.setupAnim(state);
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;

		float legZ = (Mth.PI / 11.0F);
		this.leftLeg1.zRot = legZ;
		this.rightLeg1.zRot = -legZ;
		this.leftLeg2.zRot = legZ * 0.74F;
		this.rightLeg2.zRot = -legZ * 0.74F;
		this.leftLeg3.zRot = legZ;
		this.rightLeg3.zRot = -legZ;

		float var9 = -0.0F;
		float var10 = 0.3926991F;
		this.leftLeg1.yRot = -var10 * 2.0F + var9;
		this.rightLeg1.yRot = var10 * 2.0F - var9;
		this.leftLeg2.yRot = var10 + var9;
		this.rightLeg2.yRot = -var10 - var9;
		this.leftLeg3.yRot = var10 * 2.0F + var9;
		this.rightLeg3.yRot = -var10 * 2.0F - var9;

		float var11 = -(Mth.cos(state.walkAnimationPos * 0.6662F * 2.0F + 0.0F) * 0.4F) * state.walkAnimationSpeed;
		float var12 = -(Mth.cos(state.walkAnimationPos * 0.6662F * 2.0F + Mth.PI) * 0.4F) * state.walkAnimationSpeed;
		float var14 = -(Mth.cos(state.walkAnimationPos * 0.6662F * 2.0F + (Mth.PI * 3.0F / 2.0F)) * 0.4F) * state.walkAnimationSpeed;

		float var15 = Math.abs(Mth.sin(state.walkAnimationPos * 0.6662F + 0.0F) * 0.4F) * state.walkAnimationSpeed;
		float var16 = Math.abs(Mth.sin(state.walkAnimationPos * 0.6662F + Mth.PI) * 0.4F) * state.walkAnimationSpeed;
		float var18 = Math.abs(Mth.sin(state.walkAnimationPos * 0.6662F + (Mth.PI * 3.0F / 2.0F)) * 0.4F) * state.walkAnimationSpeed;

		this.leftLeg1.yRot += var11;
		this.rightLeg1.yRot -= var11;
		this.leftLeg2.yRot += var12;
		this.rightLeg2.yRot -= var12;
		this.leftLeg3.yRot += var14;
		this.rightLeg3.yRot -= var14;

		this.leftLeg1.zRot += var15;
		this.rightLeg1.zRot -= var15;

		this.leftLeg2.zRot += var16;
		this.rightLeg2.zRot -= var16;

		this.leftLeg3.zRot += var18;
		this.rightLeg3.zRot -= var18;

		if (state.isHoldingVictim) {
			// open jaws
			this.rightPincer.yRot = -(JappaPackReloadListener.INSTANCE.isJappaPackLoaded() ? 20.0F : 170.0F) * Mth.DEG_TO_RAD;
			this.leftPincer.yRot = 20.0F * Mth.DEG_TO_RAD;
		} else {
			// close jaws
			this.rightPincer.yRot = (JappaPackReloadListener.INSTANCE.isJappaPackLoaded() ? -45.0F : 135.0F) * Mth.DEG_TO_RAD;
			this.leftPincer.yRot = 45.0F * Mth.DEG_TO_RAD;
		}
	}
}
