package net.petercashel.PacProtect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.UsernameCache;

import org.apache.logging.log4j.Level;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import cpw.mods.fml.common.FMLLog;

public class DynmapSupport extends DynmapCommonAPIListener {

	/*
	 * Heavily based on DynmapTowny
	 * All Credits go to mikeprimm
	 */
	DynmapCommonAPI api;
	MarkerAPI markerapi;
	MarkerSet set;
	private static final String DEF_DESC = "<div class=\"infowindow\"><span style=\"font-size:120%;\">Protected Chunk</span><br /> Owner <span style=\"font-weight:bold;\">%owner%</span><br /> Friends <span style=\"font-weight:bold;\">%friends%</span><br/> ChunkX <span style=\"font-weight:bold;\">%chunkX%</span><br/> ChunkZ <span style=\"font-weight:bold;\">%chunkZ%</span><br/></div>";

	public List<ChunkProtectionDefinition> toProcess = new ArrayList();
	private boolean apiLive = false;
	private Map<String, AreaMarker> resareas = new HashMap<String, AreaMarker>();
	private Map<String, Marker> resmark = new HashMap<String, Marker>();


	public DynmapSupport() {
	}

	public void serverInit() {
		DynmapCommonAPIListener.register(this);
	}

	public void initPostAPI () {
		set = markerapi.getMarkerSet("pacprotect.markerset");
		if(set == null)
			set = markerapi.createMarkerSet("pacprotect.markerset", "PacProtect", null, false);
		else
			set.setMarkerSetLabel("PacProtect");
		if(set == null) {
			FMLLog.log("PacProtect", Level.ERROR, "Error creating marker set");
			return;
		}
		set.setHideByDefault(true);
	}

	public void addMarker(ChunkProtectionDefinition d) {
		if (!apiLive) {
			toProcess.add(d);
			return;
		}
		
		World markerWorld = null;
		for( World w : MinecraftServer.getServer().worldServers) {
			if (w.provider.dimensionId == d.Dim) markerWorld = w;
		}
		
		String polyid = "pp_dim" + d.Dim + "X" + d.ChunkX + "Z" + d.ChunkZ;
		String name = "Owned Chunk";
		double[] x = new double[4];
		double[] z = new double[4];
		x[0] = d.ChunkX * 16;
		z[0] =  d.ChunkZ * 16;
		x[1] = d.ChunkX * 16 + 16;
		z[1] =  d.ChunkZ * 16;
		x[2] = d.ChunkX * 16 + 16;
		z[2] =  d.ChunkZ * 16 + 16;
		x[3] = d.ChunkX * 16;
		z[3] =  d.ChunkZ * 16 + 16;

		AreaMarker m = resareas.remove(polyid);
		if(m == null) {
			m = set.createAreaMarker(polyid, name, false, getWorldName(markerWorld), x, z, false);
			if(m == null) {
				FMLLog.log("PacProtect", Level.INFO,"error adding area marker " + polyid);
				return;
			}
		}
		else {
			m.setCornerLocations(x, z); /* Replace corner locations */
			m.setLabel(name);   /* Update label */
		}

		//Fun Part
		String desc = formatInfoWindow(d);
		m.setDescription(desc); /* Set popup */
		resareas.put(polyid, m);
	}

	public void remMarker(ChunkProtectionDefinition d) {
		String polyid = "pp_dim" + d.Dim + "X" + d.ChunkX + "Z" + d.ChunkZ;
		AreaMarker m = resareas.remove(polyid);
		set.findAreaMarker(polyid).deleteMarker();
	}

	public void updateMarker(ChunkProtectionDefinition d) {
		addMarker(d);
	}

	@Override
	public void apiEnabled(DynmapCommonAPI paramDynmapCommonAPI) {
		api = paramDynmapCommonAPI;
		markerapi = api.getMarkerAPI();

		initPostAPI();

		apiLive = true;

		if (toProcess.size() > 0) {
			for (ChunkProtectionDefinition d : toProcess) {
				this.addMarker(d);
			}
		}
	}

	private String formatInfoWindow(ChunkProtectionDefinition d ) {
		String v = "<div class=\"chunkinfo\">"+DEF_DESC+"</div>";
		v = v.replace("%owner%", getUserName(d.Owner));
		String res = "";
		for(UUID r : d.friends) {
			if(res.length()>0) res += ", ";
			res += getUserName(r);
		}
		v = v.replace("%friends%", res);
		v = v.replace("%chunkX%", String.valueOf(d.ChunkX));
		v = v.replace("%chunkZ%", String.valueOf(d.ChunkZ));
		
		return v;
	}

	private String getUserName(UUID id) {
		
		String uname = null;
		if (UsernameCache.containsUUID(id)) uname = UsernameCache.getLastKnownUsername(id);
		if (uname == null || uname.isEmpty() || uname.equalsIgnoreCase("")) {
			uname = MinecraftServer.getServer().func_152358_ax().func_152652_a(id).getName();
		}
		
		if (uname == null || uname.isEmpty() || uname.equalsIgnoreCase("")) {
			uname = id.toString();
		}
		
		return uname;
	}

	public static String getWorldName(World w) {
		String n;	
		if (w.provider.dimensionId == 0) {
			n = w.getWorldInfo().getWorldName();
		} else
			n = "DIM" + w.provider.dimensionId;
		return n;
	}

}
