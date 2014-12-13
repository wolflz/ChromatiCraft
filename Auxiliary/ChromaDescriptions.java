/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalLaser;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public final class ChromaDescriptions {

	public static final String PARENT = getParent();
	public static final String DESC_SUFFIX = ":desc";
	public static final String NOTE_SUFFIX = ":note";

	private static HashMap<ChromaResearch, String> data = new HashMap<ChromaResearch, String>();
	private static HashMap<ChromaResearch, String> notes = new HashMap<ChromaResearch, String>();

	private static HashMap<ChromaTiles, Object[]> machineData = new HashMap<ChromaTiles, Object[]>();
	private static HashMap<ChromaTiles, Object[]> machineNotes = new HashMap<ChromaTiles, Object[]>();
	private static HashMap<ChromaResearch, Object[]> miscData = new HashMap<ChromaResearch, Object[]>();
	private static HashMap<ChromaResearch, Object[]> abilityData = new HashMap<ChromaResearch, Object[]>();
	private static HashMap<ChromaResearch, Object[]> hoverData = new HashMap<ChromaResearch, Object[]>();

	private static HashMap<String, String> hoverText = new HashMap<String, String>();

	private static final boolean mustLoad = !ReikaObfuscationHelper.isDeObfEnvironment();
	private static final XMLInterface machines = new XMLInterface(ChromatiCraft.class, PARENT+"machines.xml", mustLoad);
	private static final XMLInterface blocks = new XMLInterface(ChromatiCraft.class, PARENT+"blocks.xml", mustLoad);
	private static final XMLInterface abilities = new XMLInterface(ChromatiCraft.class, PARENT+"abilities.xml", mustLoad);
	private static final XMLInterface structures = new XMLInterface(ChromatiCraft.class, PARENT+"structure.xml", mustLoad);
	private static final XMLInterface tools = new XMLInterface(ChromatiCraft.class, PARENT+"tools.xml", mustLoad);
	private static final XMLInterface resources = new XMLInterface(ChromatiCraft.class, PARENT+"resource.xml", mustLoad);
	private static final XMLInterface infos = new XMLInterface(ChromatiCraft.class, PARENT+"info.xml", mustLoad);
	private static final XMLInterface hover = new XMLInterface(ChromatiCraft.class, PARENT+"hover.xml", mustLoad);

	private static String getParent() {
		Language language = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
		String lang = language.getLanguageCode();
		String sg = lang.toUpperCase().substring(0, 2)+"/";
		if (hasLocalizedFor(language) && !"EN".equals(sg))
			return "Resources/"+sg;
		return "Resources/";
	}

	private static boolean hasLocalizedFor(Language language) {
		String lang = language.getLanguageCode();
		String sg = lang.toUpperCase().substring(0, 2)+"/";
		Object o = ChromatiCraft.class.getResourceAsStream("Resources/"+sg+"categories.xml");
		return o != null;
	}

	public static String getHoverText(String key) {
		return hoverText.get(key);
	}

	private static void addData(ChromaTiles m, Object... data) {
		machineData.put(m, data);
	}

	private static void addData(ChromaResearch h, Object... data) {
		miscData.put(h, data);
	}

	private static void addData(ChromaResearch h, int[] data) {
		Object[] o = new Object[data.length];
		for (int i = 0; i < o.length; i++)
			o[i] = data[i];
		miscData.put(h, o);
	}

	private static void addNotes(ChromaTiles m, Object... data) {
		machineNotes.put(m, data);
	}

	public static void reload() {
		loadNumericalData();

		machines.reread();
		abilities.reread();
		tools.reread();
		resources.reread();
		infos.reread();
		structures.reread();
		hover.reread();

		loadData();
	}

	private static void addEntry(ChromaResearch h, String sg) {
		data.put(h, sg);
	}

	public static void loadData() {
		ArrayList<ChromaResearch> infotabs = ChromaResearch.getInfoTabs();
		ArrayList<ChromaResearch> machinetabs = ChromaResearch.getMachineTabs();
		ArrayList<ChromaResearch> blocktabs = ChromaResearch.getBlockTabs();
		ArrayList<ChromaResearch> abilitytabs = ChromaResearch.getAbilityTabs();
		ArrayList<ChromaResearch> tooltabs = ChromaResearch.getToolTabs();
		ArrayList<ChromaResearch> resourcetabs = ChromaResearch.getResourceTabs();
		ArrayList<ChromaResearch> structuretabs = ChromaResearch.getStructureTabs();

		for (ChromaResearch h : machinetabs) {
			ChromaTiles m = h.getMachine();
			String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase()+DESC_SUFFIX);
			String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase()+NOTE_SUFFIX);
			desc = String.format(desc, machineData.get(m));
			aux = String.format(aux, machineNotes.get(m));

			if (XMLInterface.NULL_VALUE.equals(desc))
				desc = "There is no handbook data for this machine yet.";
			//ReikaJavaLibrary.pConsole(m.name().toLowerCase()+":"+desc);

			if (m.isDummiedOut()) {
				desc += "\nThis machine is currently unavailable.";
				if (m.hasPrerequisite() && !m.getPrerequisite().isLoaded())
					desc += "\nThis machine depends on another mod.";
				aux += "\nNote: Dummied Out";
			}
			if (m.hasPrerequisite()) {
				String sg = m.getPrerequisite().getModLabel().replaceAll("[|]", "");
				aux += "\nDependencies: "+ReikaStringParser.splitCamelCase(sg).replaceAll(" Craft", "Craft");
			}
			if (m.isIncomplete()) {
				desc += "\nThis machine is incomplete. Use at your own risk.";
			}

			addEntry(h, desc);
			notes.put(h, aux);
		}

		for (ChromaResearch h : blocktabs) {
			String desc = blocks.getValueAtNode("blocks:"+h.name().toLowerCase()+DESC_SUFFIX);
			addEntry(h, desc);
		}

		for (ChromaResearch h : tooltabs) {
			String desc = tools.getValueAtNode("tools:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (ChromaResearch h : resourcetabs) {
			String desc = resources.getValueAtNode("resource:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (ChromaResearch h : structuretabs) {
			String desc = structures.getValueAtNode("structure:"+h.name().toLowerCase());
			addEntry(h, desc);
		}

		for (ChromaResearch h : infotabs) {
			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase());
			desc = String.format(desc, miscData.get(h));
			addEntry(h, desc);
		}

		for (ChromaResearch h : abilitytabs) {
			String desc = abilities.getValueAtNode("ability:"+h.name().toLowerCase());
			desc = String.format(desc, abilityData.get(h));
			addEntry(h, desc);
		}

		Collection<String> keys = ChromaHelpData.instance.getHelpKeys();
		for (String s : keys) {
			String desc = hover.getValueAtNode("hover:"+s);
			desc = String.format(desc, hoverData.get(s));
			hoverText.put(s, desc);
		}
	}

	public static String getAbilityDescription(Chromabilities c) {
		return abilities.getValueAtNode("abilities:"+c.name().toLowerCase());
	}

	public static String getData(ChromaResearch h) {
		if (!data.containsKey(h))
			return "This item has no lexicon info yet.";
		return data.get(h);
	}

	public static String getNotes(ChromaResearch h) {
		if (!notes.containsKey(h))
			return "";
		return notes.get(h);
	}

	static {
		loadNumericalData();
	}

	private static void loadNumericalData() {
		addData(ChromaTiles.REPEATER, TileEntityCrystalPylon.RANGE);
		addData(ChromaTiles.LASER, TileEntityCrystalLaser.MAX_RANGE);
		addData(ChromaTiles.BEACON, CrystalElement.RED.displayName);

		addNotes(ChromaTiles.GUARDIAN, TileEntityGuardianStone.RANGE);
		addNotes(ChromaTiles.TELEPUMP, TileEntityTeleportationPump.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.MINER, TileEntityMiner.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.REPROGRAMMER, TileEntitySpawnerReprogrammer.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.REPEATER, TileEntityCrystalRepeater.RANGE);
		addNotes(ChromaTiles.TANK, TileEntityCrystalTank.FACTOR/1000, TileEntityCrystalTank.MAXCAPACITY/1000);
		addNotes(ChromaTiles.CHARGER, TileEntityCrystalCharger.CAPACITY);
		addNotes(ChromaTiles.TICKER, TileEntityInventoryTicker.getRequiredEnergy().toDisplay());
		addNotes(ChromaTiles.FURNACE, TileEntityCrystalFurnace.MULTIPLY);
		addNotes(ChromaTiles.FABRICATOR, FabricationRecipes.FACTOR, FabricationRecipes.POWER2*2);
		addNotes(ChromaTiles.BEACON, TileEntityCrystalBeacon.RATIO, TileEntityCrystalBeacon.POWER, TileEntityCrystalBeacon.MAXRANGE);
		addNotes(ChromaTiles.COLLECTOR, TileEntityCollector.XP_PER_CHROMA);
		addNotes(ChromaTiles.ITEMCOLLECTOR, TileEntityItemCollector.MAXRANGE, TileEntityItemCollector.MAXYRANGE);
		addNotes(ChromaTiles.LAMP, TileEntityChromaLamp.FACTOR);
		addNotes(ChromaTiles.POWERTREE, TileEntityPowerTree.BASE, TileEntityPowerTree.RATIO, TileEntityPowerTree.POWER);
		addNotes(ChromaTiles.LAMPCONTROL, TileEntityLampController.MAXRANGE, TileEntityLampController.MAXCHANNEL);
	}
}
