package com.gameinbucket.thanatopsis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;

import com.gameinbucket.rendering.Art;
import com.gameinbucket.rendering.AudioHandler;
import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.MovingThing;
import com.gameinbucket.rendering.Thing;
import com.gameinbucket.rendering.VectorMath;
import com.gameinbucket.world.CubeWorld;

public class GameHandler 
{
	//view parent
	public ViewHandler view;
	public StatePlay input;

	//global friction force
	public float friction;

	//viewing position and rotation
	public MovingThing view_current;

	//sprite rotation reference
	public float rot_to_view;

	//current light
	public FireLight light_current;

	//object lists
	public ArrayList<Thing> updatable;
	public ArrayList<Thing> collidable;

	public Protagonist protagonist;
	public ArrayList<Box> boxes;
	public ArrayList<Creature> fiends;
	public ArrayList<FireLight> lights;
	public ArrayList<Item> items;
	public ArrayList<Thing> scenery;
	public ArrayList<FireBall> fire_balls;

	//world instance
	public CubeWorld world;

	public int current_level = 0;
	public int[] level_list = {R.raw.t_level0, R.raw.t_level1, R.raw.t_level2, R.raw.t_level3, R.raw.t_level4, R.raw.t_level5, R.raw.t_level6, R.raw.t_level7};

	//generic particles
	public DamageEngine dmg_engine;

	//sort things based on distance to current view
	public ThingSorter thing_sorter;

	//audio sound pool
	public AudioHandler audio;
	public final MusicExecutor music;

	GameHandler(ViewHandler v)
	{
		view = v;

		//drag for moving objects
		friction = 0.90f;

		//initialize audio files
		audio = new AudioHandler(4);
		audio.load(view.context, R.raw.a_jitter_death);
		audio.load(view.context, R.raw.a_pistol_shoot);
		audio.load(view.context, R.raw.a_hand_punch);
		audio.load(view.context, R.raw.a_hand_miss);
		audio.load(view.context, R.raw.a_door);
		audio.load(view.context, R.raw.a_teleport);
		audio.load(view.context, R.raw.a_step);
		audio.load(view.context, R.raw.a_roar);
		audio.load(view.context, R.raw.a_door_open);
		audio.load(view.context, R.raw.a_wisp);
		audio.load(view.context, R.raw.a_long_death);
		audio.load(view.context, R.raw.a_fire_ball);
		
		music = new MusicExecutor(view.context);
		music.load(R.raw.a_thanatopsis);

		//initialize objects and lists
		updatable = new ArrayList<Thing>();
		collidable = new ArrayList<Thing>();

		boxes = new ArrayList<Box>();
		fiends = new ArrayList<Creature>();
		lights = new ArrayList<FireLight>();
		items = new ArrayList<Item>();
		scenery = new ArrayList<Thing>();
		fire_balls = new ArrayList<FireBall>();

		protagonist = new Protagonist(0, 0, 0, 0, 90, 0, this);
		updatable.add(protagonist);
		collidable.add(protagonist);

		//create generic world at 2x scale
		{
			world = new CubeWorld(2.0f, VectorMath.texture_8, 32, 3, 32, 4);

			//add cube texture types
			world.add_cube_type(0, 0, 1, 1);
			world.add_cube_type(1, 0, 2, 1);
			world.add_cube_type(2, 0, 3, 1);
			world.add_cube_type(3, 0, 4, 1);
			world.add_cube_type(4, 1, 5, 2);
			world.add_cube_type(0, 2, 1, 3);
		}

		//load map from image
		load_game();
		set_map(level_list[current_level]);

		//rendering perspective
		view_current = protagonist;

		//rotation of sprite to camera and current renderable light
		set_sprite_rotation();
		set_current_light();

		dmg_engine = new DamageEngine(this);
		thing_sorter = new ThingSorter(this);
	}

	public void view_open()
	{
		world.update();
	}

	public void view_close()
	{
		world.delete_buffers();
	}

