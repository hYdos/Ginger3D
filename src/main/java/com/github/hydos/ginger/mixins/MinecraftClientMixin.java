package com.github.hydos.ginger.mixins;

import com.github.fulira.litecraft.Litecraft;
import com.github.hydos.ginger.engine.common.api.GingerRegister;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> {

    public MinecraftClientMixin(String string) {
        super(string);
    }
    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;swapBuffers()V", shift = At.Shift.BEFORE))
    private void e(boolean tick, CallbackInfo ci){
        RenderSystem.pushMatrix();
        if(Litecraft.getInstance() != null){
            if(Litecraft.getInstance().isReady){
                GingerRegister.getInstance().game.render();
            }
        }
        RenderSystem.popMatrix();
    }
}
