package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Thing;
import com.gameinbucket.rendering.VectorMath;

public class Scenery extends Thing
{
	GameHandler game;
	
	Scenery(float x, float y, float z, GameHandler g, int x1, int y1, int x2, int y2)
	{
		super(x, y, z, 0.7f);
		
		game = g;
		
		tex0 = x1;
		tex1 = y1;
		tex2 = x2;
		tex3 = y2;
	}
	
	public void update()
	{
		
	}
	
	public float[] image()
	{
		return VectorMath.create_sprite(view_p[0], view_p[1], view_p[2], game.rot_to_view, 0.7f, 1.4f, VectorMath.texture_8, tex0, tex1, tex2, tex3);
	}
}
