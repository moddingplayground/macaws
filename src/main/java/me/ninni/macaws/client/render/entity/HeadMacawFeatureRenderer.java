package me.ninni.macaws.client.render.entity;

import me.ninni.macaws.client.model.MacawEntityModelLayers;
import me.ninni.macaws.client.model.entity.MacawEntityModel;
import me.ninni.macaws.entity.MacawEntity;
import me.ninni.macaws.entity.MacawsEntities;
import me.ninni.macaws.entity.access.HeadMountAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import static me.ninni.macaws.client.render.entity.MacawEntityRenderer.*;
import static me.ninni.macaws.util.MacawsNbtConstants.*;

@Environment(EnvType.CLIENT)
public class HeadMacawFeatureRenderer<T extends PlayerEntity, E extends MacawEntity>
    extends FeatureRenderer<T, PlayerEntityModel<T>> {
    private final MacawEntityModel<E> model;

    public HeadMacawFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        this.model = new MacawEntityModel<>(loader.getModelPart(MacawEntityModelLayers.MACAW));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        NbtCompound nbt = ((HeadMountAccess)entity).getHeadEntity();
        if (EntityType.get(nbt.getString("id")).filter(type -> type == MacawsEntities.MACAW).isPresent()) {
            matrices.push();

            this.getContextModel().head.rotate(matrices);
            matrices.translate(0.0D, -1.9D, 0.0D);

            MacawEntity.Variant variant = MacawEntity.Variant.readFromNbt(nbt);
            this.model.renderOnHead(matrices, this.getVertex(vertices, variant.getTexture()), light, OverlayTexture.DEFAULT_UV);

            if (nbt.getBoolean(NBT_HAS_EYEPATCH)) this.model.renderOnHead(matrices, this.getVertex(vertices, TEXTURE_EYEPATCH), light, OverlayTexture.DEFAULT_UV);
            if (nbt.contains(NBT_RING_COLOR)) {
                float[] col = DyeColor.byId(nbt.getInt(NBT_RING_COLOR)).getColorComponents();
                this.model.renderOnHead(matrices, this.getVertex(vertices, TEXTURE_RING), light, OverlayTexture.DEFAULT_UV, col[0], col[1], col[2]);
            }

            matrices.pop();
        }
    }

    public VertexConsumer getVertex(VertexConsumerProvider vertices, Identifier texture) {
        return vertices.getBuffer(this.model.getLayer(texture));
    }
}
