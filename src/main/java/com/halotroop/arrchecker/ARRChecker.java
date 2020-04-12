package com.halotroop.arrchecker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ARRChecker implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		Boolean invalidLicenseFound = false;
		for (ModContainer mod : FabricLoader.getInstance().getAllMods())
		{
			ModMetadata modMeta = mod.getMetadata();
			// Assume all authors don't know how capitalization works
			String modLicense = modMeta.getLicense().toString().toLowerCase().replace('[', ' ').replace(']', ' ').strip();
			// Create a constant of the mod name and ID to be used multiple times: Don't use an array for the string!
			String modNameAndID = (modMeta.getName() + " (" + modMeta.getId() + ")").replace('[', ' ').replace(']', ' ').strip();
			// Give a different warning for Minecraft itself.
			if (modMeta.getId() != "minecraft")
			{
				if (modLicense == null || modLicense.isBlank() || modLicense.isEmpty())
				{
					// If no license is found, assume mod is not correctly licensed, and therefore, not modpack-friendly.
					System.out.println(modNameAndID + " has no license! It may be ARR!");
					invalidLicenseFound = true;
				}
				else if (modLicense == "all rights reserved" || modLicense == "arr" || modLicense.contains("copyright"))
				{
					// If license is All Rights Reserved, or copyright is attributed, assume mod is not modpack-friendly.
					System.out.println(modNameAndID + " is ARR. Do not use it( in a modpack)!");
					invalidLicenseFound = true;
				}
				else
				{
					String[] validLicenses =
						{
							"gpl", "mit", "cc0", "apache", "unlicense", "mpl", // Short form names
							"gnu public license", "mozilla public license", "creative commons" // Long form (incorrect, but check anyway)
						};
					Boolean modLicenseInvalid = true;
					for (String validLicense : validLicenses)
					{
						// If a valid license is found, set invalid to false, and stop checking
						if (!(modLicenseInvalid = !modLicense.contains(validLicense))) break;
					}
					if (modLicenseInvalid)
					{
						// If a valid license is not found, print crayon warning.
						System.out.println(modNameAndID + " may have a crayon license! It is: " + modLicense);
					}
					// If a valid license is not found, assume mod is not modpack-friendly.
					invalidLicenseFound = modLicenseInvalid;
				}
			}
			else System.out.println("Minecraft isn't a mod, but in case you didn't know, it's ARR!\n"
				+ "Don't distribute it!");
		}
		if (invalidLicenseFound) System.err.println("This/these mod(s) may not be suitable for a modpack!");
	}
}