	public void add_fire_ball(float x, float y, float z, float a, Thing p)
	{
		FireBall f = new FireBall(x, y, z, this, a, p);

		updatable.add(f);
		fire_balls.add(f);
	}

	public void add_monster(float x, float y, float z, int type)
	{
		Creature f;

		switch (type)
		{
		case 0:
			f = new Creeper(x, y, z, 0, 0, 0, this, false);
			break;
		case 1:
			f = new JitterSkull(x, y, z, 0, 0, 0, this);
			break;
		case 2:
			f = new Wraith(x, y, z, 0, 0, 0, this);
			break;
		case 3:
			f = new Creeper(x, y, z, 0, 0, 0, this, true);
			break;
		case 4:
			f = new Baron(x, y, z, 0, 0, 0, this, false);
			break;
		case 5:
			f = new Baron(x, y, z, 0, 0, 0, this, true);
			break;
		default:
			f = new Creeper(x, y, z, 0, 0, 0, this, false);
			break;
		}

		updatable.add(f);
		collidable.add(f);
		fiends.add(f);
	}

	private void clear_map()
	{
		updatable.clear();
		collidable.clear();

		boxes.clear();
		fiends.clear();
		lights.clear();
		items.clear();
		scenery.clear();
		fire_balls.clear();

		light_current = null;

		updatable.add(protagonist);
		collidable.add(protagonist);
	}

	public void new_game()
	{
		current_level = 0;
		protagonist.full_reset();

		save_game();

		clear_map();
		world.delete_map();

		set_map(level_list[current_level]);
		set_sprite_rotation();
		set_current_light();

		world.update();
	}

	public void next_level()
	{
		current_level++;
		protagonist.reset();

		if (current_level >= level_list.length)
			current_level = 0;

		save_game();

		clear_map();
		world.delete_map();

		set_map(level_list[current_level]);
		world.update();
	}

