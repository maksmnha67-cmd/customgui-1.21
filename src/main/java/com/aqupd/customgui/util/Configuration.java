package com.aqupd.customgui.util;

import com.aqupd.customgui.CustomHandGui;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * Holds and persists the hand position/rotation offsets.
 * Ported from the original 1.12.2 mod - values and keys kept the same
 * so old config files remain compatible.
 */
public class Configuration {
    public static boolean swapChat = false;
    public static boolean update = false;
    public static boolean lefthandedit = false;

    // "1" = right hand, "2" = left hand (matches the original mod's naming)
    public static double x1Gui = 0.0, y1Gui = 0.0, z1Gui = 0.0;
    public static double x2Gui = 0.0, y2Gui = 0.0, z2Gui = 0.0;
    public static float x1Rot = 0.0f, y1Rot = 0.0f, z1Rot = 0.0f;
    public static float x2Rot = 0.0f, y2Rot = 0.0f, z2Rot = 0.0f;

    private static final File configFile =
            new File(FabricLoader.getInstance().getConfigDir().toFile(), "AqMods/CHGUI.properties");

    public static void changeChat() {
        swapChat = !swapChat;
        saveOptions();
    }

    public static void resetHand() {
        if (lefthandedit) {
            x2Gui = y2Gui = z2Gui = 0.0;
            x2Rot = y2Rot = z2Rot = 0.0f;
        } else {
            x1Gui = y1Gui = z1Gui = 0.0;
            x1Rot = y1Rot = z1Rot = 0.0f;
        }
        saveOptions();
    }

    public static void resetHands() {
        x1Gui = y1Gui = z1Gui = 0.0;
        x2Gui = y2Gui = z2Gui = 0.0;
        x1Rot = y1Rot = z1Rot = 0.0f;
        x2Rot = y2Rot = z2Rot = 0.0f;
        saveOptions();
    }

    public static void randomizeHands() {
        x1Gui = Math.random() * -1.0 + 0.5;
        y1Gui = Math.random() * -1.0 + 1.0;
        z1Gui = Math.random() * -2.0;
        x2Gui = Math.random() * -1.0 + 0.5;
        y2Gui = Math.random() * -1.0 + 1.0;
        z2Gui = Math.random() * -2.0;
        saveOptions();
    }

    public static void setHandPos(String nxyz, double value) {
        switch (nxyz) {
            case "1x" -> x1Gui = value;
            case "1y" -> y1Gui = value;
            case "1z" -> z1Gui = value;
            case "2x" -> x2Gui = value;
            case "2y" -> y2Gui = value;
            case "2z" -> z2Gui = value;
        }
        saveOptions();
    }

    public static void setHandRot(String nxyz, float value) {
        switch (nxyz) {
            case "1x" -> x1Rot = value;
            case "1y" -> y1Rot = value;
            case "1z" -> z1Rot = value;
            case "2x" -> x2Rot = value;
            case "2y" -> y2Rot = value;
            case "2z" -> z2Rot = value;
        }
        saveOptions();
    }

    public static void loadOptions() throws IOException {
        if (!configFile.exists() || configFile.length() == 0L) {
            saveOptions();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length < 2) continue;
                switch (parts[0]) {
                    case "x1Gui" -> x1Gui = Double.parseDouble(parts[1]);
                    case "y1Gui" -> y1Gui = Double.parseDouble(parts[1]);
                    case "z1Gui" -> z1Gui = Double.parseDouble(parts[1]);
                    case "x1Rot" -> x1Rot = Float.parseFloat(parts[1]);
                    case "y1Rot" -> y1Rot = Float.parseFloat(parts[1]);
                    case "z1Rot" -> z1Rot = Float.parseFloat(parts[1]);
                    case "x2Gui" -> x2Gui = Double.parseDouble(parts[1]);
                    case "y2Gui" -> y2Gui = Double.parseDouble(parts[1]);
                    case "z2Gui" -> z2Gui = Double.parseDouble(parts[1]);
                    case "x2Rot" -> x2Rot = Float.parseFloat(parts[1]);
                    case "y2Rot" -> y2Rot = Float.parseFloat(parts[1]);
                    case "z2Rot" -> z2Rot = Float.parseFloat(parts[1]);
                    case "swapChat" -> swapChat = Boolean.parseBoolean(parts[1]);
                    case "update" -> update = Boolean.parseBoolean(parts[1]);
                }
            }
        }
    }

    public static void saveOptions() {
        try {
            Files.createDirectories(configFile.getParentFile().toPath());
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
                writer.println("x1Gui:" + x1Gui);
                writer.println("y1Gui:" + y1Gui);
                writer.println("z1Gui:" + z1Gui);
                writer.println("x1Rot:" + x1Rot);
                writer.println("y1Rot:" + y1Rot);
                writer.println("z1Rot:" + z1Rot);
                writer.println("x2Gui:" + x2Gui);
                writer.println("y2Gui:" + y2Gui);
                writer.println("z2Gui:" + z2Gui);
                writer.println("x2Rot:" + x2Rot);
                writer.println("y2Rot:" + y2Rot);
                writer.println("z2Rot:" + z2Rot);
                writer.println("swapChat:" + swapChat);
                writer.println("update:" + update);
            }
        } catch (Exception e) {
            CustomHandGui.LOGGER.error("[CustomGUI] Failed to save options", e);
        }
    }
}
