package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.VectorMath;

public class Door extends Box
{
	private float next_view_p[];
	private float[][] next_box;
	private float next_width;
	private float next_length;

	private GameHandler game;
	public short key;

	Door(float x, float y, float z, float w, float h, float l, float s, float f1x, float f1y, float f2x, float f2y, float b1x, float b1y, float b2x, float b2y, float l1x, float l1y, float l2x, float l2y, float r1x, float r1y, float r2x, float r2y, float u1x, float u1y, float u2x, float u2y, float d1x, float d1y, float d2x, float d2y, GameHandler g, short direction, short k)
	{
		super(x, y, z, w, h, l, s, f1x, f1y, f2x, f2y, b1x, b1y, b2x, b2y, l1x, l1y, l2x, l2y, r1x, r1y, r2x, r2y, u1x, u1y, u2x, u2y, d1x, d1y, d2x, d2y);

		game = g;
		key = k;
		
		next_view_p = new float[3];

		next_width = l;
		next_length = w;
		
		switch (direction)
		{
		case Box.box_front:
			next_view_p[0] = x;
			next_view_p[1] = y;
			next_view_p[2] = z;
			
			next_box = VectorMath.create_box(l, h, w, s, l1x, l1y, l2x, l2y, r1x, r1y, r2x, r2y, f1x, f1y, f2x, f2y, b1x, b1y, b2x, b2y, u1x, u1y, u2x, u2y, d1x, d1y, d2x, d2y);

			for (int side = 0; side < box_sides; side++)
			{
				for (int i = 0; i < next_box[0].length; i += VectorMath.stride_size)
				{
					next_box[side][i] += next_view_p[0];
					next_box[side][i + 1] += next_view_p[1];
					next_box[side][i + 2] += next_view_p[2];
				}
			}
			break;
		case Box.box_bottom:
			next_view_p[0] = x;
			next_view_p[1] = y;
			next_view_p[2] = z - w + l;
			
			next_box = VectorMath.create_box(l, h, w, s, l2x, l1y, l1x, l2y, r1x, r2y, r2x, r1y, b1x, b1y, b2x, b2y, f1x, f1y, f2x, f2y, u1x, u1y, u2x, u2y, d1x, d1y, d2x, d2y);

			for (int side = 0; side < box_sides; side++)
			{
				for (int i = 0; i < next_box[0].length; i += VectorMath.stride_size)
				{
					next_box[side][i] += next_view_p[0];
					next_box[side][i + 1] += next_view_p[1];
					next_box[side][i + 2] += next_view_p[2];
				}
			}
			break;
		case Box.box_right:
			next_view_p[0] = x + w - l;
			next_view_p[1] = y;
			next_view_p[2] = z;
			
			next_box = VectorMath.create_box(l, h, w, s, r1x, r1y, r2x, r2y, l1x, l1y, l2x, l2y, f1x, f1y, f2x, f2y, b1x, b1y, b2x, b2y, u1x, u1y, u2x, u2y, d1x, d1y, d2x, d2y);

			for (int side = 0; side < box_sides; side++)
			{
				for (int i = 0; i < next_box[0].length; i += VectorMath.stride_size)
				{
					next_box[side][i] += next_view_p[0];
					next_box[side][i + 1] += next_view_p[1];
					next_box[side][i + 2] += next_view_p[2];
				}
			}
			break;
		case Box.box_left:
			next_view_p[0] = x;
			next_view_p[1] = y;
			next_view_p[2] = z;
			
			next_box = VectorMath.create_box(l, h, w, s, l1x, l1y, l2x, l2y, r1x, r1y, r2x, r2y, f2x, f1y, f1x, f2y, b2x, b1y, b1x, b2y, u1x, u1y, u2x, u2y, d1x, d1y, d2x, d2y);

			for (int side = 0; side < box_sides; side++)
			{
				for (int i = 0; i < next_box[0].length; i += VectorMath.stride_size)
				{
					next_box[side][i] += next_view_p[0];
					next_box[side][i + 1] += next_view_p[1];
					next_box[side][i + 2] += next_view_p[2];
				}
			}
			break;
		}
	}

	public void activate()
	{
		float[] temp_pos = view_p;
		view_p = next_view_p;
		next_view_p = temp_pos;

		float[][] temp_box = box;
		box = next_box;
		next_box = temp_box;

		float temp_width = width;
		width = next_width;
		next_width = temp_width;
		
		float temp_length = length;
		length = next_length;
		next_length = temp_length;

		game.audio.play(R.raw.a_door_open);
	}
}
