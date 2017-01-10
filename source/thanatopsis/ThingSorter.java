package com.gameinbucket.thanatopsis;

import java.util.Comparator;
import com.gameinbucket.rendering.Thing;

public class ThingSorter implements Comparator<Thing>
{
	GameHandler game;
	
	ThingSorter(GameHandler g)
	{
		game = g;
	}
	
	public int compare(Thing a, Thing b)
	{
		return (int)(b.distance_to_thing(game.view_current) - a.distance_to_thing(game.view_current));
	}
}
