package net.spookysquad.spookster.mod.mods;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.spookysquad.spookster.Spookster;
import net.spookysquad.spookster.event.Event;
import net.spookysquad.spookster.event.events.EventPacketSend;
import net.spookysquad.spookster.event.events.EventPreMotion;
import net.spookysquad.spookster.mod.Module;
import net.spookysquad.spookster.mod.Type;
import net.spookysquad.spookster.utils.PacketUtil;
import net.spookysquad.spookster.utils.PlayerUtil;
import net.spookysquad.spookster.utils.Wrapper;

import org.lwjgl.input.Keyboard;

public class NoFall extends Module {

	private float fallDistance = 0.0F;
	private int land = -1;
	
	public NoFall() {
		super(new String[] { "NoFall" }, "Prevent taking fall damage when falling off of a high distance.", Type.EXPLOITS, Keyboard.KEY_N, 0xFF13C422);
	}

	@Override
	public boolean onEnable() {
		PlayerUtil.inflictDamage(4);
		land = 1;
		
		return true;
	}
	
	@Override
	public boolean onDisable() {
		return false;
	}
	
	public boolean isSafe() {
		
		// TODO: Add more ways of checking if can land
		
		return Wrapper.getPlayer().isOnLadder() || Wrapper.getPlayer().isInWater();
	}
	
	@Override
	public void onEvent(Event e) {
		if(e instanceof EventPreMotion) {
			EventPreMotion event = (EventPreMotion) e;
			
			fallDistance += Wrapper.getPlayer().fallDistance;
			
			if(fallDistance > 3 && isSafe()) {
				land = 0;
			}
			
		}else if(e instanceof EventPacketSend) {
			EventPacketSend event = (EventPacketSend) e;
			
			if(event.getPacket() instanceof C03PacketPlayer) {
				C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
				
				if (packet instanceof C04PacketPlayerPosition) {
					packet = new C06PacketPlayerPosLook(((C04PacketPlayerPosition) packet).getPositionX(), ((C04PacketPlayerPosition) packet).getStance(), ((C04PacketPlayerPosition) packet).getPositionY(), ((C04PacketPlayerPosition) packet).getPositionZ(), Wrapper.getPlayer().rotationYaw, Wrapper.getPlayer().rotationPitch, Wrapper.getPlayer().onGround);
				}else if(packet instanceof C05PacketPlayerLook) {
					packet = new C06PacketPlayerPosLook(Wrapper.getPlayer().posX, Wrapper.getPlayer().boundingBox.minY, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ, ((C05PacketPlayerLook) packet).getYaw(), ((C05PacketPlayerLook) packet).getPitch(), Wrapper.getPlayer().onGround);
				}
				
				if(land == 1) {
					if(isEnabled()) {
						System.err.println("FALL DISTANCE RESET FGT");
					}
					
					fallDistance = 0;
					land = -1;
				}
				
				if(land < 1) {
//					event.cancel();
			        
					event.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getPositionX(), Wrapper.getPlayer().boundingBox.minY + (land == -1 ? 0.6F : 0.3F), Wrapper.getPlayer().posY + (land == -1 ? 0.6F : 0.3F), packet.getPositionZ(), packet.getYaw(), packet.getPitch(), false));
										
					if(land == 0) {
						land++;
					}
					
					if(!isEnabled()) {
						Spookster.instance.eventManager.unregisterListener(this);
					}
				}
			}
		}
	}	
}