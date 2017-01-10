package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.Input;

public class StateOptions extends StateHandler
{
	private GameHandler g;
	public Input[] inputs;

	StateOptions(RenderHandler r, GameHandler game)
	{
		super(r);

		g = game;
		inputs = new Input[6];

		//view texture increments
		inputs[0] = new Input(r.screen_buffer[0].width / 2 + (int)(42 * r.view_scale), (int)(r.screen_buffer[0].height / 1.5f - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.0f, 0.5f, 0.5f, 0.75f);
		inputs[1] = new Input(r.screen_buffer[0].width / 2 - (int)(106 * r.view_scale), (int)(r.screen_buffer[0].height / 1.5f - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.5f, 0.5f, 1.0f, 0.75f);

		//rotation sensitivity increments
		inputs[2] = new Input(r.screen_buffer[0].width / 2 + (int)(42 * r.view_scale), (int)(r.screen_buffer[0].height / 2.5f - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.0f, 0.5f, 0.5f, 0.75f);
		inputs[3] = new Input(r.screen_buffer[0].width / 2 - (int)(106 * r.view_scale), (int)(r.screen_buffer[0].height / 2.5f - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.5f, 0.5f, 1.0f, 0.75f);

		inputs[4] = new Input(r.screen_buffer[0].width / 2 - (int)(32 * r.view_scale), (int)(r.screen_buffer[0].height / 1.5f - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.0f, 0.75f, 0.5f, 1.0f);
		inputs[5] = new Input(r.screen_buffer[0].width / 2 - (int)(32 * r.view_scale), (int)(r.screen_buffer[0].height / 2.5f - 24 * r.view_scale), (int)(64 * r.view_scale), (int)(16 * r.view_scale), 0.5f, 0.75f, 1.0f, 1.0f);
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

		if (inputs[2].is_active() && g.protagonist.rotation_sensitivity > -0.08f)
		{
			g.audio.play(R.raw.a_step);
			g.protagonist.rotation_sensitivity -= 0.005f;
		}
		else if (inputs[3].is_active() && g.protagonist.rotation_sensitivity < -0.02f)
		{
			g.audio.play(R.raw.a_step);
			g.protagonist.rotation_sensitivity += 0.005f;
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
		release();
		r.state_function = r.state_main_menu;
	}

	public void update()
	{
		if (inputs[0].is_active())
		{
			if (r.vt_scale > 1)
			{
				g.audio.play(R.raw.a_step);
				
				r.vt_scale /= 2;
				r.onSurfaceChanged(null, r.screen_buffer[0].width, r.screen_buffer[0].height);
			}

			inputs[0].release();
		}
		else if (inputs[1].is_active())
		{
			g.audio.play(R.raw.a_step);
			
			r.vt_scale *= 2;
			r.onSurfaceChanged(null, r.screen_buffer[0].width, r.screen_buffer[0].height);

			inputs[1].release();
		}

		if (r.state_main_menu.level_beginning && g.light_current != null)
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
		r.draw_2d_options();
	}
}
