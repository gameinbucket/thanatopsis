package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.Sprite;
import com.gameinbucket.rendering.Thing;
import com.gameinbucket.rendering.VectorMath;

public class FireBall extends Sprite
{
	private static final int[] a_move = {8, 9};
	private static final int[] a_destroy = {10, 11};

	GameHandler game;
	Thing parent;

	FireBall(float x, float y, float z, GameHandler g, float angle, Thing p)
	{
		super(x, y, z, 0.5f, 3, 8, a_move, 0, 0, 0, 0.4f);

		game = g;
		present_action = Creature.ACTION_RUNNING;
		parent = p;

		view_v[0] += Math.sin(angle) * pace;
		view_v[2] -= Math.cos(angle) * pace;
	}

	public void update_input()
	{

	}

	public void update_position()
	{
		if (animation == a_destroy)
			return;
		
		view_p[0] += view_v[0];
		view_p[2] += view_v[2];

		//object collision
		for (int i = 0; i < game.collidable.size(); i++)
		{
			Thing s = game.collidable.get(i);

			if (s == parent)
				continue;

			boolean hit = false;

			if (collision_with_thing_x(s, view_p[0] + view_v[0]))
				hit = true;

			if (collision_with_thing_z(s, view_p[2] + view_v[2]))
				hit = true;

			if (hit)
			{
				if (s instanceof Creature)
				{
					Creature p = (Creature)s;
					p.take_damage(this, 1);
				}

				set_animation(a_destroy, 0);
				game.audio.play(R.raw.a_fire_ball, 6.0f / (float)Math.sqrt(distance_to_thing(game.view_current)), angle_to_thing(game.view_current) - VectorMath.deg_to_rad(game.view_current.view_r[1]) + VectorMath.rad_180);
				return;
			}
		}

		//box collision
		for (int i = 0; i < game.boxes.size(); i++)
		{
			Box b = game.boxes.get(i);

			collision_with_box_x(b, view_p[0] + view_v[0]);
			collision_with_box_z(b, view_p[2] + view_v[2]);

			if (view_v[0] == 0 || view_v[2] == 0)
			{
				set_animation(a_destroy, 0);
				game.audio.play(R.raw.a_fire_ball, 6.0f / (float)Math.sqrt(distance_to_thing(game.view_current)), angle_to_thing(game.view_current) - VectorMath.deg_to_rad(game.view_current.view_r[1]) + VectorMath.rad_180);
				return;
			}
		}

		//world collision
		if (cube_collision_x() || cube_collision_z())
		{
			set_animation(a_destroy, 0);
			game.audio.play(R.raw.a_fire_ball, 6.0f / (float)Math.sqrt(distance_to_thing(game.view_current)), angle_to_thing(game.view_current) - VectorMath.deg_to_rad(game.view_current.view_r[1]) + VectorMath.rad_180);
		}
	}

	public void update_animation()
	{
		next_frame();

		if (animation == a_destroy && is_animation_done())
			remove = true;
	}

	protected boolean cube_collision_x()
	{
		int x1;

		if (view_v[0] > 0)
			x1 = (int)((view_p[0] + view_v[0] + radius) / game.world.scale);
		else
			x1 = (int)((view_p[0] + view_v[0] - radius) / game.world.scale);

		int z1 = (int)(view_p[2] / game.world.scale);
		int z2 = (int)((view_p[2] - radius + 0.01f) / game.world.scale);
		int z3 = (int)((view_p[2] + radius - 0.01f) / game.world.scale);

		if (game.world.get_cube(x1, 1, z1) >= 0 || game.world.get_cube(x1, 1, z2) >= 0 || game.world.get_cube(x1, 1, z3) >= 0)
		{
			if (view_v[0] > 0)
				view_p[0] += x1 * game.world.scale - view_p[0] - radius;
			else
				view_p[0] += x1 * game.world.scale - view_p[0] + game.world.scale + radius;

			return true;
		}

		return false;
	}

	protected boolean cube_collision_z()
	{
		int x1 = (int)(view_p[0] / game.world.scale);
		int x2 = (int)((view_p[0] - radius + 0.01f) / game.world.scale);
		int x3 = (int)((view_p[0] + radius - 0.01f) / game.world.scale);

		int z1;

		if (view_v[2] > 0)
			z1 = (int)((view_p[2] + view_v[2] + radius) / game.world.scale);
		else
			z1 = (int)((view_p[2] + view_v[2] - radius) / game.world.scale);

		if (game.world.get_cube(x1, 1, z1) >= 0 || game.world.get_cube(x2, 1, z1) >= 0 || game.world.get_cube(x3, 1, z1) >= 0)
		{
			if (view_v[2] > 0)
				view_p[2] += z1 * game.world.scale - view_p[2] - radius;
			else
				view_p[2] += z1 * game.world.scale - view_p[2] + radius + game.world.scale;

			return true;
		}

		return false;
	}

	public float[] image()
	{
		return VectorMath.create_sprite(view_p[0], view_p[1], view_p[2], game.rot_to_view, 0.5f, 1.0f, VectorMath.texture_8, tex0, tex1, tex2, tex3);
	}
}
