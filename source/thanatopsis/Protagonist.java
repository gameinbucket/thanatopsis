package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.MatrixMath;
import com.gameinbucket.rendering.Thing;
import com.gameinbucket.rendering.VectorMath;

public class Protagonist extends Creature
{
	public static final int[] a_hand = {0, 1, 1};
	public static final int[] a_pistol = {4, 6, 6};

	//private float velocity_r0 = 0;
	private float velocity_r1 = 0;
	private final float rotation_friction;
	public float rotation_sensitivity;

	private int viewmodel_cos_y = 0;
	private float viewmodel_offset_y = 0;
	private float bob_cos_y = 0;

	public boolean has_pistol = false;
	public int[] next_item;
	public int pistol_ammo;
	private boolean[] keys = {false, false, false};

	private float height = 1.0f;

	public float overlay_r = 0.0f;
	public float overlay_g = 0.0f;
	public float overlay_b = 0.0f;
	public float overlay_a = 0.0f;

	public EscapeMessage escaped = null;
	private int health_moment;

	Protagonist(float x, float y, float z, int rx, int ry, int rz, GameHandler g)
	{
		super(x, y, z, 0.31f, 6, 4, a_hand, rx, ry, rz, 0.000912f, g, 2);

		rotation_friction = 0.76f;
		rotation_sensitivity = -0.04f;

		has_pistol = false;
		pistol_ammo = 0;

		health_moment = 0;

		next_item = a_hand;
		present_action = ACTION_SWITCHING;

		viewmodel_cos_y = 96;
		viewmodel_offset_y = -90 + (float)Math.cos(VectorMath.deg_to_rad(viewmodel_cos_y)) * 90;
	}

	public void full_reset()
	{
		reset();

		height = 1.0f;
		dead = false;
		escaped = null;

		has_pistol = false;
		pistol_ammo = 0;

		overlay_r = 0.0f;
		overlay_g = 0.0f;
		overlay_b = 0.0f;
		overlay_a = 0.0f;

		next_item = a_hand;
		present_action = ACTION_SWITCHING;

		viewmodel_cos_y = 96;
		viewmodel_offset_y = -90 + (float)Math.cos(VectorMath.deg_to_rad(viewmodel_cos_y)) * 90;
	}

	public void reset()
	{
		view_v[0] = 0;
		view_v[1] = 0;
		view_v[2] = 0;

		//velocity_r0 = 0;
		velocity_r1 = 0;

		//view_r[0] = 0;
		view_r[1] = 90;

		health = 2;

		for (int i = 0; i < keys.length; i++)
			keys[i] = false;
	}

	public void set_matrix(float[] m)
	{
		//MatrixMath.rotate_x(m, VectorMath.deg_to_rad(view_r[0]));
		MatrixMath.rotate_y(m, VectorMath.deg_to_rad(view_r[1]));
		//MatrixMath.rotate_z(m, VectorMath.deg_to_rad(view_r[2]));
		MatrixMath.translate(m, -view_p[0], -(view_p[1] + height - 0.05f + (float)Math.cos(VectorMath.deg_to_rad(bob_cos_y * 40)) * 0.05f), -view_p[2]);
	}

	public void take_damage(Thing t, int d)
	{
		if (dead || escaped != null || health_moment > 0)
			return;

		health -= d;
		health_moment = 44;

		float angle_to_damager = angle_to_thing(t);

		view_v[0] -= Math.sin(angle_to_damager) * 0.44f;
		view_v[2] += Math.cos(angle_to_damager) * 0.44f;

		overlay_r = 0.8f;
		overlay_a = 0.7f;

		if (health <= 0)
		{
			game.save_death();

			dead = true;
			game.audio.play(R.raw.a_long_death);
		}
		else
			game.audio.play(R.raw.a_hand_punch);
	}

