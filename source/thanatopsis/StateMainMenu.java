package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Input;

public class StateMainMenu extends StateHandler
{
	private GameHandler g;
	public Input[] inputs;

	private boolean begin_new_game = false;
	public float ending_alpha = 0.99f;
	public boolean level_beginning = true;

	StateMainMenu(RenderHandler r, GameHandler game)
	{
		super(r);

		g = game;
		inputs = new Input[3];

		//start
		inputs[0] = new Input(r.screen_buffer[0].width / 2 - (int)(96 * r.view_scale), (int)(r.screen_buffer[0].height / 2 - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.0f, 0.0f, 0.5f, 0.25f);

		//options
		inputs[1] = new Input(r.screen_buffer[0].width / 2 + (int)(32 * r.view_scale), (int)(r.screen_buffer[0].height / 2 - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.5f, 0.0f, 1.0f, 0.25f);

		//title
		inputs[2] = new Input(r.screen_buffer[0].width / 2 - (int)(64 * r.view_scale), (int)(r.screen_buffer[0].height / 1.5f - 24 * r.view_scale), (int)(128 * r.view_scale), (int)(16 * r.view_scale), 0.0f, 0.25f, 1.0f, 0.5f);
	}

	public void release()
	{
		for (int i = 0; i < inputs.length; i++)
			inputs[i].release();
	}

	public void input_down(float x, float y, int i)
	{
		x -= r.screen_buffer[0].offset_w;
		y = r.screen_buffer[0].height - y + r.screen_buffer[0].offset_h;

		for (int id = 0; id < inputs.length; id++)
			inputs[id].press(x, y, i);

		if (inputs[0].is_active())
		{
			g.audio.play(R.raw.a_step);

			level_beginning = false;

			if (g.protagonist.dead)
				begin_new_game = true;
			else
			{
				release();
				r.state_function = r.state_play;
			}
		}
		else if (inputs[1].is_active())
		{
			g.audio.play(R.raw.a_step);

			release();
			r.state_function = r.state_options;
		}
	}

	public void input_move(float x, float y, int i)
	{

	}

	public void input_up(int i)
	{
		for (int id = 0; id < inputs.length; id++)
			inputs[id].release(i);
	}

	public void input_back(Main m)
	{
		m.moveTaskToBack(true);
	}

	public void update()
	{
		if (ending_alpha == 1.0f)
		{
			ending_alpha = 0.99f;
			g.new_game();
		}
		else if (ending_alpha > 0.0f)
		{
			ending_alpha -= 0.02f;

			if (ending_alpha < 0.0f)
				ending_alpha = 0.0f;
		}
		else if (begin_new_game)
		{
			begin_new_game = false;

			g.new_game();

			release();
			r.state_function = r.state_play;

			return;
		}

		if (level_beginning && g.light_current != null)
			g.light_current.update();
	}

	public void draw()
	{
		//3d processing
		r.enable_3d();
		r.draw_3d(0, r.s_pre_light);
		r.disable_3d();

		//rendering post processing
		r.draw_screen(0, r.s_post_vignette, r.draw_buffer[0].texture());

		//main menu overlay
		r.draw_2d_welcome();
	}
}
