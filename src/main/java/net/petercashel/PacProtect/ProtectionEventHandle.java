package net.petercashel.PacProtect;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

public class ProtectionEventHandle {
	
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if (d.ChunkX == event.pickedUp.chunkCoordX && d.ChunkZ == event.pickedUp.chunkCoordX) {
				if (d.Owner.compareTo(event.player.getGameProfile().getId()) == 0 || d.hasFriend(event.player.getGameProfile().getId())) {
					System.out.println("Matching ");
				} else {
					System.out.println("Canceling ");
					System.out.println(event.isCancelable());
					event.setCanceled(true);
				}
				
			}
		}
		
		
	}
	
	@SubscribeEvent
	public void onEntityItemPickup(EntityItemPickupEvent  event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if (d.ChunkX == event.item.chunkCoordX && d.ChunkZ == event.item.chunkCoordX) {
				if (d.Owner.compareTo(event.entityPlayer.getGameProfile().getId()) == 0 || d.hasFriend(event.entityPlayer.getGameProfile().getId())) {
					System.out.println("Matching ");
				} else {
					System.out.println("Canceling ");
					System.out.println(event.isCancelable());
					event.setCanceled(true);
				}
				
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if ((d.ChunkX == (event.x / 16) && d.ChunkZ == (event.z / 16))) {
				if (d.Owner.compareTo(event.entityPlayer.getGameProfile().getId()) == 0 || d.hasFriend(event.entityPlayer.getGameProfile().getId())) {
					System.out.println("Matching ");
				} else {
					System.out.println("Canceling ");
					System.out.println(event.isCancelable());
					event.setCanceled(true);
				}
				
			}
			if ((d.ChunkX == (event.entityPlayer.chunkCoordX) && d.ChunkZ == (event.entityPlayer.chunkCoordZ))) {
				if (d.Owner.compareTo(event.entityPlayer.getGameProfile().getId()) == 0 || d.hasFriend(event.entityPlayer.getGameProfile().getId())) {
					System.out.println("Matching ");
				} else {
					System.out.println("Canceling ");
					System.out.println(event.isCancelable());
					event.setCanceled(true);
				}
				
			}
		}
	}

	@SubscribeEvent
	public void onBlockPlaceEvent(PlaceEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if (d.ChunkX == (event.x / 16) && d.ChunkZ == (event.z / 16)) {
				if (d.Owner.compareTo(event.player.getGameProfile().getId()) == 0 || d.hasFriend(event.player.getGameProfile().getId())) {
					System.out.println("Matching ");
				} else {
					System.out.println("Canceling ");
					System.out.println(event.isCancelable());
					event.setCanceled(true);
				}
				
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreakEvent(BreakEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if (d.ChunkX == (event.x / 16) && d.ChunkZ == (event.z / 16)) {
				if (d.Owner.compareTo(event.getPlayer().getGameProfile().getId()) == 0 || d.hasFriend(event.getPlayer().getGameProfile().getId())) {
					System.out.println("Matching ");
				} else {
					System.out.println("Canceling ");
					System.out.println(event.isCancelable());
					event.setCanceled(true);
				}
				
			}
		}
	}
}
