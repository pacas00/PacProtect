package net.petercashel.PacProtect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkProtectionDefinition {
	
	public UUID Owner;
	public int ChunkX = 0;
	public int ChunkZ = 0;
	boolean hasFriends = false;
	List<UUID> friends = new ArrayList<UUID>();
	
	public ChunkProtectionDefinition ( UUID owner, int chunkX, int chunkZ ) {
		this.ChunkX = chunkX;
		this.ChunkZ = chunkZ;
		this.Owner = owner;
	}
	
	public boolean addFriend (UUID friend) {
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).compareTo(friend) == 0) {
				return false;
			}
		}
		friends.add(friend);
		hasFriends = true;
		return true;
	}
	
	public boolean delFriend (UUID friend) {
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).compareTo(friend) == 0) {
				friends.remove(friend);
				return true;
			}
		}
		if (friends.isEmpty()) hasFriends = false;
		return false;
	}
	
	public List<UUID> getFriends() {
		return friends;
	}

	public boolean hasFriend(UUID id) {
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).compareTo(id) == 0) {
				return true;
			}
		}
		return false;
	}
}
