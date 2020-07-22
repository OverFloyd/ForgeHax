package dev.fiki.forgehax.main.util.draw;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import dev.fiki.forgehax.main.util.color.Color;
import dev.fiki.forgehax.main.util.math.AlignHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

import static com.mojang.blaze3d.systems.RenderSystem.*;
import static dev.fiki.forgehax.main.Common.MC;
import static dev.fiki.forgehax.main.Common.getFontRenderer;
import static dev.fiki.forgehax.main.util.math.AlignHelper.getFlowDirY2;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

/**
 * 2D rendering
 */
public class SurfaceHelper {
  public static BufferBuilderEx getBufferBuilder(Tessellator tessellator) {
    return new BufferBuilderEx(tessellator);
  }

  public static BufferBuilderEx getDefaultBufferBuilder() {
    return getBufferBuilder(Tessellator.getInstance());
  }

  static void _rect(BufferBuilder builder,
      double x, double y, double w, double h,
      Color color) {
    builder.pos(x, y, 0.0D)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex();
    builder.pos(x, y + h, 0.0D)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex();
    builder.pos(x + w, y + h, 0.0D)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex();
    builder.pos(x + w, y, 0.0D)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex();
  }

  public static void rect(BufferBuilder builder,
      float x, float y, float w, float h,
      Color color) {
    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    _rect(builder, x, y, w, h, color);
    builder.finishDrawing();
  }

  public static void outlinedRect(BufferBuilder builder,
      float x, float y, float w, float h,
      Color color) {
    builder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
    _rect(builder, x, y, w, h, color);
    builder.finishDrawing();
  }

  public static void texturedRect(BufferBuilder builder,
      float x, float y,
      float textureX, float textureY,
      float width, float height,
      float depth) {
    builder.begin(7, DefaultVertexFormats.POSITION_TEX);
    builder.pos(x, y + height, depth)
        .tex(textureX + 0, textureY + height)
        .endVertex();
    builder.pos(x + width, y + height, depth)
        .tex(textureX + width, textureY + height)
        .endVertex();
    builder.pos(x + width, y + 0, depth)
        .tex(textureX + width, textureY + 0)
        .endVertex();
    builder.pos(x + 0, y + 0, depth)
        .tex(textureX + 0, textureY + 0)
        .endVertex();
    builder.finishDrawing();
  }

  public static void line(BufferBuilder builder,
      float startX, float startY, float endX, float endY,
      Color color) {
    builder.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
    builder.pos(startX, startY, 0.0D)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex();
    builder.pos(endX, endY, 0.0D)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex();
    builder.finishDrawing();
  }

  public static void renderString(@Nonnull IRenderTypeBuffer buffer,
      @Nonnull Matrix4f matrix4f,
      @Nonnull String text,
      float x, float y,
      Color color,
      boolean shadow) {
    getFontRenderer().renderString(text,
        Math.round(x), Math.round(y),
        color.toBuffer(), shadow,
        matrix4f, buffer,
        false, 0, 15728880);
  }

  public static void renderString(@Nonnull IRenderTypeBuffer buffer,
      @Nonnull String text,
      float x, float y,
      Color color,
      boolean shadow) {
    renderString(buffer, TransformationMatrix.identity().getMatrix(), text, x, y, color, shadow);
  }

  public static IRenderTypeBuffer.Impl renderString(@Nonnull BufferBuilder builder,
      @Nonnull Matrix4f matrix4f,
      @Nonnull String text,
      float x, float y,
      Color color,
      boolean shadow) {
    IRenderTypeBuffer.Impl render = IRenderTypeBuffer.getImpl(builder);
    renderString(render, matrix4f, text, x, y, color, shadow);
    return render;
  }

  public static void renderString(@Nonnull BufferBuilder builder,
      @Nonnull String text,
      float x, float y,
      Color color,
      boolean shadow) {
    renderString(builder, TransformationMatrix.identity().getMatrix(), text, x, y, color, shadow);
  }

  public static double getStringWidth(String text) {
    return getFontRenderer().getStringWidth(text);
  }

  public static double getStringHeight() {
    return getFontRenderer().FONT_HEIGHT;
  }

