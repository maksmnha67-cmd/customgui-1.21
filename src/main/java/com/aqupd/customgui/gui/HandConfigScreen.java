package com.aqupd.customgui.gui;

import com.aqupd.customgui.util.Configuration;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

/**
 * Simple replacement for the original GuiPageButtonList-based config screen
 * (that class no longer exists in modern Minecraft). Uses vanilla
 * SliderWidget + ButtonWidget instead - same functionality, different UI
 * toolkit.
 */
public class HandConfigScreen extends Screen {
    private static final int SLIDER_WIDTH = 200;
    private static final double RANGE_POS = 2.0;   // -2..2 blocks
    private static final double RANGE_ROT = 180.0;  // -180..180 degrees

    public HandConfigScreen(Screen parent) {
        super(Text.translatable("key.aqupd.configgui"));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - SLIDER_WIDTH / 2;
        int y = 40;
        int spacing = 24;

        addPosSlider(x, y, "x", posValue("x"), v -> Configuration.setHandPos(handKey("x"), v));
        addPosSlider(x, y + spacing, "y", posValue("y"), v -> Configuration.setHandPos(handKey("y"), v));
        addPosSlider(x, y + spacing * 2, "z", posValue("z"), v -> Configuration.setHandPos(handKey("z"), v));

        addRotSlider(x, y + spacing * 3 + 10, "x", rotValue("x"), v -> Configuration.setHandRot(handKey("x"), v));
        addRotSlider(x, y + spacing * 4 + 10, "y", rotValue("y"), v -> Configuration.setHandRot(handKey("y"), v));
        addRotSlider(x, y + spacing * 5 + 10, "z", rotValue("z"), v -> Configuration.setHandRot(handKey("z"), v));

        int row = y + spacing * 6 + 24;
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable(Configuration.lefthandedit ? "gui.left.hand" : "gui.right.hand"),
                b -> {
                    Configuration.lefthandedit = !Configuration.lefthandedit;
                    this.clearAndInit();
                }).dimensions(x, row, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("config.aqupd.swapchat"),
                b -> Configuration.changeChat())
                .dimensions(x + 105, row, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("config.aqupd.resetposition"),
                b -> {
                    Configuration.resetHand();
                    this.clearAndInit();
                }).dimensions(x, row + 24, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.DONE,
                b -> this.close())
                .dimensions(x, row + 48, 200, 20).build());
    }

    private double posValue(String axis) {
        boolean left = Configuration.lefthandedit;
        return switch (axis) {
            case "x" -> left ? Configuration.x2Gui : Configuration.x1Gui;
            case "y" -> left ? Configuration.y2Gui : Configuration.y1Gui;
            default -> left ? Configuration.z2Gui : Configuration.z1Gui;
        };
    }

    private float rotValue(String axis) {
        boolean left = Configuration.lefthandedit;
        return switch (axis) {
            case "x" -> left ? Configuration.x2Rot : Configuration.x1Rot;
            case "y" -> left ? Configuration.y2Rot : Configuration.y1Rot;
            default -> left ? Configuration.z2Rot : Configuration.z1Rot;
        };
    }

    private String handKey(String axis) {
        return (Configuration.lefthandedit ? "2" : "1") + axis;
    }

    private void addPosSlider(int x, int y, String label, double value, java.util.function.DoubleConsumer onChange) {
        double normalized = (value + RANGE_POS) / (RANGE_POS * 2);
        this.addDrawableChild(new LabeledSlider(x, y, SLIDER_WIDTH, 20,
                Text.literal("Pos " + label.toUpperCase() + ": " + String.format("%.2f", value)),
                normalized, v -> {
                    double real = v * (RANGE_POS * 2) - RANGE_POS;
                    onChange.accept(real);
                }));
    }

    private void addRotSlider(int x, int y, String label, float value, java.util.function.DoubleConsumer onChange) {
        double normalized = (value + RANGE_ROT) / (RANGE_ROT * 2);
        this.addDrawableChild(new LabeledSlider(x, y, SLIDER_WIDTH, 20,
                Text.literal("Rot " + label.toUpperCase() + ": " + Math.round(value) + "\u00b0"),
                normalized, v -> {
                    double real = v * (RANGE_ROT * 2) - RANGE_ROT;
                    onChange.accept(real);
                }));
    }

    /** Small SliderWidget subclass that reports its value back via a callback. */
    private static class LabeledSlider extends SliderWidget {
        private final java.util.function.DoubleConsumer onChange;
        private final String prefix;

        LabeledSlider(int x, int y, int width, int height, Text label, double value,
                      java.util.function.DoubleConsumer onChange) {
            super(x, y, width, height, label, value);
            this.onChange = onChange;
            this.prefix = label.getString().split(":")[0];
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal(prefix + ": " + String.format("%.2f", this.value)));
        }

        @Override
        protected void applyValue() {
            onChange.accept(this.value);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