	protected void update_input()
	{
		if (escaped != null)
		{
			if (overlay_a < 1.0f)
			{
				overlay_a += 0.03f;

				if (overlay_a > 1.0f)
					overlay_a = 1.0f;
			}
			else
			{
				escaped.update();
				return;
			}
		}
		else if (dead)
		{
			if (height > 0.5f)
				height -= 0.2f;

			return;
		}
		else
		{
			if (health_moment > 0)
				health_moment--;

			if (overlay_r == 0.8f && overlay_a > 0.0f)
			{
				overlay_a -= 0.03f;

				if (overlay_a <= 0.0f)
				{
					overlay_r = 0.0f;
					overlay_a = 0.0f;
				}
			}
		}

		if (game.input.i_right_ac.is_active() && present_action == ACTION_STANDING)
		{
			viewmodel_cos_y = 0;
			viewmodel_offset_y = 0;

			if (animation == a_hand)
				present_action = ACTION_ATTACK0;
			else if (animation == a_pistol)
			{
				game.audio.play(R.raw.a_pistol_shoot);
				pistol_ammo--;

				present_action = ACTION_ATTACK1;
			}

			game.input.i_right_ac.release();
		}

		if (game.input.i_left_ac.is_active())
		{
			Box b = ray_cast_box(4);

			if (b != null)
			{
				if (b instanceof Door)
				{
					boolean open = false;

					switch(((Door)b).key)
					{
					case Item.SKELETON_KEY:
						open = keys[0];
						break;
					case Item.GREEN_KEY:
						open = keys[1];
						break;
					case Item.RED_KEY:
						open = keys[2];
						break;
					}

					if (open)
						b.activate();
					else
						game.audio.play(R.raw.a_step);
				}
				else
					b.activate();
			}

			game.input.i_left_ac.release();
		}

		if (game.input.i_rot.is_active())
		{
			//velocity_r0 -= Math.sin(game.input.i_rot.angle) * game.input.i_rot.distance * rotation_sensitivity / 2.0f;
			velocity_r1 += Math.cos(game.input.i_rot.angle) * game.input.i_rot.distance * rotation_sensitivity;
		}

		if (game.input.i_pos.is_active())
		{
			float rotation_final = game.input.i_pos.angle - VectorMath.deg_to_rad(view_r[1]);

			view_v[0] -= Math.cos(rotation_final) * game.input.i_pos.distance * pace;
			view_v[2] += Math.sin(rotation_final) * game.input.i_pos.distance * pace;
		}
	}

	protected void update_position()
	{
		//update velocities
		//velocity_r0 *= rotation_friction;
		velocity_r1 *= rotation_friction;

		view_v[0] *= game.friction;
		view_v[2] *= game.friction;

		if (Math.abs(view_v[0]) < 0.001f) view_v[0] = 0;
		if (Math.abs(view_v[2]) < 0.001f) view_v[2] = 0;

		bob_cos_y += Math.abs(view_v[0]) + Math.abs(view_v[2]);

		if (bob_cos_y >= 360)
			bob_cos_y -= 360;

		//view_r[0] += velocity_r0;
		view_r[1] += velocity_r1;

		//object collision
		for (int i = 0; i < game.collidable.size(); i++)
		{
			Thing s = game.collidable.get(i);

			if (s == this)
				continue;

			collision_with_thing_x(s, view_p[0] + view_v[0]);
			collision_with_thing_z(s, view_p[2] + view_v[2]);
		}

		//box collision
		for (int i = 0; i < game.boxes.size(); i++)
		{
			Box b = game.boxes.get(i);

			collision_with_box_x(b, view_p[0] + view_v[0]);
			collision_with_box_z(b, view_p[2] + view_v[2]);
		}

		//world collision
		{
			cube_collision_x();
			view_p[0] += view_v[0];

			cube_collision_z();
			view_p[2] += view_v[2];
		}

		//item collision
		for (int i = 0; i < game.items.size(); i++)
		{
			Item t = game.items.get(i);

			if (touching_thing(t))
			{
				pickup(t.item);
				game.items.remove(t);
				i--;
			}
		}
	}

	private void pickup(short i)
	{
		switch (i)
		{
		case Item.PISTOL:
			game.audio.play(R.raw.a_hand_miss);

			if (!has_pistol)
			{
				viewmodel_cos_y = 0;
				next_item = a_pistol;
				present_action = ACTION_SWITCHING;

				has_pistol = true;
			}

			pistol_ammo += 8;
			break;
		case Item.PISTOL_AMMO:
			game.audio.play(R.raw.a_hand_miss);

			if (pistol_ammo <= 0 && has_pistol)
			{
				viewmodel_cos_y = 0;
				next_item = a_pistol;
				present_action = ACTION_SWITCHING;
			}

			pistol_ammo += 6;
			break;
		case Item.SKELETON_KEY:
			game.audio.play(R.raw.a_hand_miss);
			keys[0] = true;
			break;
		case Item.GREEN_KEY:
			game.audio.play(R.raw.a_hand_miss);
			keys[1] = true;
			break;
		case Item.RED_KEY:
			game.audio.play(R.raw.a_hand_miss);
			keys[2] = true;
			break;
		}
	}

