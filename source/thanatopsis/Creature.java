package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Sprite;
import com.gameinbucket.rendering.Thing;

public abstract class Creature extends Sprite
{
	//general actions
	protected static final short ACTION_STANDING = 0;
	protected static final short ACTION_RUNNING = 1;
	protected static final short ACTION_DEAD = 2;
	protected static final short ACTION_SWITCHING = 3;
	protected static final short ACTION_ATTACK0 = 4;
	protected static final short ACTION_ATTACK1 = 5;

	protected GameHandler game;

	protected int health;
	public boolean dead;

	Creature(float x, float y, float z, float rad, int f_limit, int col, int[] anim, int rx, int ry, int rz, float speed, GameHandler g, int hp)
	{
		super(x, y, z, rad, f_limit, col, anim, rx, ry, rz, speed);

		game = g;
		health = hp;
		dead = false;
	}

	public abstract void take_damage(Thing t, int d);

	protected void cube_collision_x()
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

			view_v[0] = 0;
		}
	}

	protected void cube_collision_z()
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

			view_v[2] = 0;
		}
	}
}
