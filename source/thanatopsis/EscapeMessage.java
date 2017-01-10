package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.VectorMath;

public class EscapeMessage 
{
	private String full_message = "as you step out of the dark catacombs into the night, you feel a sense of relief that the desolate corridors are behind you. You wonder if anything has happened while you were lost...";
	private int substring_index;
	private int index_increment;

	private RenderHandler r;
	public float alpha;

	EscapeMessage(RenderHandler render)
	{
		r = render;

		int chars_per_line = (int)(r.screen_buffer[0].width / (VectorMath.font_size_x * r.view_scale));
		int current_line = chars_per_line;

		StringBuilder sb = new StringBuilder(full_message);

		int n_offset = 1;
		while (current_line < full_message.length())
		{
			sb.insert(current_line, "/");

			current_line += chars_per_line + n_offset;
			n_offset++;
		}

		full_message = sb.toString();

		substring_index = 1;
		index_increment = 0;
		alpha = 1.0f;
	}

	void update()
	{
		if (alpha < 1.0f && alpha > 0.0f)
		{
			alpha -= 0.02f;

			if (alpha <= 0.0f)
			{
				alpha = 0.0f;
				
				r.state_main_menu.level_beginning = true; 
				r.state_main_menu.ending_alpha = 1.0f;
				r.state_function.release();
				r.state_function = r.state_main_menu;
			}
		}

		if (substring_index >= full_message.length())
			return;

		index_increment++;

		if (index_increment == 5)
		{
			substring_index++;
			index_increment = 0;
		}
	}

	String message()
	{
		return full_message.substring(0, substring_index);
	}
}
