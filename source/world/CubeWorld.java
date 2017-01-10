package com.gameinbucket.world;

import java.util.ArrayList;

import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.GeometryBuffer;
import com.gameinbucket.rendering.VectorMath;

public class CubeWorld 
{
	private GeometryBuffer geometry_world;

	public float scale;
	private float texture_scale;

	public int width;
	public int height;
	public int length;

	private int[][][] type_world;
	private ArrayList<float[][]> cubes;

	public CubePortalMap portal_map;

	public CubeWorld(float s, float texture_s, int w, int h, int l, int sector_limit)
	{
		scale = s;
		texture_scale = texture_s;

		width = w;
		height = h;
		length = l;

		type_world = new int[width][height][length];

		for (int ww = 0; ww < width; ww++)
			for (int hh = 0; hh < height; hh++)
				for (int ll  = 0; ll < length; ll++)
					type_world[ww][hh][ll] = -1;

		cubes = new ArrayList<float[][]>();

		//allocate enough memory for a single sector
		{
			int x_limit = sector_limit < width ? sector_limit : width;
			int y_limit = sector_limit < height ? sector_limit : height;
			int z_limit = sector_limit < length ? sector_limit : length;

			int cube_limit = x_limit * y_limit * z_limit;
			geometry_world = new GeometryBuffer(cube_limit * 4, cube_limit * 6);
		}

		portal_map = new CubePortalMap(this, sector_limit);
	}

	public void add_cube_type(float t1x, float t1y, float t2x, float t2y)
	{
		float[][] cube = VectorMath.create_cube(scale, texture_scale, t1x, t1y, t2x, t2y);
		cubes.add(cube);
	}

	public void set_cube(int i, int w, int h, int l)
	{
		if (w < 0 || h < 0 || l < 0 || w >= width || h >= height || l >= length)
			return;

		type_world[w][h][l] = i;
	}

	public void set_cube_floor_ceil(int floor_ceil, int height, int x, int base_y, int z)
	{
		set_cube(floor_ceil, x, base_y			   , z);
		set_cube(floor_ceil, x, base_y + height - 1, z);
	}

	public void set_cube_wall(int wall, int height, int x, int base_y, int z)
	{
		for (int y = base_y + 1; y < base_y + height; y++)
		{
			set_cube(wall, x, y, z);
			set_cube(wall, x, y, z);
		}
	}

	public int get_cube(int w, int h, int l)
	{
		if (w < 0 || h < 0 || l < 0 || w >= width || h >= height || l >= length)
			return -1;

		return type_world[w][h][l];
	}

	private void add_side(int w, int h, int l, int s)
	{
		switch (s)
		{
		case Box.box_front:
			if (get_cube(w, h, l + 1) >= 0)
				return;
			break;
		case Box.box_back:
			if (get_cube(w, h, l - 1) >= 0)
				return;
			break;
		case Box.box_left:
			if (get_cube(w - 1, h, l) >= 0)
				return;
			break;
		case Box.box_right:
			if (get_cube(w + 1, h, l) >= 0)
				return;
			break;
		case Box.box_top:
			if (get_cube(w, h + 1, l) >= 0)
				return;
			break;
		case Box.box_bottom:
			if (get_cube(w, h - 1, l) >= 0)
				return;
			break;
		}

		float[] side = cubes.get(type_world[w][h][l])[s].clone();

		w *= scale;
		h *= scale;
		l *= scale;

		for (int i = 0; i < side.length; i += VectorMath.stride_size)
		{
			side[i] += w;
			side[i + 1] += h;
			side[i + 2] += l;
		}

		geometry_world.add(side, 4);
	}
	
	public void delete_map()
	{
		for (int ww = 0; ww < width; ww++)
			for (int hh = 0; hh < height; hh++)
				for (int ll  = 0; ll < length; ll++)
					type_world[ww][hh][ll] = -1;
		
		delete_buffers();
	}

	public void delete_buffers()
	{
		for (int w = 0; w < portal_map.map_width; w++)
			for (int h = 0; h < portal_map.map_height; h++)
				for (int l  = 0; l < portal_map.map_length; l++)
					portal_map.sector_map[w][h][l].delete_buffer();
	}

	public void update()
	{
		//loop through every sector of the map
		for (int w = 0; w < portal_map.map_width; w++)
		{
			for (int h = 0; h < portal_map.map_height; h++)
			{
				for (int l  = 0; l < portal_map.map_length; l++)
				{
					CubeSector s = portal_map.sector_map[w][h][l];

					//reset the geometry buffer
					geometry_world.clear();

					//loop through each cube of the sector
					for (int x = s.x1; x < s.x2; x++)
					{
						for (int y = s.y1; y < s.y2; y++)
						{
							for (int z = s.z1; z < s.z2; z++)
							{
								//if the cube is empty continue
								if (type_world[x][y][z] == -1)
									continue;

								if (y == 0)
									add_side(x, y, z, Box.box_top);
								else if (y == height - 1)
									add_side(x, y, z, Box.box_bottom);
								else if (x == 0)
									add_side(x, y, z, Box.box_right);
								else if (x == width - 1)
									add_side(x, y, z, Box.box_left);
								else if (z == 0)
									add_side(x, y, z, Box.box_front);
								else if (z == length - 1)
									add_side(x, y, z, Box.box_back);
								else
								{
									for (int side = 0; side < Box.box_sides; side++)
										add_side(x, y, z, side);
								}
							}
						}
					}

					//update the buffer with the new cube vertices and indices
					geometry_world.update();

					//send the geometry buffer to a new vertex and index buffer object
					s.set_buffer(geometry_world.generate_array_buffer());
				}
			}
		}
	}
}
