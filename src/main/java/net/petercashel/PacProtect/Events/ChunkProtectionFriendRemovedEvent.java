package net.petercashel.PacProtect.Events;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.petercashel.PacProtect.ChunkProtectionDefinition;
import cpw.mods.fml.common.eventhandler.Event;

public class ChunkProtectionFriendRemovedEvent extends Event {
	public final int ChunkX;
	public final int ChunkZ;
	public final UUID Owner;
	public final List<UUID> friends;
	
	public ChunkProtectionFriendRemovedEvent(ChunkProtectionDefinition d) {
		this.ChunkX = d.ChunkX;
		this.ChunkZ = d.ChunkZ;
		this.Owner = d.Owner;
		this.friends = d.getFriends();
	}
	

}
