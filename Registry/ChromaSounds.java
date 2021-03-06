/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.net.URL;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public enum ChromaSounds implements SoundEnum {

	RIFT("rift"),
	POWERDOWN("powerdown-2"),
	DISCHARGE("discharge"),
	CAST("cast3"),
	POWER("ambient"),
	CRAFTING("ambient1_short"),
	CRAFTDONE("craftdone2"),
	UPGRADE("upgrade"),
	ABILITY("ability"),
	ERROR("error"),
	INFUSE("infuse"),
	INFUSION("infuse2"),
	USE("use2"),
	TRAP("slam2"),
	DING("ding2"),
	DING_HI("ding2_hi"),
	DING_LO("ding2_lo"),
	SHOCKWAVE("shockwave3"),
	BALLLIGHTNING("balllightning"),
	ITEMSTAND("stand"),
	POWERCRYS("powercrystal"),
	GUICLICK("gui2"),
	GUISEL("gui4"),
	DRONE("drone2"),
	PORTAL("portal2"),
	ORB("orb"),
	GOTODIM("todim"),
	OVERLOAD("discharge2"),
	PYLONFLASH("pylonboost"),
	PYLONTURBO("pylonturbo"),
	PYLONBOOSTRITUAL("pylonboost_ritual_short"),
	PYLONBOOSTSTART("pylonbooststart"),
	DASH("dash"),
	REPEATERSURGE("repeatersurge"),
	FIRE("fire"),
	LASER("laser");

	public static final ChromaSounds[] soundList = values();

	public static final String PREFIX = "Reika/ChromatiCraft/";
	public static final String SOUND_FOLDER = "Sounds/";
	private static final String SOUND_PREFIX = "Reika.ChromatiCraft.Sounds.";
	private static final String SOUND_DIR = "Sounds/";
	private static final String SOUND_EXT = ".ogg";
	private static final String MUSIC_FOLDER = "music/";
	private static final String MUSIC_PREFIX = "music.";

	private final String path;
	private final String name;
	//private final SoundCategory category;

	private boolean isVolumed = false;

	private ChromaSounds(String n) {
		if (n.startsWith("#")) {
			isVolumed = true;
			n = n.substring(1);
		}
		name = n;
		path = PREFIX+SOUND_FOLDER+name+SOUND_EXT;
		//category = cat;
	}

	public float getSoundVolume() {
		float vol = 1;//ConfigRegistry.MACHINEVOLUME.getFloat(); //config float
		if (vol < 0)
			vol = 0;
		if (vol > 1)
			vol = 1F;
		return vol;
	}

	@Override
	public float getModulatedVolume() {
		if (!isVolumed)
			return 1F;
		else
			return this.getSoundVolume();
	}

	public void playSound(Entity e) {
		this.playSound(e, 1, 1);
	}

	public void playSound(Entity e, float vol, float pitch) {
		this.playSound(e.worldObj, e.posX, e.posY, e.posZ, vol, pitch);
	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch) {
		if (world.isRemote)
			return;
		ReikaSoundHelper.playSound(this, ChromatiCraft.packetChannel, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z, float vol, float pitch) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, vol, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, 1, 1);
	}

	public void playSoundAtBlock(TileEntity te, float vol, float pitch) {
		this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord, vol, pitch);
	}

	public void playSoundAtBlockNoAttenuation(TileEntity te, float vol, float pitch) {
		if (te.worldObj.isRemote)
			return;
		double x = te.xCoord+0.5;
		double y = te.yCoord+0.5;
		double z = te.zCoord+0.5;
		ReikaSoundHelper.playSound(this, ChromatiCraft.packetChannel, te.worldObj, x, y, z, vol/* *this.getModulatedVolume()*/, pitch, false);
	}

	public void playSoundAtBlock(TileEntity te) {
		this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public void playSoundAtBlock(WorldLocation loc) {
		this.playSoundAtBlock(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public String getName() {
		return this.name();
	}

	public String getPath() {
		return path;
	}

	public URL getURL() {
		return ChromatiCraft.class.getResource(SOUND_DIR+name+SOUND_EXT);
	}

	public static ChromaSounds getSoundByName(String name) {
		for (int i = 0; i < soundList.length; i++) {
			if (soundList[i].name().equals(name))
				return soundList[i];
		}
		ChromatiCraft.logger.logError("\""+name+"\" does not correspond to a registered sound!");
		return null;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.MASTER;
	}

	@Override
	public boolean canOverlap() {
		return this == RIFT || this == CAST || this == USE || this == ERROR || this == INFUSE || this == DING || this == DRONE || this == ITEMSTAND;
	}

	@Override
	public boolean attenuate() {
		return this != GOTODIM && this != PYLONTURBO && this != PYLONFLASH && this != PYLONBOOSTRITUAL && this != PYLONBOOSTSTART && this != REPEATERSURGE;
	}
}
