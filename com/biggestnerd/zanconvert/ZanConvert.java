package com.biggestnerd.zanconvert;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ZanConvert {

	private ArrayList<Waypoint> waypoints;
	
	public static void main(String[] args) {
		ZanConvert converter = new ZanConvert();
		converter.init();
	}
	
	public void init() {
		waypoints = new ArrayList<Waypoint>();
		File mcDir = new File(System.getProperty("user.dir"));
		File zansDir = new File(mcDir, "mods/VoxelMods/voxelMap");
		if(!zansDir.exists()) {
			System.out.println("there are no zans waypoints :/");
			System.exit(0);
		}
		File civDir = new File(mcDir, "civRadar");
		if(!civDir.isDirectory()) {
			civDir.mkdir();
		}
		File civPointDir = new File(civDir, "waypoints");
		if(!civPointDir.isDirectory()) {
			civPointDir.mkdir();
		}
		
		try {
			convert(zansDir, civPointDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void convert(File zansDir, File civPointDir) throws Exception {
		for(File f : zansDir.listFiles()) {
			if(f.getName().endsWith(".points")) {
				File civPointFile = new File(civPointDir, f.getName());
				Pattern pointPattern = Pattern.compile("name:(.*),x:([-]?[0-9]+),z:([-]?[0-9]+),y:([-]?[0-9]+),enabled:(true|false),red:([0-1]\\.[0-9]+),green:([0-1]\\.[0-9]+),blue:([0-1]\\.[0-9]+),suffix:,world:,dimensions:(0|-1|1).*");
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String line;
				while((line = reader.readLine()) != null) {
					Matcher pointMatcher = pointPattern.matcher(line);
					while(pointMatcher.find()) {
						waypoints.add(new Waypoint(
								Integer.parseInt(pointMatcher.group(2)),
								Integer.parseInt(pointMatcher.group(3)),
								Integer.parseInt(pointMatcher.group(4)),
								pointMatcher.group(1),
								Float.parseFloat(pointMatcher.group(6)),
								Float.parseFloat(pointMatcher.group(7)), 
								Float.parseFloat(pointMatcher.group(8)),
								Boolean.getBoolean(pointMatcher.group(5)),
								Integer.parseInt(pointMatcher.group(9))
								));
					}
				}
				reader.close();
				save(civPointFile);
				waypoints.clear();
			}
		}
	}
	
	public void save(File file) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			String json = gson.toJson(this);
			
			FileWriter fw = new FileWriter(file);
			fw.write(json);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class Waypoint {
		public double x, y, z;
		public String name;
		public int dimension;
		public boolean enabled;
		public float red, green, blue;
		
		public Waypoint(int x, int y, int z, String name, float r, float g, float b, boolean enabled, int id) {
			this.x = x + 0.5D;
			this.z = z + 0.5D;
			this.y = y;
			this.name = name;
			red = r;
			green = g;
			blue = b;
			this.dimension = id;
			this.enabled = enabled;
		}
	}
}
