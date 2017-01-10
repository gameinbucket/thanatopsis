package com.gameinbucket.thanatopsis;

public class Portal extends Scenery
{
	boolean next;
	int image_increment = 0;
	
	Portal(float x, float y, float z, GameHandler g, int x1, int y1, int x2, int y2, boolean n)
	{
		super(x, y, z, g, x1, y1, x2, y2);
		next = n;
	}
	
	public void update()
	{
		if (next)
		{
			if (touching_thing(game.protagonist))
			{
				game.audio.play(R.raw.a_long_death);
				game.next_level();
			}
			
			image_increment++;
			
			if (image_increment == 2)
			{
				tex0++;
				tex2++;
				
				if (tex2 == 9)
				{
					tex0 = 0;
					tex2 = 1;
				}
				
				image_increment = 0;
			}
		}
		else
		{
			if (game.protagonist.escaped != null)
				return;
			
			if (touching_thing(game.protagonist))
				game.protagonist.escaped = new EscapeMessage(game.input.r);
		}
	}
}
