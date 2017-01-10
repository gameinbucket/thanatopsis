package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.VectorMath;

public class Torch
{
	protected float[][] box;

	Torch(float x, float y, float z, float w, float h, float l, float s, float f1x, float f1y, float f2x, float f2y, float b1x, float b1y, float b2x, float b2y, float l1x, float l1y, float l2x, float l2y, float r1x, float r1y, float r2x, float r2y, float u1x, float u1y, float u2x, float u2y, float d1x, float d1y, float d2x, float d2y, short direction)
	{
		box = VectorMath.create_box(w, h, l, s, f1x, f1y, f2x, f2y, b1x, b1y, b2x, b2y, l1x, l1y, l2x, l2y, r1x, r1y, r2x, r2y, u1x, u1y, u2x, u2y, d1x, d1y, d2x, d2y);

		switch (direction)
		{
		case Box.box_front:
			for (int side = 0; side < Box.box_sides; side++)
			{
				VectorMath.triangle_rotate_x(box[side], VectorMath.deg_to_rad(45));
				VectorMath.triangle_rotate_y(box[side], VectorMath.deg_to_rad(90));

				for (int i = 0; i < box[0].length; i += VectorMath.stride_size)
				{
					box[side][i] += x - 0.35f;
					box[side][i + 1] += y;
					box[side][i + 2] += z + l / 2;
				}
			}
			break;
		case Box.box_bottom:
			for (int side = 0; side < Box.box_sides; side++)
			{
				VectorMath.triangle_rotate_x(box[side], VectorMath.deg_to_rad(-45));
				VectorMath.triangle_rotate_y(box[side], VectorMath.deg_to_rad(90));

				for (int i = 0; i < box[0].length; i += VectorMath.stride_size)
				{
					box[side][i] += x + 0.26f;
					box[side][i + 1] += y;
					box[side][i + 2] += z + l / 2;
				}
			}
			break;
		case Box.box_right:
			for (int side = 0; side < Box.box_sides; side++)
			{
				VectorMath.triangle_rotate_z(box[side], VectorMath.deg_to_rad(45));
				VectorMath.triangle_rotate_y(box[side], VectorMath.deg_to_rad(90));

				for (int i = 0; i < box[0].length; i += VectorMath.stride_size)
				{
					box[side][i] += x - w / 2;
					box[side][i + 1] += y;
					box[side][i + 2] += z - 0.26f;
				}
			}
			break;
		case Box.box_left:
			for (int side = 0; side < Box.box_sides; side++)
			{
				VectorMath.triangle_rotate_z(box[side], VectorMath.deg_to_rad(-45));
				VectorMath.triangle_rotate_y(box[side], VectorMath.deg_to_rad(90));

				for (int i = 0; i < box[0].length; i += VectorMath.stride_size)
				{
					box[side][i] += x - w / 2;
					box[side][i + 1] += y;
					box[side][i + 2] += z + 0.35f;
				}
			}
			break;
		case Short.MAX_VALUE:
			for (int side = 0; side < Box.box_sides; side++)
			{
				VectorMath.triangle_rotate_y(box[side], VectorMath.deg_to_rad(5));
				
				for (int i = 0; i < box[0].length; i += VectorMath.stride_size)
				{
					box[side][i] += x;
					box[side][i + 1] += y;
					box[side][i + 2] += z + 0.05f;
				}
			}
			break;
		}
	}

	public float[] image(int s)
	{
		return box[s];
	}
}
