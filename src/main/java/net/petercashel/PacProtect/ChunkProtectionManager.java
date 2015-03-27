package net.petercashel.PacProtect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.server.MinecraftServer;
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
		if (mod_PacProtect.dynmap != null) mod_PacProtect.dynmap.addMarker(chunkProtectionDefinition);
		save();
		return true;
	}

	public static boolean addChunk(UUID owner, int chunkX, int chunkZ, int dim) {
		return ChunkProtectionManager.addChunk(new ChunkProtectionDefinition(owner, chunkX, chunkZ, dim));
	}

	public static boolean removeChunk (UUID Owner, int ChunkX, int ChunkZ, int dim) {
		for (int i = 0; i < protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = protectedChunks.get(i);
			if (d.ChunkX == ChunkX && d.ChunkZ == ChunkZ && d.Owner.compareTo(Owner) == 0) {
				protectedChunks.remove(i);
				ChunkProtectionRemovedEvent event = new ChunkProtectionRemovedEvent(d);
				MinecraftForge.EVENT_BUS.post(event);
				if (mod_PacProtect.dynmap != null) mod_PacProtect.dynmap.remMarker(d);
				return true;
			}
		}
		return false;
	}

	public static boolean addFriend(UUID owner, int chunkX, int chunkZ, int dim, UUID id) {
		for (int i = 0; i < protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = protectedChunks.get(i);
			if (d.ChunkX == chunkX && d.ChunkZ == chunkZ && d.Owner.compareTo(owner) == 0) {
				int index = i;
				d.addFriend(id);
				protectedChunks.set(index, d);
				ChunkProtectionFriendAddedEvent event = new ChunkProtectionFriendAddedEvent(d);
				MinecraftForge.EVENT_BUS.post(event);
				if (mod_PacProtect.dynmap != null) mod_PacProtect.dynmap.updateMarker(d);
				return true;
			}
		}
		return false;
	}

	public static boolean removeFriend(UUID owner, int chunkX, int chunkZ, int dim,
			UUID id) {
		for (int i = 0; i < protectedChunks.size(); i++) {
			ChunkProtectionDefinition d = protectedChunks.get(i);
			if (d.ChunkX == chunkX && d.ChunkZ == chunkZ && d.Owner.compareTo(owner) == 0) {
				int index = i;
				d.delFriend(id);
				protectedChunks.set(index, d);
				ChunkProtectionFriendRemovedEvent event = new ChunkProtectionFriendRemovedEvent(d);
				MinecraftForge.EVENT_BUS.post(event);
				if (mod_PacProtect.dynmap != null) mod_PacProtect.dynmap.updateMarker(d);
				return true;
			}
		}
		return false;
	}

	public static boolean save() {

		JsonArray jsonRoot = new JsonArray();

		Iterator<ChunkProtectionDefinition> iterator = protectedChunks.iterator();
		while (iterator.hasNext()) {
			ChunkProtectionDefinition p = iterator.next();
			JsonObject jobj = new JsonObject();

			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ChunkProtectionDefinition.class, new ChunkProtectionDefinitionTypeAdapter());
			gsonBuilder.setPrettyPrinting();

			final Gson gson = gsonBuilder.create();
			final String json = gson.toJson(p);

			JsonParser parse = new JsonParser();

			jsonRoot.add(parse.parse(json));
		}

		// Use GSON to pretty up my JSON.Simple
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString;
		jsonString = gson.toJson(jsonRoot);
		// done

		String dirPath = "";
		if (MinecraftServer.getServer().isDedicatedServer()) {
			dirPath = MinecraftServer.getServer().getFolderName() + File.separator + "PacProtect" + File.separator;
		} else {
			dirPath = "saves" + MinecraftServer.getServer().getFolderName() + File.separator + "PacProtect" + File.separator;
		}

		FileOutputStream fop = null;
		File file;
		File dir = new File(dirPath);
		dir.mkdirs();
		String content = jsonString;

		try {

			file = new File(dir, "protectedAreas.json");
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static boolean load() {
		String content = "";
		String dirPath = "";
		if (MinecraftServer.getServer().isDedicatedServer()) {
			dirPath = MinecraftServer.getServer().getFolderName() + File.separator + "PacProtect" + File.separator;
		} else {
			dirPath = "saves" + File.separator + MinecraftServer.getServer().getFolderName() + File.separator + "PacProtect" + File.separator;
		}
		new File(dirPath).mkdir();

		try {
			content = readFile(dirPath + "protectedAreas.json", Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		JsonElement jelement = new JsonParser().parse(content);
		JsonArray jarray = jelement.getAsJsonArray();

		Iterator<JsonElement> iterator = jarray.iterator();
		while (iterator.hasNext()) {
			JsonElement e = iterator.next();

			String json = e.toString();
			System.out.println(json);

			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ChunkProtectionDefinition.class, new ChunkProtectionDefinitionTypeAdapter());
			gsonBuilder.setPrettyPrinting();

			final Gson gson = gsonBuilder.create();

			ChunkProtectionDefinition d = gson.fromJson(json, ChunkProtectionDefinition.class);

			protectedChunks.add(d);
			if (mod_PacProtect.dynmap != null) mod_PacProtect.dynmap.addMarker(d);
		}

		return true;

	}

	public static String readFile(String path, Charset encoding) throws IOException {

		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void reload() {
		protectedChunks = null;
		protectedChunks = new ArrayList<ChunkProtectionDefinition>();
		if (mod_PacProtect.dynmap != null) mod_PacProtect.dynmap.purgeSet();
		load();		
	}



}
