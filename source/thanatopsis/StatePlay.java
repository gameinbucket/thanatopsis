package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.MoveInput;
import com.gameinbucket.rendering.TapInput;
import com.gameinbucket.rendering.Thing;

public class StatePlay extends StateHandler
{
	private GameHandler g;

	public TapInput i_left_ac;
	public TapInput i_right_ac;
	public MoveInput i_rot;
	public MoveInput i_pos;

	public void release()
	{
		i_left_ac.release();
		i_right_ac.release();
		i_pos.release();
		i_rot.release();
	}

	StatePlay(RenderHandler r, GameHandler game)
	{
		super(r);

		g = game;
		g.input = this;

		int split_y = (int)(r.screen_buffer[0].height / 2.5f);

		i_left_ac = new TapInput(split_y, r.screen_buffer[0].height, 0, r.screen_buffer[0].width / 2);
		i_right_ac = new TapInput(split_y, r.screen_buffer[0].height, r.screen_buffer[0].width / 2, r.screen_buffer[0].width);
		i_pos = new MoveInput(0, split_y, 0, r.screen_buffer[0].width / 2);
		i_rot = new MoveInput(0, split_y, r.screen_buffer[0].width / 2, r.screen_buffer[0].width);
	}

	public void input_down(float x, float y, int i)
	{
		x -= r.screen_buffer[0].offset_w;
		y = r.screen_buffer[0].height - y + r.screen_buffer[0].offset_h;

		i_left_ac.press(x, y, i);
		i_right_ac.press(x, y, i);
		i_pos.press(x, y, i);
		i_rot.press(x, y, i);
	}

	public void input_move(float x, float y, int i)
	{
		x -= r.screen_buffer[0].offset_w;
		y = r.screen_buffer[0].height - y + r.screen_buffer[0].offset_h;

		i_pos.move(x, y, i);
		i_rot.move(x, y, i);
	}

	public void input_up(int i)
	{
		i_left_ac.release(i);
		i_right_ac.release(i);
		i_pos.release(i);
		i_rot.release(i);
	}

	public void input_back(Main m)
	{
		if (g.protagonist.escaped != null)
			g.protagonist.escaped.alpha = 0.99f;
		else
		{
			release();
			r.state_function = r.state_main_menu;
		}
	}

	public void update()
	{
		for (int i = 0; i < g.updatable.size(); i++)
		{
			Thing s = g.updatable.get(i);

			s.update();

			if (s.remove)
			{
				g.collidable.remove(s);
				g.updatable.remove(s);

				if (s instanceof FireBall)
					g.fire_balls.remove(s);

				i--;
			}
		}

		if (g.dmg_engine.active)
			g.dmg_engine.update();

		g.set_sprite_rotation();
		g.set_current_light();
	}

	public void draw()
	{
		//rendering pre process
		r.enable_3d();
		r.draw_3d(0, r.s_pre_light);
		r.disable_3d();

		//rendering post processing
		r.draw_screen(0, r.s_post_vignette, r.draw_buffer[0].texture());

		//draw 2d overlay
		r.draw_2d();
	}
}
