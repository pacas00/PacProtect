package net.petercashel.PacProtect;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ChunkProtectionDefinitionTypeAdapter extends TypeAdapter<ChunkProtectionDefinition> {

	@Override
	public void write( final JsonWriter out, final ChunkProtectionDefinition d) throws IOException {
		out.beginObject();
		out.name("Dim").value(d.Dim);
		out.name("ChunkX").value(d.ChunkX);
		out.name("ChunkZ").value(d.ChunkZ);
		out.name("Owner").value(d.Owner.toString());
		String s = "";
		for (int i = 0; i < d.friends.size(); i++) {
			if (!d.friends.isEmpty()) {
				if (i == 0) {
					s = d.friends.get(i).toString();
				} else {
					s = s + ";" + d.friends.get(i).toString();
				}
			}

		}
		out.name("Friends").value(s);
		out.endObject();

	}

	@Override
	public ChunkProtectionDefinition read( final JsonReader in) throws IOException {
		final ChunkProtectionDefinition d = new ChunkProtectionDefinition();

		in.beginObject();
		while (in.hasNext()) {
			String s = in.nextName();
			if (s.equalsIgnoreCase("ChunkX")) {
				d.ChunkX = Integer.parseInt((in.nextString()));
			} else if (s.equalsIgnoreCase("ChunkZ")) {
				d.ChunkZ = Integer.parseInt((in.nextString()));
			} else if (s.equalsIgnoreCase("Dim")) {
				d.Dim = Integer.parseInt((in.nextString()));
			} else if (s.equalsIgnoreCase("Owner")) {
				d.Owner = UUID.fromString((in.nextString()));
			} else if (s.equalsIgnoreCase("Friends")) {
				String st = in.nextString();
				String[] fr = st.split(";");
				if (fr.length == 0 || st.isEmpty() || st.equalsIgnoreCase("")) { } else {
					for (int i = 0; i < fr.length; i++) {
						d.friends.add(UUID.fromString(fr[i]));
					}
				}
			}
		}
		in.endObject();

		return d;
	}

}
