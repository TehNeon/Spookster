package net.spookysquad.spookster.mod.mods;

import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.spookysquad.spookster.Spookster;
import net.spookysquad.spookster.event.Event;
import net.spookysquad.spookster.event.events.EventPreMotion;
import net.spookysquad.spookster.mod.Module;
import net.spookysquad.spookster.mod.Type;
import net.spookysquad.spookster.utils.GameUtil;
import net.spookysquad.spookster.utils.PlayerUtil;

import org.lwjgl.input.Keyboard;

public class Speed extends Module {

	private int state = 0, timerState = 0;
	private float ground = 0;

	public Speed() {
		super(new String[] { "Speed" }, "Move faster.", Type.EXPLOITS,
				Keyboard.KEY_C, 0xFF96CC39);
	}
	
	@Override
	public boolean onEnable() {
        Blocks.ice.slipperiness = 0.6F;
        Blocks.packed_ice.slipperiness = 0.6F;
		return true;
	}
	
	@Override
	public boolean onDisable() {
		Spookster.instance.eventManager.unregisterListener(this);
		
		GameUtil.setGameSpeed(1.0F);
        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;
		return false;
	}

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventPreMotion) {
			EventPreMotion event = (EventPreMotion) e;
			
			double speed = 3.15;
			double slow = 1.462;
			double offset = 4.9F;
			float timer = 1.375F;
			
			if(PlayerUtil.isMoving()) {
				boolean iceBelow = false;
				boolean shouldSpeed = !getPlayer().isSneaking() && getPlayer().onGround;
				boolean shouldOffset = true;
				
				if(PlayerUtil.getBlock(-0.1D) instanceof BlockIce || PlayerUtil.getBlock(-0.1D) instanceof BlockPackedIce) {
					iceBelow = true;
					shouldSpeed = false;
				}

				boolean liquidBelow = false;
				if(PlayerUtil.getBlock(0.0D) instanceof BlockLiquid && (false || false) /** TODO: JESUS */) {
					liquidBelow = true;
					shouldSpeed = false;
				}

				boolean canStep = false;
				if(false /** TODO: STEP */) {
					for(Object o: getWorld().getCollidingBoundingBoxes(getPlayer(), getPlayer().boundingBox.copy().expand(0.5D, 0.0D, 0.5D))) {
						if(o instanceof AxisAlignedBB) {
							AxisAlignedBB bb = (AxisAlignedBB) o;
							
							if(bb != null) {
								canStep = true;
								shouldOffset = false;
							}
						}
					}
				}	
				
				if(iceBelow) {
					getPlayer().motionX *= 1.51D;
					getPlayer().motionZ *= 1.51D;
				}
				
				if(liquidBelow) {
					if(state > 2) {
						state = 0;
					}
					
					++state;
					switch(state) {
						case 1:
							getPlayer().motionX *= 1.5D;
							getPlayer().motionZ *= 1.5D;
							break;
						case 2:
							getPlayer().motionX /= 1.375D;
							getPlayer().motionZ /= 1.375D;
							this.state = 0;
							break;
					}
				}
				
				if(getPlayer().onGround && ground < 1) {
					ground += 0.2F;
				}
				
				if(!getPlayer().onGround) {
					ground = 0;
				}
				
				if(ground == 1 && shouldSpeed) {							
					if(!getPlayer().isSprinting()) {
						offset += 0.8D;
					}
					
					if(getPlayer().moveStrafing != 0.0F) {
						speed -= 0.1D;
						offset += 0.5D;
					}
					
					if(getPlayer().isInWater()) {
						speed -= 0.1D;
					}
					
					++state;
					switch(state) {
					case 1:
						GameUtil.setGameSpeed(timer);
						getPlayer().motionX *= speed;
						getPlayer().motionZ *= speed;
						break;
					case 2:
						GameUtil.setGameSpeed(1.0F);
						getPlayer().motionX /= slow;
						getPlayer().motionZ /= slow;
						break;
					case 3:
						GameUtil.setGameSpeed(1.05F);
						break;
					case 4:
						GameUtil.setGameSpeed(1.0F);
						
						if(shouldOffset) {
							getPlayer().setPosition(getPlayer().posX + getPlayer().motionX / offset, getPlayer().posY, getPlayer().posZ + getPlayer().motionZ / offset);
						}
						
						state = 0;
						break;
					}
				}else if(GameUtil.getGameSpeed() > 1.0F) {
					GameUtil.setGameSpeed(1.0F);
				}
			}else if(GameUtil.getGameSpeed() > 1.0F) {
				GameUtil.setGameSpeed(1.0F);
			}
		}
	}
}