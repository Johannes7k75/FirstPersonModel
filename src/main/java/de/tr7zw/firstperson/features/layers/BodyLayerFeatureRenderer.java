package de.tr7zw.firstperson.features.layers;

import de.tr7zw.firstperson.FirstPersonModelMod;
import de.tr7zw.firstperson.PlayerSettings;
import de.tr7zw.firstperson.accessor.PlayerEntityModelAccessor;
import de.tr7zw.firstperson.features.LayerMode;
import de.tr7zw.firstperson.render.SolidPixelModelPart;
import de.tr7zw.firstperson.render.SolidPixelWrapper;
import de.tr7zw.firstperson.util.SkinUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class BodyLayerFeatureRenderer 
extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public BodyLayerFeatureRenderer(
		FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
			super(featureRendererContext);
			thinArms = ((PlayerEntityModelAccessor)featureRendererContext.getModel()).hasThinArms();
	}

	private final boolean thinArms;
	
	private static int entityCounter = 0;

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
			AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k,
			float l) {
		if (!abstractClientPlayerEntity.hasSkinTexture() || abstractClientPlayerEntity.isInvisible()) {
			return;
		}
		if (!isEnabled(abstractClientPlayerEntity)) {
			return;
		}

		PlayerSettings settings = (PlayerSettings) abstractClientPlayerEntity;
		// check for it being setup first to speedup the rendering
		if(settings.getSkinLayers() == null && !setupModel(abstractClientPlayerEntity, settings)) {
			return; // no head layer setup and wasn't able to setup
		}
		
		VertexConsumer vertexConsumer = vertexConsumerProvider
				.getBuffer(RenderLayer.getEntityCutout((Identifier) abstractClientPlayerEntity.getSkinTexture()));
		int m = LivingEntityRenderer.getOverlay((LivingEntity) abstractClientPlayerEntity, (float) 0.0f);
		renderLayers(abstractClientPlayerEntity, settings.getSkinLayers(), matrixStack, vertexConsumer, i, m);
	}
	
	private boolean setupModel(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerSettings settings) {
		if(!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
			return false; // default skin
		}
		NativeImage skin = SkinUtil.getSkinTexture(abstractClientPlayerEntity);
		SolidPixelModelPart[] layers = new SolidPixelModelPart[5];
		layers[0] = SolidPixelWrapper.wrapBox(this.getContextModel(), 4, 12, 4, 0, 48, true, 0);
		layers[1] = SolidPixelWrapper.wrapBox(this.getContextModel(), 4, 12, 4, 0, 32, true, 0);
		if(thinArms) {
			layers[2] = SolidPixelWrapper.wrapBox(this.getContextModel(), 3, 12, 4, 48, 48, true, -2);
			layers[3] = SolidPixelWrapper.wrapBox(this.getContextModel(), 3, 12, 4, 40, 32, true, -2);
		} else {
			layers[2] = SolidPixelWrapper.wrapBox(this.getContextModel(), 4, 12, 4, 48, 48, true, -2);
			layers[3] = SolidPixelWrapper.wrapBox(this.getContextModel(), 4, 12, 4, 40, 32, true, -2);
		}
		layers[4] = SolidPixelWrapper.wrapBox(this.getContextModel(), 8, 12, 4, 16, 32, true, 0);
		settings.setupSkinLayers(layers);
		skin.untrack();
		return true;
	}

	public void renderLayers(AbstractClientPlayerEntity abstractClientPlayerEntity, SolidPixelModelPart[] layers, MatrixStack matrixStack, VertexConsumer vertices, int light, int overlay) {
		float pixelScaling = 1.16f; //1.125f
		SolidPixelModelPart leftLeg = layers[0];
		SolidPixelModelPart rightLeg = layers[1];
		SolidPixelModelPart leftArm = layers[2];
		SolidPixelModelPart rightArm = layers[3];
		SolidPixelModelPart jacket = layers[4];
		// Left leg
		if(abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG)) {
			matrixStack.push();
			leftLeg.customCopyPositionAndRotation(this.getContextModel().leftLeg);
			leftLeg.pivotY -= 2.625f;
			matrixStack.scale(pixelScaling, pixelScaling, pixelScaling);
			if(abstractClientPlayerEntity.isSneaking()) {
				matrixStack.translate(0, 0, -0.03f);
			}
			leftLeg.customRender(matrixStack, vertices, light, overlay);
			matrixStack.pop();
		}
		// Right leg
		if(abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG)) {
			matrixStack.push();
			rightLeg.customCopyPositionAndRotation(this.getContextModel().rightLeg);
			rightLeg.pivotY -= 2.625f;
			matrixStack.scale(pixelScaling, pixelScaling, pixelScaling);
			if(abstractClientPlayerEntity.isSneaking()) {
				matrixStack.translate(0, 0, -0.03f);
			}
			rightLeg.customRender(matrixStack, vertices, light, overlay);
			matrixStack.pop();
		}
		// Left Arm
		if(abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE)) {
			matrixStack.push();
			leftArm.customCopyPositionAndRotation(this.getContextModel().leftArm);
			leftArm.pivotY -= 0.825f;
			leftArm.pivotX -= 0.02f;
			matrixStack.scale(pixelScaling, pixelScaling, pixelScaling);
			if(abstractClientPlayerEntity.isSneaking()) {
				matrixStack.translate(0, 0, 0.01f);
			}
			leftArm.customRender(matrixStack, vertices, light, overlay);
			matrixStack.pop();
		}
		// Right Arm
		if(abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE)) {
			matrixStack.push();
			rightArm.customCopyPositionAndRotation(this.getContextModel().rightArm);
			rightArm.pivotY -= 0.825f;
			rightArm.pivotX += 0.02f;
			matrixStack.scale(pixelScaling, pixelScaling, pixelScaling);
			if(abstractClientPlayerEntity.isSneaking()) {
				matrixStack.translate(0, 0, 0.01f);
			}
			rightArm.customRender(matrixStack, vertices, light, overlay);
			matrixStack.pop();
		}
		// jacket
		if(abstractClientPlayerEntity.isPartVisible(PlayerModelPart.JACKET)) {
			matrixStack.push();
			jacket.customCopyPositionAndRotation(this.getContextModel().jacket);
			jacket.pivotY -= 1f;
			matrixStack.scale(pixelScaling, pixelScaling, pixelScaling);
			if(abstractClientPlayerEntity.isSneaking()) {
				matrixStack.translate(0, 0, -0.025f);
			}
			jacket.customRender(matrixStack, vertices, light, overlay);
			matrixStack.pop();
		}
		
	}
	
	public static boolean isEnabled(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		LayerMode mode = FirstPersonModelMod.config.skinLayerMode;
		if (mode == LayerMode.VANILLA2D)
			return false;
		ClientPlayerEntity thePlayer = MinecraftClient.getInstance().player;
		if (thePlayer == abstractClientPlayerEntity) {
			entityCounter = 0;
			return true;
		}
		//if(entityCounter > FirstPersonModelMod.config.layerLimiter)return false;
		if (mode != LayerMode.ONLYSELF) {
			int distance = FirstPersonModelMod.config.optimizedLayerDistance
					* FirstPersonModelMod.config.optimizedLayerDistance;
			boolean ret = thePlayer.getPos().squaredDistanceTo(abstractClientPlayerEntity.getPos()) < distance;
			if(ret)entityCounter++;
			return ret;
		}
		return false;
	}

}
