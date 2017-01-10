package com.gameinbucket.world;

import java.util.ArrayList;

import com.gameinbucket.rendering.ArrayBuffer;
import com.gameinbucket.rendering.MovingThing;

public class CubePortalMap 
{
	private CubeWorld world;
	private int sector_size;
	
	private ArrayList<ArrayBuffer> sector_list = new ArrayList<ArrayBuffer>();
	
	public int map_width;
	public int map_height;
	public int map_length;
	
	public CubeSector[][][] sector_map;

	public CubePortalMap(CubeWorld cw, int sector_dimensions)
	{
		world = cw;
		sector_size = sector_dimensions;
		
		map_width = (int)Math.ceil((double)world.width / (double)sector_size);
		map_height = (int)Math.ceil((double)world.height / (double)sector_size);
		map_length = (int)Math.ceil((double)world.length / (double)sector_size);
		
		sector_map = new CubeSector[map_width][map_height][map_length];
		
		//divide portions of map into sections
		for (int w = 0; w < map_width; w++)
		{
			for (int h = 0; h < map_height; h++)
			{
				for (int l  = 0; l < map_length; l++)
				{
					int x2 = (w + 1) * sector_size;
					int y2 = (h + 1) * sector_size;
					int z2 = (l + 1) * sector_size;
					
					if (x2 > world.width)
						x2 = world.width;
					
					if (y2 > world.height)
						y2 = world.height;
					
					if (z2 > world.length)
						z2 = world.length;
					
					sector_map[w][h][l] = new CubeSector(w * sector_size, h * sector_size, l * sector_size, x2, y2, z2);
				}
			}
		}
		
		evaluate_sectors();
	}
	
	private void evaluate_sectors()
	{
		//set connections between sectors
		for (int w = 0; w < map_width; w++)
		{
			for (int h = 0; h < map_height; h++)
			{
				for (int l  = 0; l < map_length; l++)
				{
					sector_map[w][h][l].left = w > 0 ? sector_map[w - 1][h][l] : null;
					sector_map[w][h][l].right = w < map_width - 1 ? sector_map[w + 1][h][l] : null;
					sector_map[w][h][l].bottom = h > 0 ? sector_map[w][h - 1][l] : null;
					sector_map[w][h][l].top = h < map_height - 1 ? sector_map[w][h + 1][l] : null;
					sector_map[w][h][l].back = l > 0 ? sector_map[w][h][l - 1] : null;
					sector_map[w][h][l].front = l < map_length - 1 ? sector_map[w][h][l + 1] : null;
				}
			}
		}
	}
	
	private void sector_recurse(CubeSector s, int length)
	{
		if (sector_list.contains(s.buffer) || length > 4)
			return;
		
		sector_list.add(s.buffer);
		length++;
		
		if (s.left != null) //@270
			sector_recurse(s.left, length);
		
		if (s.right != null) //@90
			sector_recurse(s.right, length);
		
		if (s.front != null) //@180
			sector_recurse(s.front, length);
		
		if (s.back != null) //@0
			sector_recurse(s.back, length);
	}

	/*private void sector_recurse_frustum(FrustumCuller f, CubeSector s)
	{
		if (sector_list.contains(s.buffer))
			return;
		
		if (!f.square_in_frustum(s.xmid, s.zmid, s.size))
			return;
		
		sector_list.add(s.buffer);
		
		if (s.left != null)
			sector_recurse_frustum(f, s.left);
		
		if (s.right != null)
			sector_recurse_frustum(f, s.right);
		
		if (s.front != null)
			sector_recurse_frustum(f, s.front);
		
		if (s.back != null)
			sector_recurse_frustum(f, s.back);
	}*/
	
	public ArrayList<ArrayBuffer> render_list(MovingThing v)
	{
		sector_list.clear();
		sector_recurse(sector_map[(int)(v.view_p[0] / world.scale) / sector_size][0][(int)(v.view_p[2] / world.scale) / sector_size], 0);
		//CubeSector s = sector_map[(int)(v.view_p[0] / world.scale) / sector_size][0][(int)(v.view_p[2] / world.scale) / sector_size];
		//sector_list.add(s.buffer);
		
		//com.gameinbucket.thanatopsis.Main.print("xm, xm, s: " + s.xmid + ", " + s.zmid + ", " + s.size);
		
		//if (f.square_in_frustum(s.xmid, s.zmid, s.size))
		//	com.gameinbucket.thanatopsis.Main.print("it is!");
		//sector_recurse_frustum(f, sector_map[(int)(v.view_p[0] / world.scale) / sector_size][0][(int)(v.view_p[2] / world.scale) / sector_size]);
		
		return sector_list;
	}
}
