package net.petercashel.PacProtect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;
import net.petercashel.PacProtect.Events.ChunkProtectionAddedEvent;
import net.petercashel.PacProtect.Events.ChunkProtectionFriendAddedEvent;
import net.petercashel.PacProtect.Events.ChunkProtectionFriendRemovedEvent;
import net.petercashel.PacProtect.Events.ChunkProtectionRemovedEvent;

public class ChunkProtectionManager {
	
	protected static List<ChunkProtectionDefinition> protectedChunks = new ArrayList<ChunkProtectionDefinition>();

	public static boolean addChunk (ChunkProtectionDefinition chunkProtectionDefinition) {
		for (int i = 0; i < protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = protectedChunks.get(i);
			if (d.ChunkX == chunkProtectionDefinition.ChunkX && d.ChunkZ == chunkProtectionDefinition.ChunkZ) {
				return false;
			}
		}
		protectedChunks.add(chunkProtectionDefinition);
		ChunkProtectionAddedEvent event = new ChunkProtectionAddedEvent(chunkProtectionDefinition);
		MinecraftForge.EVENT_BUS.post(event);
		return true;
	}

	public static boolean addChunk(UUID owner, int chunkX, int chunkZ) {
		return ChunkProtectionManager.addChunk(new ChunkProtectionDefinition(owner, chunkX, chunkZ));
	}
	
	public static boolean removeChunk (UUID Owner, int ChunkX, int ChunkZ) {
		for (int i = 0; i < protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = protectedChunks.get(i);
			if (d.ChunkX == ChunkX && d.ChunkZ == ChunkZ && d.Owner.compareTo(Owner) == 0) {
				protectedChunks.remove(i);
				ChunkProtectionRemovedEvent event = new ChunkProtectionRemovedEvent(d);
				MinecraftForge.EVENT_BUS.post(event);
				return true;
			}
		}
		return false;
	}

	public static boolean addFriend(UUID owner, int chunkX, int chunkZ, UUID id) {
		for (int i = 0; i < protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = protectedChunks.get(i);
			if (d.ChunkX == chunkX && d.ChunkZ == chunkZ && d.Owner.compareTo(owner) == 0) {
				d.addFriend(id);
				ChunkProtectionFriendAddedEvent event = new ChunkProtectionFriendAddedEvent(d);
				MinecraftForge.EVENT_BUS.post(event);
				return true;
			}
		}
		return false;
	}

	public static boolean removeFriend(UUID owner, int chunkX, int chunkZ,
			UUID id) {
		for (int i = 0; i < protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = protectedChunks.get(i);
			if (d.ChunkX == chunkX && d.ChunkZ == chunkZ && d.Owner.compareTo(owner) == 0) {
				d.delFriend(id);
				ChunkProtectionFriendRemovedEvent event = new ChunkProtectionFriendRemovedEvent(d);
				MinecraftForge.EVENT_BUS.post(event);
				return true;
			}
		}
		return false;
	}

}
