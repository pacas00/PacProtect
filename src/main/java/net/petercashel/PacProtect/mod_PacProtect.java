package net.petercashel.PacProtect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;


@Mod(modid = "mod_PacProtect", name = "PacProtect", version = "mod_PacProtect")
public class mod_PacProtect {
	
    @Instance(value = "mod_PacProtect")
	public static mod_PacProtect instance;
	
	public static final String VERSION = "@VERSION@";
	
	@SidedProxy(clientSide = "net.petercashel.PacProtect.ClientProxy", serverSide = "net.petercashel.PacProtect.CommonProxy")
	public static CommonProxy proxy;

	
	public static final String CATEGORY_GENERAL = "general";

	private MinecraftServer server;

	private LandProtectCMD cmd;
	Timer saveTimer = new Timer();
	
	static DynmapSupport dynmap = null;
	
	File configdir = new File("config" + File.separator + "PacProtect");
	File config = new File("config" + File.separator + "PacProtect" + File.separator + "PacProtect.cfg");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configdir.mkdirs();
		Configuration cfg = new Configuration(config);
		try {
			cfg.load();
			
			//anvilBadItem = cfg.get(CATEGORY_GENERAL, "anvilInvalidItemChat", false).getBoolean(false);

			
		} catch (Exception e) {
			System.out.println("[PacProtect] Error Loading Config");
		} finally {
			cfg.save();
		}
		if(Loader.isModLoaded("Dynmap")) dynmap = new DynmapSupport();

	}

	@EventHandler
	public void init(FMLInitializationEvent event){

		proxy.init();
		
		System.out.println("[PacProtect] initialised.");
		FMLLog.log("PacProtect", Level.INFO, "Mod Has Loaded [PacProtect]");
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new ProtectionEventHandle());
		MinecraftForge.EVENT_BUS.register(new ProtectionEventHandle());
		
//		//List FakePlayers
//		//net.minecraftforge.common.util.FakePlayerFactory.fakePlayers;
//		Class FPF = null;
//		try {
//			FPF = this.getClass().forName("net.minecraftforge.common.util.FakePlayerFactory");
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Field[] f = FPF.getDeclaredFields();
//		Object fkPlrs = null;
//		for (int i = 0; i < f.length; i++) {
//			if (f[i].getName().contains("fakePlayers")) {
//				try {
//					f[i].setAccessible(true);
//					fkPlrs = f[i].get(null);
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				FMLLog.log("PacProtect", Level.INFO, f[i].getName());
//				System.out.println(f[i].getName());
//			}
//		}
//		Map<GameProfile, FakePlayer> fakePlayers = (Map<GameProfile, FakePlayer>)(fkPlrs);
//		if (fakePlayers != null) {
//			if (!fakePlayers.isEmpty()) {
//				Iterator it = fakePlayers.keySet().iterator();
//				while (it.hasNext()) {
//					GameProfile n = (GameProfile) it.next();
//					FMLLog.log("PacProtect", Level.INFO, "FakePlayer: " + n.getName());
//					System.out.println("FakePlayer: " + n.getName());
//				}
//			}
//		}
		
	}
	
	@EventHandler
	public void ServerStarting(FMLServerStartingEvent event) 
	{
		server = MinecraftServer.getServer();
		ServerCommandManager commands = (ServerCommandManager) server.getCommandManager();
		cmd = new LandProtectCMD();
		commands.registerCommand(cmd);

		if(Loader.isModLoaded("Dynmap")) dynmap.serverInit();
		
		

	}
	
	@EventHandler
	public void ServerStarted(FMLServerStartedEvent event) 
	{
		//Load ProtectedChunks
	    ChunkProtectionManager.load();
	    saveTimer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
			    ChunkProtectionManager.save();
			  }
			}, 5*60*1000, 5*60*1000);
	}
	
	@EventHandler
	public void ServerStopping(FMLServerStoppingEvent event) 
	{
		saveTimer.cancel();
		saveTimer.purge();
		//Save ProtectedChunks
	    ChunkProtectionManager.save();
		
	}
	
    @NetworkCheckHandler
    public boolean netCheckHandler(Map<String, String> mods, Side side) {
        return true;
    }
}