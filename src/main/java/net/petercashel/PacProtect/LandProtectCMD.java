package net.petercashel.PacProtect;

import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class LandProtectCMD extends CommandBase {

	@Override
	public String getCommandName() {
		return "protect";
	}

	@Override
	public String getCommandUsage(ICommandSender paramICommandSender) {
		// TODO Auto-generated method stub
		return "/protect {help|add|remove|friend}";
	}

	@Override
	public void processCommand(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 1) throw new WrongUsageException("/protect {help|add|remove|friend}");
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}

		String cmd = args[0];

		if (cmd.equalsIgnoreCase("help")) help(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("add")) add(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("addRadius")) addRadius(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("remove")) remove(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("friend")) friend(paramICommandSender, args);
		
		else if (cmd.equalsIgnoreCase("admin")) admin(paramICommandSender, args);

		else throw new WrongUsageException("/protect {help|add|remove|friend}");


	}

	private void friend(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 2) throw new WrongUsageException("/protect friend {add|remove}");

		String cmd = args[1];

		if (cmd.equalsIgnoreCase("add")) addFriend(paramICommandSender, args);
		
		else if (cmd.equalsIgnoreCase("remove")) removeFriend(paramICommandSender, args);
		
		else throw new WrongUsageException("/protect friend {add|remove}");


	}

	private void removeFriend(ICommandSender paramICommandSender, String[] args) {
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;

		if (args.length < 3) throw new WrongUsageException("/protect friend remove USERNAME");

		try {
			EntityPlayer playerFriend = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[2]);

			boolean result = ChunkProtectionManager.removeFriend(Owner, ChunkX, ChunkZ, player.dimension, playerFriend.getGameProfile().getId());
			if (result) {
				ChatComponentText t = new ChatComponentText("Friend removed successfully.");
				paramICommandSender.addChatMessage(t);
			} else {
				ChatComponentText t = new ChatComponentText("Friend could not be removed.");
				paramICommandSender.addChatMessage(t);
			}
		} catch (NullPointerException e) {
			throw new WrongUsageException("Player must be online to add");
		}

	}

	private void addFriend(ICommandSender paramICommandSender, String[] args) {
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;

		if (args.length < 3) throw new WrongUsageException("/protect friend add USERNAME");
		System.out.println(args[2]);
		try {
			EntityPlayer playerFriend = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[2]);

			boolean result = ChunkProtectionManager.addFriend(Owner, ChunkX, ChunkZ, player.dimension, playerFriend.getGameProfile().getId());
			if (result) {
				ChatComponentText t = new ChatComponentText("Friend added successfully.");
				paramICommandSender.addChatMessage(t);
			} else {
				ChatComponentText t = new ChatComponentText("Friend could not be added.");
				paramICommandSender.addChatMessage(t);
			}
		} catch (NullPointerException e) {
			throw new WrongUsageException("Player must be online to add");
		}

	}

	private void remove(ICommandSender paramICommandSender, String[] args) {

		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;

		boolean result = ChunkProtectionManager.removeChunk(Owner, ChunkX, ChunkZ, player.dimension);
		if (result) {
			ChatComponentText t = new ChatComponentText("Protection removed successfully.");
			paramICommandSender.addChatMessage(t);
		} else {
			ChatComponentText t = new ChatComponentText("Protection could not be removed.");
			paramICommandSender.addChatMessage(t);
		}


	}

	private void addRadius(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 2) throw new WrongUsageException("/protect add RADIUS");
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;
		int radius = Integer.valueOf(args[1]);
		if (radius > 4) throw new WrongUsageException("Radius must be smaller than 5");
		int added = 0;
		int failed = 0;
		//ChunkProtectionManager.addChunk(Owner, ChunkX, ChunkZ, player.dimension);
		for (int i = (ChunkX - radius); i < (ChunkX + radius); i++) {
			for (int j = (ChunkZ - radius); j < (ChunkZ + radius); j++) {
				boolean result = ChunkProtectionManager.addChunk(Owner, i, j, player.dimension);
				if (result) {
					added++;
				} else {
					failed++;
				}
			}
		}
			ChatComponentText t = new ChatComponentText(added + " Protections added successfully.");
			paramICommandSender.addChatMessage(t);
			if (failed > 0) {
				ChatComponentText t2 = new ChatComponentText(failed + " Protections failed to be added.");
				paramICommandSender.addChatMessage(t2);

			}
	}
	
	private void add(ICommandSender paramICommandSender, String[] args) {
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;

		boolean result = ChunkProtectionManager.addChunk(Owner, ChunkX, ChunkZ, player.dimension);
		if (result) {
			ChatComponentText t = new ChatComponentText("Protection added successfully.");
			paramICommandSender.addChatMessage(t);
		} else {
			ChatComponentText t = new ChatComponentText("Protection already exists.");
			paramICommandSender.addChatMessage(t);
		}
	}

	private void help(ICommandSender paramICommandSender, String[] args) {
		// TODO Auto-generated method stub
		paramICommandSender.addChatMessage(new ChatComponentText("/protect {help|add|remove|friend}"));

	}
	
	private void admin(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 2) throw new WrongUsageException("/protect admin {}");
		
	}

	

}
