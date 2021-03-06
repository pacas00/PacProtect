package net.petercashel.PacProtect;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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
			if (d.ChunkX == event.pickedUp.chunkCoordX && d.ChunkZ == event.pickedUp.chunkCoordX && d.Dim == (event.pickedUp.dimension)) {
				if (d.Owner.compareTo(event.player.getGameProfile().getId()) == 0 || d.hasFriend(event.player.getGameProfile().getId()) || event.player instanceof FakePlayer) {
				} else {
					if (event.isCancelable()) event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityItemPickup(EntityItemPickupEvent  event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if (d.ChunkX == event.item.chunkCoordX && d.ChunkZ == event.item.chunkCoordX && d.Dim == (event.item.dimension)) {
				if (d.Owner.compareTo(event.entityPlayer.getGameProfile().getId()) == 0 || d.hasFriend(event.entityPlayer.getGameProfile().getId()) || event.entityPlayer instanceof FakePlayer) {
				} else {
					if (event.isCancelable()) event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if ((d.ChunkX == (event.x / 16) && d.ChunkZ == (event.z / 16) && d.Dim == event.world.provider.dimensionId)) {
				if (d.Owner.compareTo(event.entityPlayer.getGameProfile().getId()) == 0 || d.hasFriend(event.entityPlayer.getGameProfile().getId()) || event.entityPlayer instanceof FakePlayer) {
				} else {
					if (event.isCancelable()) event.setCanceled(true);
				}
			}
			if ((d.ChunkX == (event.entityPlayer.chunkCoordX) && d.ChunkZ == (event.entityPlayer.chunkCoordZ))) {
				if (d.Owner.compareTo(event.entityPlayer.getGameProfile().getId()) == 0 || d.hasFriend(event.entityPlayer.getGameProfile().getId()) || event.entityPlayer instanceof FakePlayer) {
				} else {
					if (event.isCancelable()) event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockPlaceEvent(PlaceEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if (d.ChunkX == (event.x / 16) && d.ChunkZ == (event.z / 16) && d.Dim == event.world.provider.dimensionId) {
				if (d.Owner.compareTo(event.player.getGameProfile().getId()) == 0 || d.hasFriend(event.player.getGameProfile().getId()) || event.player instanceof FakePlayer) {
				} else {
					if (event.isCancelable()) event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreakEvent(BreakEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if (d.ChunkX == (event.x / 16) && d.ChunkZ == (event.z / 16) && d.Dim == event.world.provider.dimensionId) {
				if (d.Owner.compareTo(event.getPlayer().getGameProfile().getId()) == 0 || d.hasFriend(event.getPlayer().getGameProfile().getId()) || event.getPlayer() instanceof FakePlayer) {
				} else {
					if (event.isCancelable()) event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if ((d.ChunkX == (event.target.chunkCoordX) && d.ChunkZ == (event.target.chunkCoordZ) && d.Dim == (event.target.dimension))) {
				if (d.Owner.compareTo(event.entityPlayer.getGameProfile().getId()) == 0 || d.hasFriend(event.entityPlayer.getGameProfile().getId()) || event.entityPlayer instanceof FakePlayer) {
				} else {
					if (event.isCancelable()) event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if ((d.ChunkX == (event.entity.chunkCoordX) && d.ChunkZ == (event.entity.chunkCoordZ) && d.Dim == (event.entity.dimension))) {
				if (event.source.getEntity() instanceof EntityPlayer) {
					EntityPlayer plr = (EntityPlayer) event.source.getEntity();
					if (d.Owner.compareTo(plr.getGameProfile().getId()) == 0 || d.hasFriend(plr.getGameProfile().getId()) || event.source.getEntity() instanceof FakePlayer) {
					} else {
						if (event.isCancelable()) event.setCanceled(true);
					}
				} else {
					if (event.source.getEntity() instanceof EntityMob) {
						EntityMob mob = (EntityMob) event.source.getEntity();
						if (event.isCancelable()) event.setCanceled(true);
						mob.setDead();
					}
					if (event.source.getEntity() instanceof EntitySlime) {
						EntitySlime mob = (EntitySlime) event.source.getEntity();
						if (event.isCancelable()) event.setCanceled(true);
						mob.setDead();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingSpawnEvent(LivingSpawnEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if ((d.ChunkX == (event.entity.chunkCoordX) && d.ChunkZ == (event.entity.chunkCoordZ) && d.Dim == (event.entity.dimension))) {
				if (event.entity instanceof EntityMob) {
					if (event.isCancelable()) event.setCanceled(true);
					else event.entity.setDead();
				}
				if (event.entity instanceof EntitySlime) {
					if (event.isCancelable()) event.setCanceled(true);
					else event.entity.setDead();
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingJumpEvent(LivingJumpEvent event)
	{
		for (int i = 0; i < ChunkProtectionManager.protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = ChunkProtectionManager.protectedChunks.get(i);
			if ((d.ChunkX == (event.entity.chunkCoordX) && d.ChunkZ == (event.entity.chunkCoordZ) && d.Dim == (event.entity.dimension))) {
				if (event.entity instanceof EntityMob) {
					event.entity.setDead();
				}
				if (event.entity instanceof EntitySlime) {
					event.entity.setDead();
				}
			}
		}
	}
}
