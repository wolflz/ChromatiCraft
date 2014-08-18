/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityCrystalTile;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.DragonAPI.Instantiable.WorldLocation;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

public abstract class CrystalTileRender extends ChromaRenderBase {

	@Override
	public final String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		if (tile.worldObj != null) {
			TileEntityCrystalTile te = (TileEntityCrystalTile)tile;
			CrystalTarget ct = te.getTarget();
			if (ct != null) {

				double t = (System.currentTimeMillis()/600D)%360;
				t /= 30D;

				byte sides = 6;
				double r = 0.35+0.025*Math.sin(t*12);

				GL11.glPushMatrix();
				GL11.glTranslated(par2, par4, par6);

				WorldLocation src = new WorldLocation(te);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/beam.png");
				ReikaRenderHelper.disableLighting();
				//GL11.glDisable(GL11.GL_CULL_FACE);
				//GL11.glEnable(GL11.GL_BLEND);
				GL11.glShadeModel(GL11.GL_SMOOTH);
				Tessellator v5 = Tessellator.instance;

				GL11.glTranslated(src.xCoord-te.xCoord+0.5, src.yCoord-te.yCoord+0.5, src.zCoord-te.zCoord+0.5);

				//			for (int k = 0; k < li.size(); k++) {
				//				CrystalTarget ct = li.get(k);

				WorldLocation tgt = ct.location;


				//v5.setColorRGBA_I(te.getColor().color.getJavaColor().brighter().getRGB(), te.renderAlpha+255);
				//v5.addVertex(src.xCoord-te.xCoord+0.5, src.yCoord-te.yCoord+0.5, src.zCoord-te.zCoord+0.5);
				int dx = tgt.xCoord-src.xCoord;
				int dy = tgt.yCoord-src.yCoord;
				int dz = tgt.zCoord-src.zCoord;

				GL11.glPushMatrix();
				float f7 = MathHelper.sqrt_float(dx*dx+dz*dz);
				float f8 = MathHelper.sqrt_float(dx*dx+dy*dy+dz*dz);
				double ang1 = -Math.atan2(dz, dx) * 180 / Math.PI - 90;
				double ang2 = -Math.atan2(f7, dy) * 180 / Math.PI - 90;
				GL11.glRotated(ang1, 0.0F, 1.0F, 0.0F);
				GL11.glRotated(ang2, 1.0F, 0.0F, 0.0F);

				//v5.addVertex(0, 0, 0);
				//v5.addVertex(dx, dy, dz);

				v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
				v5.setColorOpaque_I(ct.color.color.getJavaColor().brighter().getRGB());
				for (int i = 0; i <= sides; i++) {
					double f11 = r*MathHelper.sin(i % sides * (float)Math.PI * 2.0F / sides) * 0.75F;
					double f12 = r*MathHelper.cos(i % sides * (float)Math.PI * 2.0F / sides) * 0.75F;
					double f13 = i % sides * 1.0F / sides;
					v5.addVertexWithUV(f11, f12, 0, t, t+1);
					v5.addVertexWithUV(f11, f12, f8, t+1, t);
				}

				v5.draw();

				GL11.glPopMatrix();
				//			}

				//GL11.glDisable(GL11.GL_BLEND);
				GL11.glShadeModel(GL11.GL_FLAT);
				GL11.glEnable(GL11.GL_CULL_FACE);
				ReikaRenderHelper.enableLighting();
				GL11.glPopMatrix();
			}
		}
	}

}