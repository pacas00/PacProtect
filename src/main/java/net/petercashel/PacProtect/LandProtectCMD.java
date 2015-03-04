package net.petercashel.PacProtect;

import java.util.List;
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

	 /**
     * Return the required permission level for this command.
     */
	@Override
	public int getRequiredPermissionLevel()
    {
        return 0;
    }
	
	@Override
	public List getCommandAliases()
    {
        return null;
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_)
    {
        return p_71519_1_.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredAdminPermissionLevel()
    {
        return 4;
    }

    @Override
	public String getCommandUsage(ICommandSender paramICommandSender) {
		// TODO Auto-generated method stub
		return "/protect {help|add|remove|friend}";
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
	{
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("friend")) {
				return args.length == 3 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers()) : null;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("friend")) {
				String[] subcmd = {"add","remove","addRadius","removeRadius"};
				return args.length == 2 ? getListOfStringsMatchingLastWord(args, subcmd) : null;
			}
		} else if (args.length == 1) {
			String[] subcmd = {"help","add","addRadius","friend","remove","removeRadius","admin"};
			return args.length == 1 ? getListOfStringsMatchingLastWord(args, subcmd) : null;
		}
		return null;
	}

	protected String[] getListOfPlayers()
	{
		return MinecraftServer.getServer().getAllUsernames();
	}

	@Override
	public void processCommand(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 1) throw new WrongUsageException("/protect {help|add|addRadius|friend|remove|removeRadius|admin}");
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}

		String cmd = args[0];

		if (cmd.equalsIgnoreCase("help")) help(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("add")) add(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("addRadius")) addRadius(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("remove")) remove(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("removeRadius")) removeRadius(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("friend")) friend(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("admin")) admin(paramICommandSender, args);

		else throw new WrongUsageException("/protect {help|add|addRadius|friend|remove|removeRadius|admin}");


	}

	private void friend(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 2) throw new WrongUsageException("/protect friend {add|remove|addRadius|removeRadius}");

		String cmd = args[1];

		if (cmd.equalsIgnoreCase("add")) addFriend(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("addRadius")) addFriendRadius(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("remove")) removeFriend(paramICommandSender, args);

		else if (cmd.equalsIgnoreCase("removeRadius")) removeFriendRadius(paramICommandSender, args);

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

	private void removeFriendRadius(ICommandSender paramICommandSender, String[] args) {
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;

		if (args.length < 4) throw new WrongUsageException("/protect friend removeRadius USERNAME RADIUS");
		int radius = Integer.valueOf(args[3]);
		if (radius > 4) throw new WrongUsageException("Radius must be smaller than 5");
		int added = 0;
		int failed = 0;
		for (int i = (ChunkX - radius); i < (ChunkX + radius); i++) {
			for (int j = (ChunkZ - radius); j < (ChunkZ + radius); j++) {
				try {
					EntityPlayer playerFriend = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[2]);

					boolean result = ChunkProtectionManager.removeFriend(Owner, ChunkX, ChunkZ, player.dimension, playerFriend.getGameProfile().getId());
					if (result) {
						added++;
					} else {
						failed++;
					}
				} catch (NullPointerException e) {
					throw new WrongUsageException("Player must be online to add");

				}

			}
		}
		ChatComponentText t = new ChatComponentText("Removed Friend from " + added + " Protections successfully.");
		paramICommandSender.addChatMessage(t);
		if (failed > 0) {
			ChatComponentText t2 = new ChatComponentText(failed + "Failed to remove Friend from " + failed +" Protections.");
			paramICommandSender.addChatMessage(t2);

		}


	}

	private void addFriendRadius(ICommandSender paramICommandSender, String[] args) {
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;

		if (args.length < 4) throw new WrongUsageException("/protect friend addRadius USERNAME RADIUS");
		System.out.println(args[2]);
		int radius = Integer.valueOf(args[3]);
		if (radius > 4) throw new WrongUsageException("Radius must be smaller than 5");
		int added = 0;
		int failed = 0;
		for (int i = (ChunkX - radius); i < (ChunkX + radius); i++) {
			for (int j = (ChunkZ - radius); j < (ChunkZ + radius); j++) {
				try {
					EntityPlayer playerFriend = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[2]);

					boolean result = ChunkProtectionManager.addFriend(Owner, ChunkX, ChunkZ, player.dimension, playerFriend.getGameProfile().getId());
					if (result) {
						added++;
					} else {
						failed++;
					}
				} catch (NullPointerException e) {
					throw new WrongUsageException("Player must be online to add");

				}

			}
		}
		ChatComponentText t = new ChatComponentText("Added Friend to " + added + " Protections successfully.");
		paramICommandSender.addChatMessage(t);
		if (failed > 0) {
			ChatComponentText t2 = new ChatComponentText(failed + "Failed to add Friend to " + failed +" Protections.");
			paramICommandSender.addChatMessage(t2);

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

	private void removeRadius(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 2) throw new WrongUsageException("/protect removeRadius RADIUS");
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;
		int radius = Integer.valueOf(args[1]);
		if (radius > 4) throw new WrongUsageException("Radius must be smaller than 5");
		int added = 0;
		int failed = 0;
		for (int i = (ChunkX - radius); i < (ChunkX + radius); i++) {
			for (int j = (ChunkZ - radius); j < (ChunkZ + radius); j++) {
				boolean result = ChunkProtectionManager.removeChunk(Owner, i, j, player.dimension);
				if (result) {
					added++;
				} else {
					failed++;
				}
			}
		}
		ChatComponentText t = new ChatComponentText(added + " Protections removed successfully.");
		paramICommandSender.addChatMessage(t);
		if (failed > 0) {
			ChatComponentText t2 = new ChatComponentText(failed + " Protections failed to be removed.");
			paramICommandSender.addChatMessage(t2);

		}
	}

	private void addRadius(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 2) throw new WrongUsageException("/protect addRadius RADIUS");
		EntityPlayer player = (EntityPlayer) paramICommandSender;

		UUID Owner = player.getGameProfile().getId();
		int ChunkX = player.chunkCoordX;
		int ChunkZ = player.chunkCoordZ;
		int radius = Integer.valueOf(args[1]);
		if (radius > 4) throw new WrongUsageException("Radius must be smaller than 5");
		int added = 0;
		int failed = 0;
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
		paramICommandSender.addChatMessage(new ChatComponentText("/protect {help|add|addRadius|friend|remove|removeRadius|admin}"));
	}

	private void admin(ICommandSender paramICommandSender, String[] args) {
		if (args.length < 2) throw new WrongUsageException("/protect admin {}");
		
		if (!(paramICommandSender.canCommandSenderUseCommand(4, "llllllll"))) {
			throw new WrongUsageException("Your not the boss of me.");
		}
		
		

	}



}