  public static void drawText(String msg, float x, float y, int color, boolean shadow) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
    Matrix4f matrix4f = TransformationMatrix.identity().getMatrix();

    renderString(builder, matrix4f, msg, x, y, Color.of(color), shadow).finish();
  }

  public static void drawText(String msg, float x, float y, int color) {
    drawText(msg, x, y, color, false);
  }

  public static void drawTextShadow(String msg, float x, float y, int color) {
    drawText(msg, x, y, color, true);
  }

  public static void drawTextShadowCentered(String msg, float x, float y, int color) {
    float offsetX = getTextWidth(msg) / 2f;
    float offsetY = getTextHeight() / 2f;
    drawTextShadow(msg, x - offsetX, y - offsetY, color);
  }

  public static void drawTextAlignH(String msg, float x, float y, int color, boolean shadow, int alignmask) {
    final int offsetX = AlignHelper.alignH(getTextWidth(msg), alignmask);
    drawText(msg, x - offsetX, y, color, shadow);
  }

  public static void drawTextShadowAlignH(String msg, int x, int y, int color, int alignmask) {
    drawTextAlignH(msg, x, y, color, true, alignmask);
  }

  public static void drawTextAlign(String msg, int x, int y, int color, boolean shadow, int alignmask) {
    final int offsetX = AlignHelper.alignH(getTextWidth(msg), alignmask);
    final int offsetY = AlignHelper.alignV(getTextHeight(), alignmask);
    drawText(msg, x - offsetX, y - offsetY, color, shadow);
  }

  public static void drawTextShadowAlign(String msg, int x, int y, int color, int alignmask) {
    drawTextAlign(msg, x, y, color, true, alignmask);
  }

  public static void drawTextAlign(String msg, int x, int y, int color, double scale, boolean shadow, int alignmask) {
    final int offsetX = AlignHelper.alignH((int) (getTextWidth(msg) * scale), alignmask);
    final int offsetY = AlignHelper.alignV((int) (getTextHeight() * scale), alignmask);
    if (scale != 1.0d) {
      drawText(msg, x - offsetX, y - offsetY, color, scale, shadow);
    } else {
      drawText(msg, x - offsetX, y - offsetY, color, shadow);
    }
  }

  public static void drawTextAlign(List<String> msgList, int x, int y, int color, double scale, boolean shadow, int alignmask) {
    pushMatrix();
    disableDepthTest();
    scaled(scale, scale, scale);

    final int offsetY = AlignHelper.alignV((int) (getTextHeight() * scale), alignmask);
    final int height = (int) (getFlowDirY2(alignmask) * (getTextHeight() + 1) * scale);
    final float invScale = (float) (1 / scale);

    for (int i = 0; i < msgList.size(); i++) {
      final int offsetX = AlignHelper.alignH((int) (getTextWidth(msgList.get(i)) * scale), alignmask);

      drawText(msgList.get(i),
          (int) ((x - offsetX) * invScale), (int) ((y - offsetY + height * i) * invScale),
          color, shadow);
    }

    disableDepthTest();
    popMatrix();
  }

  public static void drawText(String msg, int x, int y, int color, double scale, boolean shadow) {
    disableDepthTest();
    scaled(scale, scale, scale);
    drawText(msg, x, y, color, shadow);
  }

  public static void drawText(String msg, int x, int y, int color, double scale) {
    drawText(msg, x, y, color, scale, false);
  }

  public static void drawTextShadow(String msg, int x, int y, int color, double scale) {
    drawText(msg, x, y, color, scale, true);
  }

  @Deprecated
  public static int getTextWidth(String text, double scale) {
    return (int) (getStringWidth(text) * scale);
  }

  @Deprecated
  public static int getTextWidth(String text) {
    return getTextWidth(text, 1.D);
  }

  @Deprecated
  public static int getTextHeight() {
    return (int) getStringHeight();
  }

  @Deprecated
  public static int getTextHeight(double scale) {
    return (int) (getStringHeight() * scale);
  }

  public static void drawItem(ItemStack item, int x, int y) {
    MC.getItemRenderer().renderItemAndEffectIntoGUI(item, x, y);
  }

  public static void drawItemOverlay(ItemStack stack, int x, int y) {
    MC.getItemRenderer().renderItemOverlayIntoGUI(getFontRenderer(), stack, x, y, null);
  }

  public static void setItemRendererDepth(float depth) {
    MC.getItemRenderer().zLevel = depth;
  }

  private static void renderModel(IBakedModel modelIn, ItemStack stack,
      int combinedLightIn, int combinedOverlayIn,
      MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
    Random random = new Random();
    long i = 42L;

    for (Direction direction : Direction.values()) {
      random.setSeed(42L);
      MC.getItemRenderer().renderQuads(matrixStackIn, bufferIn,
          modelIn.getQuads(null, direction, random), stack, combinedLightIn, combinedOverlayIn);
    }

    random.setSeed(42L);
    MC.getItemRenderer().renderQuads(matrixStackIn, bufferIn,
        modelIn.getQuads(null, null, random), stack, combinedLightIn, combinedOverlayIn);
  }

  private static RenderType getRenderType(ItemStack itemStackIn) {
    Item item = itemStackIn.getItem();
    if (item instanceof BlockItem) {
      Block block = ((BlockItem) item).getBlock();
      return RenderTypeLookup.canRenderInLayer(block.getDefaultState(), RenderType.getTranslucent())
          ? RenderTypeEx.blockTranslucentCull()
          : RenderTypeEx.blockCutout();
    } else {
      return RenderTypeEx.blockTranslucentCull();
    }
  }

  private static IVertexBuilder getBuffer(IRenderTypeBuffer bufferIn, RenderType renderTypeIn, boolean isItemIn, boolean glintIn) {
    return glintIn ? VertexBuilderUtils.newDelegate(
        bufferIn.getBuffer(isItemIn
            ? RenderType.getGlint()
            : RenderType.getEntityGlint()),
        bufferIn.getBuffer(renderTypeIn))
        : bufferIn.getBuffer(renderTypeIn);
  }

  public static void renderItem(LivingEntity living, ItemStack itemStack, MatrixStack stack, IRenderTypeBuffer buffer) {
    stack.push();

    IBakedModel model = MC.getItemRenderer().getItemModelWithOverrides(itemStack, living.world, living);

    if (Items.TRIDENT.equals(itemStack.getItem())) {
      model = MC.getItemRenderer().getItemModelMesher().getModelManager()
          .getModel(new ModelResourceLocation("minecraft:trident#inventory"));
    }

    model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(stack, model,
        ItemCameraTransforms.TransformType.GUI, false);

    stack.translate(-0.5D, -0.5D, -0.5D);
    if (!model.isBuiltInRenderer()) {
      RenderType rendertype = getRenderType(itemStack);

      IVertexBuilder builder = ItemRenderer.getBuffer(buffer, rendertype, true, itemStack.hasEffect());
      renderModel(model, itemStack, 15728880, OverlayTexture.NO_OVERLAY, stack, builder);
    } else {
      itemStack.getItem().getItemStackTileEntityRenderer().func_239207_a_(itemStack,
          ItemCameraTransforms.TransformType.NONE,
          stack, buffer,
          15728880, OverlayTexture.NO_OVERLAY);
    }

    stack.pop();
  }

  public static void drawScaledCustomSizeModalRect(double x, double y,
      float u, float v,
      double uWidth, double vHeight,
      double width, double height,
      double tileWidth, double tileHeight) {
    float f = 1.0F / (float) tileWidth;
    float f1 = 1.0F / (float) tileHeight;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferbuilder
        .pos(x, y + height, 0.0D)
        .tex(u * f, (v + (float) vHeight) * f1)
        .endVertex();
    bufferbuilder
        .pos(x + width, y + height, 0.0D)
        .tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1)
        .endVertex();
    bufferbuilder
        .pos(x + width, y, 0.0D)
        .tex((u + (float) uWidth) * f, v * f1)
        .endVertex();
    bufferbuilder
        .pos(x, y, 0.0D)
        .tex(u * f, v * f1)
        .endVertex();
    tessellator.draw();
  }

  @Deprecated
  public static void drawRect(int x, int y, int i, int height, int toBuffer) {
    throw new UnsupportedOperationException("TODO 1.16"); // TODO: 1.16
//    AbstractGui.func_238463_a_(TransformationMatrix.identity(), x, y, i, height, toBuffer);
  }

  @Deprecated
  public static void drawOutlinedRect(int x, int y, int width, int height, int color) {
    float f3 = (float) (color >> 24 & 255) / 255.0F;
    float f = (float) (color >> 16 & 255) / 255.0F;
    float f1 = (float) (color >> 8 & 255) / 255.0F;
    float f2 = (float) (color & 255) / 255.0F;
    Matrix4f matrix = TransformationMatrix.identity().getMatrix();
    BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
    RenderSystem.enableBlend();
    RenderSystem.disableTexture();
    RenderSystem.defaultBlendFunc();
    bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
    bufferbuilder.pos(matrix, (float) x, (float) y, 0.0F).color(f, f1, f2, f3).endVertex();
    bufferbuilder.pos(matrix, (float) x, (float) y + height, 0.0F).color(f, f1, f2, f3).endVertex();
    bufferbuilder.pos(matrix, (float) x + width, (float) y + height, 0.0F).color(f, f1, f2, f3).endVertex();
    bufferbuilder.pos(matrix, (float) x + width, (float) y, 0.0F).color(f, f1, f2, f3).endVertex();
    bufferbuilder.finishDrawing();
    WorldVertexBufferUploader.draw(bufferbuilder);
    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }

  @Deprecated
  public static void texturedRect(int x, int y, int i, int i1, int i2, int i3, int depth) {
    drawRect(x, y, i, i1, depth);
  }

  @Deprecated
  public static void drawOutlinedRectShaded(int x, int y, int w, int h, int colorOutline, int shade, float width) {
    float f = (float) (colorOutline >> 24 & 255) / 255.0F;
    float f1 = (float) (colorOutline >> 16 & 255) / 255.0F;
    float f2 = (float) (colorOutline >> 8 & 255) / 255.0F;
    float f3 = (float) (colorOutline & 255) / 255.0F;
    float f4 = (float) (shade >> 24 & 255) / 255.0F;
    float f5 = (float) (shade >> 16 & 255) / 255.0F;
    float f6 = (float) (shade >> 8 & 255) / 255.0F;
    float f7 = (float) (shade & 255) / 255.0F;
    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.disableAlphaTest();
    RenderSystem.defaultBlendFunc();
    RenderSystem.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferbuilder.pos((double) w, (double) y, 0).color(f1, f2, f3, f).endVertex();
    bufferbuilder.pos((double) x, (double) y, 0).color(f1, f2, f3, f).endVertex();
    bufferbuilder.pos((double) x, (double) h, 0).color(f5, f6, f7, f4).endVertex();
    bufferbuilder.pos((double) w, (double) h, 0).color(f5, f6, f7, f4).endVertex();
    tessellator.draw();
    RenderSystem.shadeModel(7424);
    RenderSystem.disableBlend();
    RenderSystem.enableAlphaTest();
    RenderSystem.enableTexture();
  }

  @Deprecated
  public static void drawTextureRect(double x, double y, double width, double height, float u, float v, float t, float s) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder renderer = tessellator.getBuffer();
    renderer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
    renderer.pos(x + width, y, 0F).tex(t, v).endVertex();
    renderer.pos(x, y, 0F).tex(u, v).endVertex();
    renderer.pos(x, y + height, 0F).tex(u, s).endVertex();
    renderer.pos(x, y + height, 0F).tex(u, s).endVertex();
    renderer.pos(x + width, y + height, 0F).tex(t, s).endVertex();
    renderer.pos(x + width, y, 0F).tex(t, v).endVertex();
    tessellator.draw();
  }

  @Deprecated
  public static void drawLine(float size, float x, float y, float x1, float y1) {
    RenderSystem.lineWidth(size);
    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();
    buffer.begin(GL_LINES, DefaultVertexFormats.POSITION);
    buffer.pos(x, y, 0F).endVertex();
    buffer.pos(x1, y1, 0F).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture();
  }
}