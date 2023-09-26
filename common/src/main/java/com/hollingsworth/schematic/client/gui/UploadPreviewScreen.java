package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.export.CameraSettings;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Paths;

public class UploadPreviewScreen extends BaseSchematicScreen {

    DynamicTexture dynamicTexture;
    public int yaw = 225;
    public int pitch = 30;
    public int roll;
    WrappedScene wrappedScene;
    Scene scene;
    int drawDelay = 0;
    public UploadPreviewScreen() {
        super();
        wrappedScene = new WrappedScene();
        scene = new Scene(new GuidebookLevel(), new CameraSettings());
        scene.getCameraSettings().setIsometricYawPitchRoll(yaw, pitch, roll);
        wrappedScene.setScene(scene);
        wrappedScene.placeStructure(Paths.get("./schematics/test/test.nbt"));
        scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());
        scene.centerScene();
    }

    public void buildTexture(){
        scene.getCameraSettings().setIsometricYawPitchRoll(yaw, pitch, roll);
        scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());
        scene.centerScene();
        NativeImage nativeImage = wrappedScene.asNativeImage(1.0f);
        dynamicTexture = new DynamicTexture(nativeImage);
        Minecraft.getInstance().getTextureManager().register(new ResourceLocation(Constants.MOD_ID, "test_text"), dynamicTexture);

    }

    @Override
    public void tick() {
        super.tick();
        if(drawDelay > 0) {
            drawDelay--;
            if(drawDelay == 0){
                buildTexture();
            }
        }
    }

    @Override
    public void init() {
        super.init();
        buildTexture();
        addRenderableWidget(new ShortTextField(font, bookLeft + 185, bookTop + 41, Component.empty()));
        addRenderableWidget(new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_small.png"), b ->{
        }));
        NoScrollMultiText description = new NoScrollMultiText(font, bookLeft + 185, bookTop + 73, 95, 78, Component.empty(), Component.empty());
        addRenderableWidget(description);
        addRenderableWidget(new DecoratedSlider(bookLeft + 41, bookTop + 152, 95, 15, Component.empty(), Component.empty(), 0, 360, 225, 5, 1, false, this::setYaw));
        addRenderableWidget(new DecoratedSlider(bookLeft + 41, bookTop + 168, 95, 15, Component.empty(), Component.empty(), 0, 90, 30, 5, 1, true, this::setPitch));
    }

    public void setYaw(int yaw){
        this.yaw = yaw;
        drawDelay = 3;
    }

    public void setPitch(int pitch){
        this.pitch = pitch;
        drawDelay = 3;
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        int previewX = bookLeft + 25;
        int previewY = bookTop + 41;
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_model_preview.png"), previewX, previewY, 0, 0 , 143, 111, 143, 111);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(),  bookRight - 67, bookTop + 157);


        // center x and y on point 50, 50
        int x = previewX + 143/2;
        int y = previewY + 111/2;

        int imageWidth = dynamicTexture.getPixels().getWidth();
        int imageHeight = dynamicTexture.getPixels().getHeight();

        // scale width and height to fit in the box of 100,100
        Dimension origDim = new Dimension(imageWidth, imageHeight);
        Dimension boundary = new Dimension(100, 100);
        Dimension newDim = getScaledDimension(origDim, boundary);
        // Offset x and Y so the image is centered
        x -= newDim.width/2;
        y -= newDim.height/2;
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale((float)newDim.width / (float)imageWidth, (float)newDim.height / (float)imageHeight, 1);
        // translate so it is back to the center, account for scale
        poseStack.translate((float)x / ((float)newDim.width / (float)imageWidth), (float)y / ((float)newDim.height / (float)imageHeight), 0);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "test_text"), 0, 0, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        poseStack.popPose();
//        PoseStack poseStack = graphics.pose();
//        poseStack.pushPose();
//        // scale so the image fits in the box
//        poseStack.scale((float)scaledWidth / (float)dynamicTexture.getPixels().getWidth(), (float)scaledHeight / (float)dynamicTexture.getPixels().getHeight(), 1);
//        graphics.blit(new ResourceLocation(Constants.MOD_ID, "test_text"), x,y, 0, 0, dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight(), dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight());
//        poseStack.popPose();

    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_long.png"), 25, 25, 0, 0 , 143, 15, 143, 15);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_short.png"), 185, 25, 0, 0 , 95, 15, 95, 15);

        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_short.png"), 185, 57, 0, 0 , 95, 15, 95, 15);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.preview").getVisualOrderText(), 25 + 143/2, 29);

        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.name").getVisualOrderText(), 185 + 48, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.description").getVisualOrderText(), 185 + 48, 61);

    }

    public record Dimension(int width, int height){

    }
}
