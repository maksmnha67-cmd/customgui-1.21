package com.shyz.customgui;

import com.shyz.customgui.gui.HandConfigScreen;
import com.shyz.customgui.util.Configuration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomHandGui implements ClientModInitializer {
    public static final String MOD_ID = "customgui";
    public static final Logger LOGGER = LoggerFactory.getLogger("CustomHandGui");

    public static boolean isLeftHand = false;

    private static KeyBinding changeHandsKey;
    private static KeyBinding openConfigKey;
    private static KeyBinding resetHandsKey;
    private static KeyBinding randomizeKey;

    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        try {
            Configuration.loadOptions();
        } catch (Exception e) {
            LOGGER.error("[CustomGUI] Failed to load config", e);
        }

        changeHandsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shyz.changehands", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_5, "key.shyz.categories.handgui"));
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shyz.configgui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, "key.shyz.categories.handgui"));
        resetHandsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shyz.resethands", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_7, "key.shyz.categories.handgui"));
        randomizeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shyz.randomize", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, "key.shyz.categories.handgui"));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        while (changeHandsKey.wasPressed()) {
            SimpleOption<net.minecraft.util.Arm> mainArm = client.options.getMainArm();
            mainArm.setValue(mainArm.getValue() == net.minecraft.util.Arm.LEFT
                    ? net.minecraft.util.Arm.RIGHT : net.minecraft.util.Arm.LEFT);
            client.options.write();
        }
        while (openConfigKey.wasPressed()) {
            client.setScreen(new HandConfigScreen(null));
        }
        while (resetHandsKey.wasPressed()) {
            Configuration.resetHands();
        }
        while (randomizeKey.wasPressed()) {
            Configuration.randomizeHands();
        }

        if (Configuration.update) {
            tickCounter++;
            if (tickCounter >= 80) {
                tickCounter = 0;
                try {
                    Configuration.loadOptions();
                } catch (Exception e) {
                    LOGGER.error("[CustomGUI] Failed to reload config", e);
                }
            }
        }
    }
}
