package net.spookysquad.spookster.mod.mods;

import java.util.List;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.spookysquad.spookster.event.Event;
import net.spookysquad.spookster.event.events.Event3DRender;
import net.spookysquad.spookster.mod.Module;
import net.spookysquad.spookster.mod.Type;
import net.spookysquad.spookster.mod.mods.projectiles.Arrow;
import net.spookysquad.spookster.mod.mods.projectiles.Basic;
import net.spookysquad.spookster.mod.mods.projectiles.SplashPotion;
import net.spookysquad.spookster.mod.mods.projectiles.Throwable;
import net.spookysquad.spookster.utils.Wrapper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Projectiles extends Module {

	private Throwable[] throwables = { new Arrow(), new SplashPotion(), new Basic() };

	public Projectiles() {
		super(new String[] { "Projectiles" }, "Holy niggers.", Type.RENDER, Keyboard.KEY_NONE, 0xFFA06FA3);
	}

	public double pX = -9000, pY = -9000, pZ = -9000;

	public void onEvent(Event event) {
		if (event instanceof Event3DRender) {
			Event3DRender render = (Event3DRender) event;

			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			Throwable throwable = getThrowable(player, player.getCurrentEquippedItem());
			if (throwable == null) { return; }

			GL11.glPushMatrix();

			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(3042);
			GL11.glDisable(3553);
			
			GL11.glDisable(2929);
			GL11.glLineWidth(2.0F);
			GL11.glColor3d(0.6D, 0, 0.6D);
			GL11.glBegin(3);
			WorldClient world = Minecraft.getMinecraft().theWorld;

			double x = player.prevPosX + (player.posX - player.prevPosX) * render.getPartialTicks();
			double y = player.prevPosY + (player.posY - player.prevPosY) * render.getPartialTicks();
			double z = player.prevPosZ + (player.posZ - player.prevPosZ) * render.getPartialTicks();
			x -= MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * 0.16D;
			y -= 0.10000000149011612D;
			z -= MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * 0.16D;
			double motionX = -MathHelper.sin(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(player.rotationPitch / 180.0F * 3.1415927F) * 0.4D;
			double motionZ = MathHelper.cos(player.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(player.rotationPitch / 180.0F * 3.1415927F) * 0.4D;
			double motionY = -MathHelper.sin((player.rotationPitch + throwable.yOffset()) / 180.0F * 3.1415927F) * 0.4D;
			vertex(x - motionX, y - motionY, z - motionZ);
			float sqrt = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
			motionX /= sqrt;
			motionY /= sqrt;
			motionZ /= sqrt;
			motionX *= throwable.getPower();
			motionY *= throwable.getPower();
			motionZ *= throwable.getPower();
			MovingObjectPosition movingObjectPosition;

			for (;;) {
				float width = ((throwable instanceof Arrow) ? 0.5F : 0.25F) / 2.0F;
				float height = (throwable instanceof Arrow) ? 0.5F : 0.25F;
				AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(x - width, y, z - width, x + width, y + height, z + width);
				Vec3 velocity = isInWater(boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D));
				boolean inWater = velocity != null;
				if (inWater) {
					velocity = velocity.normalize();
					motionX += velocity.xCoord * 0.014D;
					motionY += velocity.yCoord * 0.014D;
					motionZ += velocity.zCoord * 0.014D;
					boundingBox = boundingBox.addCoord(velocity.xCoord * 0.014D, velocity.yCoord * 0.014D, velocity.zCoord * 0.014D);
				}
				Vec3 vector1 = Vec3.createVectorHelper(x, y, z);
				Vec3 vector2 = Vec3.createVectorHelper(x + motionX, y + motionY, z + motionZ);
				if ((throwable instanceof Arrow)) {
					movingObjectPosition = world.rayTraceBlocks(vector1, vector2, false, true, false);
				} else {
					movingObjectPosition = world.rayTraceBlocks(vector1, vector2);
				}
				vector1 = Vec3.createVectorHelper(x, y, z);
				vector2 = Vec3.createVectorHelper(x + motionX, y + motionY, z + motionZ);
				if (movingObjectPosition != null) {
					vector2 = Vec3.createVectorHelper(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
				}
				List entities = world.getEntitiesWithinAABBExcludingEntity(player, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
				double var6 = 0.0D;
				for (int index = 0; index < entities.size(); index++) {
					Entity entity = (Entity) entities.get(index);
					if ((entity instanceof EntityLivingBase)) {
						if ((player.fishEntity == null) || (entity != player.fishEntity)) {
							if (((entity instanceof EntityEnderman)) || (!entity.isEntityAlive())) {
							}
						}
					} else if (entity.canBeCollidedWith()) {
						boundingBox = entity.boundingBox.expand(0.3D, 0.3D, 0.3D);
						MovingObjectPosition hit = boundingBox.calculateIntercept(vector1, vector2);
						if (hit != null) {
							double distance = vector1.distanceTo(hit.hitVec);
							if ((distance < var6) || (var6 == 0.0D)) {
								var6 = distance;
								hit.entityHit = entity;
								movingObjectPosition = hit;
							}
						}
					}
				}
				x += motionX;
				y += motionY;
				z += motionZ;
				if (movingObjectPosition != null) {
					x = movingObjectPosition.hitVec.xCoord;
					y = movingObjectPosition.hitVec.yCoord;
					z = movingObjectPosition.hitVec.zCoord;
					break;
				}
				if (y <= -64.0D) {
					y = -64.0D;
					break;
				}
				float resistance = 0.99F;
				float gravity = throwable.getGravity();

				if (inWater) {
					resistance = 0.8F;
				}
				motionY -= gravity;
				motionX *= resistance;
				motionY *= resistance;
				motionZ *= resistance;
				vertex(x, y, z);
			}
			vertex(x, y, z);
			GL11.glEnd();
			if (movingObjectPosition != null) {
				if (movingObjectPosition.entityHit != null) {
					/*AxisAlignedBB boundingBox = movingObjectPosition.entityHit.boundingBox.expand(0.3D, 0.3D, 0.3D);

					System.out.println("yay");
					
					Render3DUtil.drawOutlineBox(boundingBox);
					
					// Render3D.drawOutlineBox(boundingBox.minX,
					// boundingBox.minY, boundingBox.minZ, boundingBox.maxX,
					// boundingBox.maxY, boundingBox.maxZ);

					GL11.glColor4d(0.25D, 0.8D, 1.0D, 0.125D);

					// Render3D.drawSkinBox(boundingBox.minX, boundingBox.minY,
					// boundingBox.minZ, boundingBox.maxX, boundingBox.maxY,
					// boundingBox.maxZ);

					GL11.glBegin(1);
					GL11.glEnd();*/
				} else {
					GL11.glPushMatrix();
					GL11.glTranslated(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ);
					switch (movingObjectPosition.sideHit) {
					case 4:
					case 5:
						GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
						break;
					case 2:
					case 3:
						GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
						break;
					default:
						GL11.glRotatef(-(player.prevRotationYawHead + (player.rotationYawHead - player.rotationYawHead) * render.getPartialTicks()), 0.0F, 1.0F, 0.0F);
					}
					GL11.glTranslated(-(x - RenderManager.renderPosX), -(y - RenderManager.renderPosY), -(z - RenderManager.renderPosZ));

					GL11.glBegin(2);
					vertex(x + 0.5D, y, z - 0.5D);
					vertex(x + 0.5D, y, z + 0.5D);
					vertex(x - 0.5D, y, z + 0.5D);
					vertex(x - 0.5D, y, z - 0.5D);
					GL11.glEnd();
					GL11.glColor4d(0.6D, 0, 0.6D, 0.35D);
					GL11.glBegin(1);
					vertex(x + 0.5D, y, z - 0.5D);
					vertex(x - 0.5D, y, z + 0.5D);
					vertex(x + 0.5D, y, z + 0.5D);
					vertex(x - 0.5D, y, z - 0.5D);
					GL11.glEnd();
					GL11.glColor4d(0.6D, 0, 0.6D, 0.125D);
					GL11.glDisable(2884);
					GL11.glBegin(7);
					vertex(x + 0.5D, y, z - 0.5D);
					vertex(x + 0.5D, y, z + 0.5D);
					vertex(x - 0.5D, y, z + 0.5D);
					vertex(x - 0.5D, y, z - 0.5D);
					GL11.glEnd();
					GL11.glEnable(2884);
					GL11.glPopMatrix();
				}
			}
			GL11.glEnable(2929);

			GL11.glDisable(3042);
			GL11.glEnable(3553);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glPopMatrix();

		}
	}

	private Vec3 isInWater(AxisAlignedBB boundingBox) {
		int minX = MathHelper.floor_double(boundingBox.minX);
		int maxX = MathHelper.floor_double(boundingBox.maxX + 1.0D);
		int minY = MathHelper.floor_double(boundingBox.minY);
		int maxY = MathHelper.floor_double(boundingBox.maxY + 1.0D);
		int minZ = MathHelper.floor_double(boundingBox.minZ);
		int maxZ = MathHelper.floor_double(boundingBox.maxZ + 1.0D);
		if (!Minecraft.getMinecraft().theWorld.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ)) { return null; }
		Vec3 velocity = null;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					if ((Wrapper.getWorld().getBlock(x, y, z).getMaterial() == Material.water) && (maxY >= y + 1 - BlockLiquid.getLiquidHeightPercent(Wrapper.getWorld().getBlockMetadata(x, y, z)))) {
						if (velocity == null) {
							velocity = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
						}
						Wrapper.getWorld().getBlock(x, y, z).velocityToAddToEntity(Minecraft.getMinecraft().theWorld, x, y, z, null, velocity);
					}
				}
			}
		}
		return velocity;
	}

	private Throwable getThrowable(EntityPlayer player, ItemStack item) {
		if ((item == null) || (item.getItem() == null)) { return null; }
		for (Throwable throwable : this.throwables) {
			if (throwable.checkItem(item)) { return throwable; }
		}
		return null;
	}

	private void vertex(double x, double y, double z) {
		GL11.glVertex3d(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ);
	}
}
