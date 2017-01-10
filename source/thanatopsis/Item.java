package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Thing;
import com.gameinbucket.rendering.VectorMath;

public class Item extends Thing
{
	public final static short PISTOL = 62;
	public final static short PISTOL_AMMO = 61;
	public final static short SKELETON_KEY = 60;
	public final static short GREEN_KEY = 59;
	public final static short RED_KEY = 58;

	GameHandler game;
	public short item;

	Item(float x, float y, float z, GameHandler g, short id)
	{
		super(x, y, z, 0.5f);

		game = g;
		item = id;

		int tx = item % 8;
		int ty = item / 8;

		tex0 = tx;
		tex2 = tx + 1;

		tex1 = ty;
		tex3 = ty + 1;
	}

	public void update()
	{

	}

	public float[] image()
	{
		return VectorMath.create_sprite(view_p[0], view_p[1], view_p[2], game.rot_to_view, 0.7f, 1.4f, VectorMath.texture_8, tex0, tex1, tex2, tex3);
	}
}