	public void save_game()
	{
		try
		{
			FileOutputStream fos = view.context.openFileOutput("save_file.dat", Context.MODE_PRIVATE);

			fos.write(current_level);
			fos.write(protagonist.pistol_ammo);

			if (protagonist.has_pistol) fos.write(1);
			else fos.write(0);

			//Main.print("saving game file: " + current_level + ", " + protagonist.pistol_ammo + ", " + protagonist.has_pistol);

			fos.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void save_death()
	{
		try
		{
			FileOutputStream fos = view.context.openFileOutput("save_file.dat", Context.MODE_PRIVATE);

			fos.write(0);
			fos.write(0);
			fos.write(0);

			//Main.print("saving game file: 0, 0, false");

			fos.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void load_game()
	{
		try
		{
			File file = view.context.getFileStreamPath("save_file.dat");

			if (file.exists())
			{
				FileInputStream fis = view.context.openFileInput("save_file.dat");

				current_level = fis.read();
				protagonist.pistol_ammo = fis.read();

				if (fis.read() == 1) protagonist.has_pistol = true;
				else protagonist.has_pistol = false;

				if (protagonist.has_pistol)
					protagonist.next_item = Protagonist.a_pistol;

				fis.close();

				//Main.print("loaded game file: " + current_level + ", " + protagonist.pistol_ammo + ", " + protagonist.has_pistol);
			}
			else
			{
				save_game();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void red_key_map(int[] maze_data)
	{
		ArrayList<Float> creeper_x = new ArrayList<Float>();
		ArrayList<Float> creeper_y = new ArrayList<Float>();

		for (int x = 0; x < world.width; x++)
		{
			for (int z = 0; z < world.length; z++)
			{
				switch (Art.rgb(maze_data[x + z * world.width]))
				{
				case Art.white:
					world.set_cube_wall(1, world.height, x, 0, z);
					break;
				case Art.dark_tan:
					world.set_cube_wall(4, world.height, x, 0, z);
					break;
				case Art.yellow:
					world.set_cube_wall(3, world.height, x, 0, z);
					break;
				case Art.blue:
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.murky_brown:
					world.set_cube(5, x, 0, z);
					world.set_cube(0, x, world.height - 1, z);
					break;
				case Art.half_yellow:
					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				case Art.green:
					world.set_cube(2, x, 0, z);
					break;
				case Art.purple:
					Portal p = new Portal((x + 0.5f) * world.scale, world.scale + 0.2f, (z + 0.5f) * world.scale, this, 0, 6, 1, 7, current_level < level_list.length - 1 ? true : false);

					if (current_level < level_list.length - 1)
						scenery.add(p);

					updatable.add(p);
					world.set_cube(2, x, 0, z);
					break;
				case Art.red:
					world.set_cube_floor_ceil(0, world.height, x, 0, z);

					creeper_x.add((x + 0.5f) * world.scale);
					creeper_y.add((z + 0.5f) * world.scale);
					break;
				case Art.half_red:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 0);
					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				case Art.pinky:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 3);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.half_green:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 0);
					world.set_cube(2, x, 0, z);
					break;
				case Art.pink:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 1);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.light_yellow:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 2);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.leaf:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 2);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.purplo:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 4);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.fishy:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 5);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.magenta:
					protagonist.view_p[0] = (x + 0.5f) * world.scale;
					protagonist.view_p[1] = world.scale;
					protagonist.view_p[2] = (z + 0.5f) * world.scale;

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.teal:
					boxes.add(new Door(x * world.scale, world.scale, z * world.scale, world.scale, world.scale, world.scale / 8.0f, VectorMath.texture_8, 1, 1, 2, 2, 0, 1, 1, 2, 1.8f, 1, 2, 2, 1.8f, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_front, Item.SKELETON_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.light_blue:
					boxes.add(new Door(x * world.scale, world.scale, (z + 1) * world.scale - world.scale / 8.0f, world.scale, world.scale, world.scale / 8.0f, VectorMath.texture_8, 1, 1, 0, 2, 2, 1, 1, 2, 2, 1, 1.8f, 2, 1.8f, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_bottom, Item.GREEN_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.light_green:
					boxes.add(new Door((x + 1) * world.scale - world.scale / 8.0f, world.scale, z * world.scale, world.scale / 8.0f, world.scale, world.scale, VectorMath.texture_8, 1.8f, 1, 2, 2, 1.8f, 1, 2, 2, 1, 1, 2, 2, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_right, Item.RED_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.lime_green:
					boxes.add(new Door(x * world.scale, world.scale, z * world.scale, world.scale / 8.0f, world.scale, world.scale, VectorMath.texture_8, 1.8f, 1, 2, 2, 1.8f, 1, 2, 2, 1, 1, 0, 2, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_left, Item.RED_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.half_teal:
					FireLight f = new FireLight(x, 1.7f * world.scale, z, this, 1.6f);
					updatable.add(f);
					collidable.add(f);
					lights.add(f);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.juicy:
					FireLight l = new FireLight(x, world.scale + 0.3f, z, this, 3.2f);
					updatable.add(l);
					collidable.add(l);
					lights.add(l);

					boxes.add(new Box((x + 0.5f) * world.scale - 0.16f, world.scale, (z + 0.5f) * world.scale - 0.48f, 0.32f, 0.2f, 0.96f, VectorMath.texture_8, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 , 1, 1));
					world.set_cube(2, x, 0, z);
					break;
				case Art.gray:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.PISTOL));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.orange:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.PISTOL_AMMO));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.dark_red:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.SKELETON_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.dark_green:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.GREEN_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.dark_blue:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.RED_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.sorta_red:
					Scenery sc1 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 5, 0, 6, 1);
					collidable.add(sc1);
					scenery.add(sc1);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.sorta_green:
					Scenery sc2 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 6, 0, 7, 1);
					collidable.add(sc2);
					scenery.add(sc2);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.sorta_blue:
					Scenery sc3 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 7, 0, 8, 1);
					collidable.add(sc3);
					scenery.add(sc3);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.yellowy_yellow:
					scenery.add(new Scenery((x + 0.5f) * world.scale, world.scale + 0.7f, (z + 0.5f) * world.scale, this, 7, 1, 8, 2));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.bluish_teal:
					scenery.add(new Scenery((x + 0.5f) * world.scale, world.scale + 0.7f, (z + 0.5f) * world.scale, this, 6, 1, 7, 2));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.magenty_magenta:
					scenery.add(new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 5, 1, 6, 2));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.lighty_gray:
					Scenery sc7 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 4, 0, 5, 1);
					collidable.add(sc7);
					scenery.add(sc7);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				}
			}
		}

		//randomize creeper locations
		int creeper_limit = (int)(creeper_x.size() / 1.5f + 0.5f);
		java.util.Random rand = new java.util.Random();

		for (int i = 0; i < creeper_limit; i++)
		{
			int a = rand.nextInt(creeper_x.size());

			add_monster(creeper_x.get(a), world.scale, creeper_y.get(a), 0);

			creeper_x.remove(a);
			creeper_y.remove(a);
		}
	}

	private void blue_key_map(int[] maze_data)
	{
		ArrayList<Float> creeper_x = new ArrayList<Float>();
		ArrayList<Float> creeper_y = new ArrayList<Float>();

		for (int x = 0; x < world.width; x++)
		{
			for (int z = 0; z < world.length; z++)
			{
				switch (Art.rgb(maze_data[x + z * world.width]))
				{
				case Art.white:
					world.set_cube_wall(1, world.height, x, 0, z);
					break;
				case Art.dark_tan:
					world.set_cube_wall(4, world.height, x, 0, z);
					break;
				case Art.yellow:
					world.set_cube_wall(3, world.height, x, 0, z);
					break;
				case Art.blue:
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.murky_brown:
					world.set_cube(5, x, 0, z);
					world.set_cube(0, x, world.height - 1, z);
					break;
				case Art.half_yellow:
					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				case Art.green:
					world.set_cube(2, x, 0, z);
					break;
				case Art.purple:
					Portal p = new Portal((x + 0.5f) * world.scale, world.scale + 0.2f, (z + 0.5f) * world.scale, this, 0, 6, 1, 7, current_level < level_list.length - 1 ? true : false);

					if (current_level < level_list.length - 1)
						scenery.add(p);

					updatable.add(p);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.red:
					world.set_cube_floor_ceil(0, world.height, x, 0, z);

					creeper_x.add((x + 0.5f) * world.scale);
					creeper_y.add((z + 0.5f) * world.scale);
					break;
				case Art.half_red:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 0);
					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				case Art.pinky:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 3);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.half_green:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 0);
					world.set_cube(2, x, 0, z);
					break;
				case Art.pink:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 1);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.light_yellow:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 2);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.leaf:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 2);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.purplo:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 4);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.fishy:
					add_monster((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, 5);
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.magenta:
					protagonist.view_p[0] = (x + 0.5f) * world.scale;
					protagonist.view_p[1] = world.scale;
					protagonist.view_p[2] = (z + 0.5f) * world.scale;

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.teal:
					boxes.add(new Door(x * world.scale, world.scale, z * world.scale, world.scale, world.scale, world.scale / 8.0f, VectorMath.texture_8, 1, 1, 2, 2, 0, 1, 1, 2, 1.8f, 1, 2, 2, 1.8f, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_front, Item.SKELETON_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.light_blue:
					boxes.add(new Door(x * world.scale, world.scale, (z + 1) * world.scale - world.scale / 8.0f, world.scale, world.scale, world.scale / 8.0f, VectorMath.texture_8, 1, 1, 0, 2, 2, 1, 1, 2, 2, 1, 1.8f, 2, 1.8f, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_bottom, Item.GREEN_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.light_green:
					boxes.add(new Door((x + 1) * world.scale - world.scale / 8.0f, world.scale, z * world.scale, world.scale / 8.0f, world.scale, world.scale, VectorMath.texture_8, 1.8f, 1, 2, 2, 1.8f, 1, 2, 2, 1, 1, 2, 2, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_right, Item.RED_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.lime_green:
					boxes.add(new Door(x * world.scale, world.scale, z * world.scale, world.scale / 8.0f, world.scale, world.scale, VectorMath.texture_8, 1.8f, 1, 2, 2, 1.8f, 1, 2, 2, 1, 1, 0, 2, 2, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, this, Box.box_left, Item.RED_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.half_teal:
					FireLight f = new FireLight(x, 1.7f * world.scale, z, this, 1.6f);
					updatable.add(f);
					collidable.add(f);
					lights.add(f);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.juicy:
					FireLight l = new FireLight(x, world.scale + 0.3f, z, this, 3.2f);
					updatable.add(l);
					collidable.add(l);
					lights.add(l);

					boxes.add(new Box((x + 0.5f) * world.scale - 0.16f, world.scale, (z + 0.5f) * world.scale - 0.48f, 0.32f, 0.2f, 0.96f, VectorMath.texture_8, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 , 1, 1));
					world.set_cube(2, x, 0, z);
					break;
				case Art.gray:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.PISTOL));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.orange:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.PISTOL_AMMO));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.dark_red:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.SKELETON_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.dark_green:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.GREEN_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.dark_blue:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.RED_KEY));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.sorta_red:
					Scenery sc1 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 5, 0, 6, 1);
					collidable.add(sc1);
					scenery.add(sc1);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.sorta_green:
					Scenery sc2 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 6, 0, 7, 1);
					collidable.add(sc2);
					scenery.add(sc2);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.sorta_blue:
					Scenery sc3 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 7, 0, 8, 1);
					collidable.add(sc3);
					scenery.add(sc3);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.yellowy_yellow:
					scenery.add(new Scenery((x + 0.5f) * world.scale, world.scale + 0.7f, (z + 0.5f) * world.scale, this, 7, 1, 8, 2));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.bluish_teal:
					scenery.add(new Scenery((x + 0.5f) * world.scale, world.scale + 0.7f, (z + 0.5f) * world.scale, this, 6, 1, 7, 2));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.magenty_magenta:
					scenery.add(new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 5, 1, 6, 2));
					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				case Art.lighty_gray:
					Scenery sc7 = new Scenery((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, 4, 0, 5, 1);
					collidable.add(sc7);
					scenery.add(sc7);

					world.set_cube_floor_ceil(0, world.height, x, 0, z);
					break;
				}
			}
		}

		//randomize creeper locations
		int creeper_limit = (int)(creeper_x.size() / 1.5f + 0.5f);
		java.util.Random rand = new java.util.Random();

		for (int i = 0; i < creeper_limit; i++)
		{
			int a = rand.nextInt(creeper_x.size());

			add_monster(creeper_x.get(a), world.scale, creeper_y.get(a), 0);

			creeper_x.remove(a);
			creeper_y.remove(a);
		}
	}

	private void green_key_map(int[] maze_data)
	{
		ArrayList<Float> creeper_x = new ArrayList<Float>();
		ArrayList<Float> creeper_y = new ArrayList<Float>();

		for (int x = 0; x < world.width; x++)
		{
			for (int z = 0; z < world.length; z++)
			{
				switch (Art.rgb(maze_data[x + z * world.width]))
				{
				case Art.white:
					world.set_cube_wall(3, world.height, x, 0, z);
					break;
				case Art.blue:
					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				case Art.red:
					world.set_cube_floor_ceil(3, world.height, x, 0, z);

					creeper_x.add((x + 0.5f) * world.scale);
					creeper_y.add((z + 0.5f) * world.scale);
					break;
				case Art.magenta:
					protagonist.view_p[0] = (x + 0.5f) * world.scale;
					protagonist.view_p[1] = world.scale;
					protagonist.view_p[2] = (z + 0.5f) * world.scale;

					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				case Art.orange:
					items.add(new Item((x + 0.5f) * world.scale, world.scale, (z + 0.5f) * world.scale, this, Item.PISTOL_AMMO));
					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				case Art.purple:
					Portal p = new Portal((x + 0.5f) * world.scale, world.scale + 0.2f, (z + 0.5f) * world.scale, this, 0, 6, 1, 7, current_level < level_list.length - 1 ? true : false);

					if (current_level < level_list.length - 1)
						scenery.add(p);

					updatable.add(p);
					world.set_cube_floor_ceil(3, world.height, x, 0, z);
					break;
				}
			}
		}

		//randomize creeper locations
		int creeper_limit = (int)(creeper_x.size() / 1.5f + 0.5f);
		java.util.Random rand = new java.util.Random();

		for (int i = 0; i < creeper_limit; i++)
		{
			int a = rand.nextInt(creeper_x.size());

			add_monster(creeper_x.get(a), world.scale, creeper_y.get(a), 0);

			creeper_x.remove(a);
			creeper_y.remove(a);
		}
	}

	private void set_map(int r)
	{
		//get level image data
		{
			Bitmap maze_image = Art.load_image_data(view.context, r);
			int[] maze_data;

			int width = maze_image.getWidth();
			int height = maze_image.getHeight();

			maze_data = new int[width * height];
			maze_image.getPixels(maze_data, 0, width, 0, 0, width, height);

			maze_image.recycle();
			
			int key0 = Art.rgb(maze_data[0]);
			int key1 = Art.rgb(maze_data[1]);
			
			maze_data[0] = 0;
			maze_data[1] = 0;

			if (key0 == Art.red) red_key_map(maze_data);
			else if (key0 == Art.green) green_key_map(maze_data);
			else blue_key_map(maze_data);

			if (key1 == Art.red) protagonist.view_r[1] = 180;
			else if (key1 == Art.green) protagonist.view_r[1] = 270;
			else if (key1 == Art.blue) protagonist.view_r[1] = 0;
		}

		//modify light position based on location of neighboring walls
		for (int i = 0; i < lights.size(); i++)
		{
			FireLight f = lights.get(i);

			if (world.get_cube((int)f.view_p[0] - 1, 1, (int)f.view_p[2]) >= 0)
			{
				f.view_p[0] = (f.view_p[0] + 0.1f) * world.scale;
				f.view_p[2] = (f.view_p[2] + 0.5f) * world.scale;
				f.set_offset(0.4f, 0.0f);
			}
			else if (world.get_cube((int)f.view_p[0] + 1, 1, (int)f.view_p[2]) >= 0)
			{
				f.view_p[0] = (f.view_p[0] + 0.9f) * world.scale;
				f.view_p[2] = (f.view_p[2] + 0.5f) * world.scale;
				f.set_offset(-0.4f, 0.0f);
			}
			else if (world.get_cube((int)f.view_p[0], 1, (int)f.view_p[2] - 1) >= 0)
			{
				f.view_p[0] = (f.view_p[0] + 0.5f) * world.scale;
				f.view_p[2] = (f.view_p[2] + 0.1f) * world.scale;
				f.set_offset(0.0f, 0.4f);
			}
			else if (world.get_cube((int)f.view_p[0], 1, (int)f.view_p[2] + 1) >= 0)
			{
				f.view_p[0] = (f.view_p[0] + 0.5f) * world.scale;
				f.view_p[2] = (f.view_p[2] + 0.9f) * world.scale;
				f.set_offset(0.0f, -0.4f);
			}
			else
			{
				f.view_p[0] = (f.view_p[0] + 0.5f) * world.scale;
				f.view_p[2] = (f.view_p[2] + 0.5f) * world.scale;
				f.set_offset(0.0f, 0.0f);
			}
		}
	}

	public void set_sprite_rotation()
	{
		rot_to_view = -VectorMath.deg_to_rad(view_current.view_r[1]);
	}

	public void set_current_light()
	{
		FireLight f = null;
		float greatest = Float.MAX_VALUE;

		for (int i = 0; i < lights.size(); i++)
		{
			float dist = view_current.distance_to_thing(lights.get(i));

			if (dist < greatest)
			{
				greatest = dist;
				f = lights.get(i);
			}
		}

		light_current = f;
	}
}
