/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Tolerance;
import Reika.DragonAPI.ModInteract.Bees.BeeTraits;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public enum CrystalBeeTypes {
	BLACK(Speeds.SLOW, Life.NORMAL, Fertility.LOW, Flowering.SLOWER, Territory.DEFAULT, Tolerance.NONE, Tolerance.NONE, 0, 0, EnumTemperature.NORMAL, EnumHumidity.NORMAL),
	RED(Speeds.NORMAL, CrystalBees.superLife, Fertility.NORMAL, Flowering.AVERAGE, Territory.DEFAULT, Tolerance.DOWN, Tolerance.DOWN, 1, 1, EnumTemperature.HOT, EnumHumidity.ARID),
	GREEN(Speeds.SLOWEST, Life.NORMAL, Fertility.LOW, Flowering.SLOWEST, Territory.DEFAULT, Tolerance.BOTH, Tolerance.NONE, 4, 0, EnumTemperature.NORMAL, EnumHumidity.DAMP),
	BROWN(Speeds.SLOWER, CrystalBees.blinkLife, Fertility.NORMAL, Flowering.SLOW, Territory.DEFAULT, Tolerance.NONE, Tolerance.DOWN, 0, 1, EnumTemperature.COLD, EnumHumidity.ARID),
	BLUE(Speeds.FAST, Life.LONG, Fertility.NORMAL, Flowering.SLOWEST, Territory.LARGER, Tolerance.UP, Tolerance.NONE, 2, 0, EnumTemperature.NORMAL, EnumHumidity.NORMAL),
	PURPLE(Speeds.NORMAL, Life.NORMAL, CrystalBees.superFertility, Flowering.SLOWER, Territory.DEFAULT, Tolerance.NONE, Tolerance.NONE, 0, 0, EnumTemperature.NORMAL, EnumHumidity.DAMP),
	CYAN(Speeds.SLOW, Life.SHORT, Fertility.NORMAL, Flowering.FASTER, Territory.LARGE, Tolerance.BOTH, Tolerance.NONE, 1, 0, EnumTemperature.COLD, EnumHumidity.DAMP),
	LIGHTGRAY(Speeds.SLOWEST, Life.SHORTER, Fertility.LOW, Flowering.SLOWEST, Territory.LARGER, Tolerance.BOTH, Tolerance.NONE, 1, 0, EnumTemperature.NORMAL, EnumHumidity.NORMAL),
	GRAY(Speeds.SLOWEST, Life.LONG, Fertility.LOW, Flowering.SLOWEST, Territory.DEFAULT, Tolerance.NONE, Tolerance.BOTH, 0, 2, EnumTemperature.NORMAL, EnumHumidity.NORMAL),
	PINK(Speeds.NORMAL, Life.ELONGATED, Fertility.NORMAL, Flowering.FAST, Territory.DEFAULT, Tolerance.NONE, Tolerance.NONE, 0, 0, EnumTemperature.WARM, EnumHumidity.NORMAL),
	LIME(Speeds.SLOW, Life.SHORTER, Fertility.NORMAL, Flowering.AVERAGE, CrystalBees.superTerritory, Tolerance.NONE, Tolerance.NONE, 0, 0, EnumTemperature.NORMAL, EnumHumidity.NORMAL),
	YELLOW(Speeds.FAST, Life.ELONGATED, Fertility.HIGH, Flowering.SLOW, Territory.DEFAULT, Tolerance.UP, Tolerance.NONE, 1, 0, EnumTemperature.NORMAL, EnumHumidity.ARID),
	LIGHTBLUE(CrystalBees.superSpeed, Life.SHORTER, Fertility.LOW, Flowering.SLOWEST, Territory.DEFAULT, Tolerance.NONE, Tolerance.UP, 0, 0, EnumTemperature.NORMAL, EnumHumidity.NORMAL),
	MAGENTA(Speeds.NORMAL, Life.SHORT, Fertility.NORMAL, CrystalBees.superFlowering, Territory.DEFAULT, Tolerance.NONE, Tolerance.BOTH, 0, 2, EnumTemperature.NORMAL, EnumHumidity.NORMAL),
	ORANGE(Speeds.FASTER, Life.SHORTENED, Fertility.NORMAL, Flowering.SLOW, Territory.DEFAULT, Tolerance.NONE, Tolerance.NONE, 0, 0, EnumTemperature.HOT, EnumHumidity.ARID),
	WHITE(Speeds.SLOWER, Life.LONGER, Fertility.NORMAL, Flowering.FAST, Territory.DEFAULT, Tolerance.NONE, Tolerance.NONE, 0, 0, EnumTemperature.ICY, EnumHumidity.NORMAL);

	public static final CrystalBeeTypes[] list = values();

	protected final BeeTraits traits = new BeeTraits();

	private CrystalBeeTypes(Speeds s, Life l, Fertility f, Flowering f2, Territory a, Tolerance d1, Tolerance d2, int tt, int ht, EnumTemperature t, EnumHumidity h) {
		traits.speed = s;
		traits.lifespan = l;
		traits.fertility = f;
		traits.flowering = f2;
		traits.area = a;
		traits.tempDir = d1;
		traits.humidDir = d2;
		traits.tempTol = tt;
		traits.humidTol = ht;
		traits.temperature = t;
		traits.humidity = h;

		if (this.ordinal() == CrystalElement.CYAN.ordinal())
			traits.isTolerant = true;
		traits.isNocturnal = this.ordinal() == CrystalElement.BLUE.ordinal();
	}

	public BeeTraits getTraits() {
		return traits;
	}
}