	private Box ray_cast_box(int step_limit)
	{
		final float r = 0.5f;
		float xa = 0.5f *  (float)Math.sin(VectorMath.deg_to_rad(view_r[1]));
		float za = 0.5f *  -(float)Math.cos(VectorMath.deg_to_rad(view_r[1]));
		float x = view_p[0];
		float z = view_p[2];

		for (int p = 0; p < step_limit; p++)
		{
			x += xa;
			z += za;

			for (int i = 0; i < game.boxes.size(); i++)
			{
				Box s = game.boxes.get(i);

				if (x - r <= s.view_p[0] + s.width && x + r >= s.view_p[0] &&
						z - r + 0.01f <= s.view_p[2] + s.length && z + r - 0.01f >= s.view_p[2])
					return s;
			}
		}

		return null;
	}

	private Creature ray_cast_creature(int step_limit)
	{
		final float r = 0.5f;
		float xa = 0.5f *  (float)Math.sin(VectorMath.deg_to_rad(view_r[1]));
		float za = 0.5f *  -(float)Math.cos(VectorMath.deg_to_rad(view_r[1]));
		float x = view_p[0];
		float z = view_p[2];

		for (int p = 0; p < step_limit; p++)
		{
			x += xa;
			z += za;

			for (int i = 0; i < game.fiends.size(); i++)
			{
				Creature s = game.fiends.get(i);

				if (s.dead)
					continue;

				if (x - r <= s.view_p[0] + s.radius && x + r >= s.view_p[0] - s.radius &&
						z - r + 0.01f <= s.view_p[2] + s.radius && z + r - 0.01f >= s.view_p[2] - s.radius)
					return s;
			}
		}

		return null;
	}

	protected void update_animation()
	{
		if (dead)
			return;

		if (present_action == ACTION_ATTACK0)
		{
			next_frame();

			if (frame == 1 && frame_increment == 0)
			{
				Creature s = ray_cast_creature(4);

				if (s != null)
				{
					s.take_damage(this, 1);
					game.audio.play(R.raw.a_hand_punch);
				}
				else
					game.audio.play(R.raw.a_hand_miss);
			}
			else if (is_animation_done())
			{
				viewmodel_offset_y = 0;

				present_action = ACTION_STANDING;
				set_animation(a_hand, 0);
			}
		}
		else if (present_action == ACTION_ATTACK1)
		{
			next_frame();

			if (frame == 1 && frame_increment == 0)
			{
				Creature s = ray_cast_creature(50);

				if (s != null)
					s.take_damage(this, 1);
			}
			else if (frame == 2 && frame_increment == 0)
				viewmodel_offset_y = -2.8f;
			else if (is_animation_done())
			{
				viewmodel_offset_y = 0;

				if (pistol_ammo <= 0)
				{
					present_action = ACTION_SWITCHING;
					next_item = a_hand;
				}
				else
				{
					present_action = ACTION_STANDING;
					set_animation(a_pistol, 0);
				}
			}
		}
		else if (present_action == ACTION_SWITCHING)
		{
			viewmodel_cos_y += 12;
			viewmodel_offset_y = -90 + (float)Math.cos(VectorMath.deg_to_rad(viewmodel_cos_y)) * 90;

			if (viewmodel_cos_y == 180)
				set_animation(next_item, 0);
			else if (viewmodel_cos_y == 360)
				present_action = ACTION_STANDING;
		}
		else if (Math.abs(view_v[0]) >= 0.01f || Math.abs(view_v[2]) >= 0.01f)
		{
			viewmodel_cos_y += 9;
			viewmodel_offset_y = -2.8f + (float)Math.cos(VectorMath.deg_to_rad(viewmodel_cos_y)) * 2.8f;
		}
	}

	public boolean draw()
	{
		if (dead)
			return false;

		return true;
	}

	public float[] image()
	{
		return VectorMath.create_rectangle(game.view.render.screen_buffer[0].width / 2 - (43 - velocity_r1) * game.view.render.view_scale, viewmodel_offset_y * game.view.render.view_scale, 86 * game.view.render.view_scale, 86 * game.view.render.view_scale, VectorMath.texture_4, tex0, tex1, tex2, tex3);
	}
}
