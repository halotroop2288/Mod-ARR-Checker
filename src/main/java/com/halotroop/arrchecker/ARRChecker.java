package com.halotroop.arrchecker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ARRChecker implements ModInitializer {
    private static final List<String> arrMods = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger("arrchecker");

    @Override
    public void onInitialize() {
        arrMods.clear();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata modMeta = mod.getMetadata();
            // Assume all authors don't know how capitalization works, and don't use an array for the string!
            // '-' is being replaced with ' ' due to some mod authors having specified their creative commons license without '-' but with spaces instead
            final String modLicense = modMeta.getLicense().toString().toLowerCase().replaceAll("[\\[\\]-]", " ").trim();
            // Create a constant of the mod name and ID to be used multiple times: Don't use an array for the string!
            final String modNameAndID = (modMeta.getName() + " (" + modMeta.getId() + ")").replaceAll("[\\[\\]]", " ").trim();
            // Ignore built in mods
            if (!modMeta.getType().equals("builtin")) {
                if (modLicense.isEmpty()) {
                    // If no license is found, assume mod is not correctly licensed, and therefore, not modpack-friendly.
                    LOGGER.warn(modNameAndID + " has no license! It may be ARR!");
                    arrMods.add(modMeta.getId());
                } else if (modLicense.equals("all rights reserved") || modLicense.equals("arr") || modLicense.contains("copyright")) {
                    // If license is All Rights Reserved, or copyright is attributed, assume mod is not modpack-friendly.
                    LOGGER.warn(modNameAndID + " is ARR. Do not use it in a modpack!");
                    arrMods.add(modMeta.getId());
                } else {
                    String[] validLicenses =
                            {
                                    "gpl", "mit", "cc0", "apache", "unlicense", "mpl", // Short form names
                                    "gnu lesser general public license", "gnu general public license", "mozilla public license", "creative commons", "cc by nc" // Long form (incorrect, but check anyway)
                            };
                    boolean modLicenseInvalid = true;
                    for (String validLicense : validLicenses) {
                        // If a valid license is found, set invalid to false, and stop checking
                        if (!(modLicenseInvalid = !modLicense.contains(validLicense))) break;
                    }
                    if (modLicenseInvalid) {
                        // If a valid license is not found, print crayon warning.
                        LOGGER.warn(modNameAndID + " may have a crayon license! It is: " + modLicense);
                        // If a valid license is not found, assume mod is not modpack-friendly.
                        arrMods.add(modMeta.getId());
                    }
                }
            }
        }
        if (!arrMods.isEmpty()) {
            StringBuilder invalidMods = new StringBuilder();
            for (int i = 0, arrModsSize = arrMods.size(); i < arrModsSize; i++) {
                String mod = arrMods.get(i);
                if (invalidMods.length() > 0) {
                    boolean isLastElement = i == arrModsSize - 1;
                    invalidMods.append(isLastElement ? " and " : ", ");
                }
                invalidMods.append(mod);
            }
            if (invalidMods.length() != 0) {
                LOGGER.error(arrMods.size() == 1 ? "This mod" : "These mods" +
                        " may not be suitable for a modpack:\n" + invalidMods);
            }
        }
        LOGGER.warn("Minecraft isn't a mod, but in case you didn't know, it's ARR!\nDon't distribute it!");
    }

    public static List<String> getARRMods() {
        return arrMods;
    }
}
