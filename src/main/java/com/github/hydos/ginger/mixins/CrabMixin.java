package com.github.hydos.ginger.mixins;

import com.github.fulira.litecraft.Litecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashScreen.class)
public class CrabMixin {

    @Shadow
    @Final
    private boolean reloading;

    @Inject(method = "render",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReloadMonitor;isApplyStageComplete()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReloadMonitor;throwExceptions()V"))
    private void onResourceLoadComplete(CallbackInfo ci) {
        if (!reloading) {
            new Litecraft(1080, 860, 60);
        }
    }

}