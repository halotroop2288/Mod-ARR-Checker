package com.halotroop.arrchecker;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ARRChecker implements ModInitializer
{
	private static final List<String> arrMods = new ArrayList<>();

	@Override
	public void onInitialize()
	{
		arrMods.clear();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods())
		{
			ModMetadata modMeta = mod.getMetadata();
			// Assume all authors don't know how capitalization works, and don't use an array for the string!
			String modLicense = modMeta.getLicense().toString().toLowerCase().replace('[', ' ').replace(']', ' ').trim();
			// Create a constant of the mod name and ID to be used multiple times: Don't use an array for the string!
			String modNameAndID = (modMeta.getName() + " (" + modMeta.getId() + ")").replace('[', ' ').replace(']', ' ').trim();
			// Give a different warning for Minecraft itself.
			if (!modMeta.getId().equals("minecraft"))
			{
				if (modLicense.isEmpty())
				{
					// If no license is found, assume mod is not correctly licensed, and therefore, not modpack-friendly.
					System.out.println(modNameAndID + " has no license! It may be ARR!");
					arrMods.add(modMeta.getId());
				}
				else if (modLicense.equals("all rights reserved") || modLicense.equals("arr") || modLicense.contains("copyright"))
				{
					// If license is All Rights Reserved, or copyright is attributed, assume mod is not modpack-friendly.
					System.out.println(modNameAndID + " is ARR. Do not use it( in a modpack)!");
					arrMods.add(modMeta.getId());
				}
				else
				{
					String[] validLicenses =
						{
							"gpl", "mit", "cc0", "apache", "unlicense", "mpl", // Short form names
							"gnu public license", "mozilla public license", "creative commons" // Long form (incorrect, but check anyway)
						};
					boolean modLicenseInvalid = true;
					for (String validLicense : validLicenses)
					{
						// If a valid license is found, set invalid to false, and stop checking
						if (!(modLicenseInvalid = !modLicense.contains(validLicense))) break;
					}
					if (modLicenseInvalid)
					{
						// If a valid license is not found, print crayon warning.
						System.out.println(modNameAndID + " may have a crayon license! It is: " + modLicense);
						// If a valid license is not found, assume mod is not modpack-friendly.
						arrMods.add(modMeta.getId());
					}
				}
			}
		}
		if (!arrMods.isEmpty())
		{
			String invalidMods = "";
			for (String mod : arrMods)
			{
				invalidMods = invalidMods
					+ (invalidMods.isEmpty() ? mod
					: arrMods.get(arrMods.size() - 1).equals(mod) ? (" and " + mod)
					: (", " + mod));
			}
			if (!invalidMods.isEmpty()) System.out.println(arrMods.size() == 1 ?
					"This mod" : "These mods" + " may not be suitable for a modpack:\n" + invalidMods);
		}
		System.out.println("Minecraft isn't a mod, but in case you didn't know, it's ARR!\n" + "Don't distribute it!");
	}

	public static List<String> getARRMods()
	{ return arrMods; }
}
