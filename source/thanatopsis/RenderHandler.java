package com.gameinbucket.thanatopsis;

import java.util.ArrayList;
import java.util.Collections;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gameinbucket.rendering.Art;
import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.FrameBuffer;
import com.gameinbucket.rendering.GeometryBuffer;
import com.gameinbucket.rendering.MatrixMath;
import com.gameinbucket.rendering.ArrayBuffer;
import com.gameinbucket.rendering.MovingThing;
import com.gameinbucket.rendering.Shader;
import com.gameinbucket.rendering.VectorMath;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class RenderHandler implements GLSurfaceView.Renderer 
{
	//view parent
	private ViewHandler view;

	//shader rendering
	public Shader s_current;
	public Shader s_pre_color;
	public Shader s_pre_texture;
	public Shader s_pre_light;
	public Shader s_pre_color_fire;
	public Shader s_post_texture;
	public Shader s_post_vignette;

	//textures
	private int t_input_banners;
	private int t_font;
	private int t_cubes;
	private int t_fire_ramp;
	private int t_hand;
	private int t_fiends;

	//matrices
	private float[] perspective_matrix = new float[16];
	private float[] orthographic_matrix = new float[16];
	private float[] modelview_matrix = new float[16];
	private float[] mvp_matrix = new float[16];
	//public FrustumCuller culler = new FrustumCuller();

	//texture of main drawing image
	public int vt_scale = 4;
	private int view_texture_width;
	private int view_texture_height;
	public float view_scale;

	//native buffer allocations
	private GeometryBuffer geometry_dynamic_other;

	//vertex and index buffer objects
	private ArrayBuffer screen_buffer_object;

	//frame and texture buffers for deferred rendering
	public FrameBuffer[] draw_buffer;
	public FrameBuffer[] screen_buffer;

	public StateHandler state_function;
	public StatePlay state_play;
	public StateMainMenu state_main_menu;
	public StateOptions state_options;

	//sleeping time
	private int sleep_time;
	private final int fixed_step = 1000 / 30;
	private long begin_time = System.currentTimeMillis();

	public RenderHandler(ViewHandler v)
	{
		view = v;

		//initialize geometry buffers
		geometry_dynamic_other = new GeometryBuffer(1200, 1200);

		//Main.print("renderer initialized at " + System.currentTimeMillis());
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BACK);

		//textures
		t_input_banners = Art.load_texture(view.context, R.raw.t_input_banners, GLES20.GL_CLAMP_TO_EDGE);
		t_font = Art.load_texture(view.context, R.raw.t_font, GLES20.GL_CLAMP_TO_EDGE);
		t_hand = Art.load_texture(view.context, R.raw.t_hand, GLES20.GL_CLAMP_TO_EDGE);
		t_fiends = Art.load_texture(view.context, R.raw.t_fiends, GLES20.GL_CLAMP_TO_EDGE);
		t_fire_ramp = Art.load_texture(view.context, R.raw.t_fire_ramp, GLES20.GL_CLAMP_TO_EDGE);

		//t_cubes
		{
			Bitmap img = Art.load_image_data(view.context, R.raw.t_cubes);

			int i_width = img.getWidth();
			int i_height = img.getHeight();

			int[] pixels = new int[i_width * i_height];

			img.getPixels(pixels, 0, i_width, 0, 0, i_width, i_height);
			img.recycle();

			//planks
			Art.mult_pixels(pixels, i_width, 0 , 0, 16, 16, 128.0f / 255.0f, 64.0f / 255.0f, 0.0f / 255.0f);
			Art.mult_pixels(pixels, i_width, 0 , 32, 16, 48, 128.0f / 255.0f, 64.0f / 255.0f, 0.0f / 255.0f);
			Art.mult_pixels(pixels, i_width, 5 * 16 , 0, 8 * 16, 16, 128.0f / 255.0f, 64.0f / 255.0f, 0.0f / 255.0f);
			//stone
			Art.mult_pixels(pixels, i_width, 16, 0, 32, 16, 110.0f / 255.0f, 110.0f / 255.0f, 110.0f / 255.0f);
			Art.mult_pixels(pixels, i_width, 4 * 16, 16, 5 * 16, 32, 110.0f / 255.0f, 110.0f / 255.0f, 110.0f / 255.0f);
			//Art.mult_pixels(pixels, i_width, 16, 0, 32, 16, 179.0f / 255.0f, 206.0f / 255.0f, 226.0f / 255.0f);
			//grass
			Art.mult_pixels(pixels, i_width, 32, 0, 48, 16, 42.0f / 255.0f, 175.0f / 255.0f, 3.0f / 255.0f);
			//plagued
			Art.mult_pixels(pixels, i_width, 48, 0, 64, 16, 76.0f  / 255.0f, 34.0f  / 255.0f, 37.0f  / 255.0f);
			//door front and back
			Art.mult_pixels(pixels, i_width, 0 , 16, 32, 32, 156.0f / 255.0f, 78.0f / 255.0f, 0.0f / 255.0f);

			t_cubes = Art.load_texture(pixels, i_width, i_height, GLES20.GL_CLAMP_TO_EDGE);
		}

		//programs
		int[] attributes0 = {0, 1};
		s_pre_color = generate_program(R.raw.s_pre_color_v, R.raw.s_pre_color_f, attributes0);
		s_pre_color_fire = generate_program(R.raw.s_pre_color_v, R.raw.s_pre_color_fire_f, attributes0);

		int[] attributes1 = {0, 1, 2};
		s_pre_texture = generate_program(R.raw.s_pre_texture_v, R.raw.s_pre_texture_f, attributes1);

		int[] attributes2 = {0, 1, 2, 3};
		s_pre_light = generate_program(R.raw.s_pre_light_v, R.raw.s_pre_light_f, attributes2);

		int[] attributes3 = {0};
		s_post_texture = generate_program(R.raw.s_post_texture_v, R.raw.s_post_texture_f, attributes3);
		s_post_vignette = generate_program(R.raw.s_post_texture_v, R.raw.s_post_vignette_f, attributes3);

		//Main.print("surface created at " + System.currentTimeMillis());
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int native_width, int native_height) 
	{
		screen_buffer = new FrameBuffer[1];

		/*{
			float target_ratio;

			//target_ratio = 2.0f / 1.0f; 
			//target_ratio = 800.0f / 600.0f;
			//target_ratio = 1280.0f / 736.0f;
			target_ratio = 1280.0f / 720.0f;
			//target_ratio = 320.0f / 200.0f;
			//target_ratio = 1.0f;

			if ((float)native_width / (float)native_height > target_ratio)
			{
				int virtual_width = (int)Math.floor((float)native_height * target_ratio);
				screen_buffer[0] = new FrameBuffer(FrameBuffer.TYPE_VIEW, GLES20.GL_NEAREST, virtual_width, native_height, (native_width - virtual_width) / 2, 0);
			}
			else
			{
				int virtual_height = (int)Math.floor((float)native_width / target_ratio);
				screen_buffer[0] = new FrameBuffer(FrameBuffer.TYPE_VIEW, GLES20.GL_NEAREST, native_width, virtual_height, 0, (native_height - virtual_height) / 2);
			}
		}*/

		//int target_width = native_width;
		//int target_height = native_width / 2;
		//screen_buffer[0] = new FrameBuffer(FrameBuffer.TYPE_VIEW, GLES20.GL_NEAREST, target_width, target_height, (native_width - target_width) / 2, (native_height - target_height) / 2);
		screen_buffer[0] = new FrameBuffer(FrameBuffer.TYPE_VIEW, GLES20.GL_NEAREST, native_width, native_height, 0, 0);

		//set the scaling of the view textures as a power of two
		{
			view_texture_width = 2;
			view_texture_height = 2;

			while (view_texture_width < screen_buffer[0].width)
				view_texture_width *= 2;

			while (view_texture_height < screen_buffer[0].height)
				view_texture_height *= 2;
		}

		view_scale = screen_buffer[0].height / 254.333f;

		//initialize drawing and screen frame buffers
		draw_buffer = new FrameBuffer[1];
		draw_buffer[0] = new FrameBuffer(FrameBuffer.TYPE_DRAW, GLES20.GL_NEAREST, view_texture_width / vt_scale, view_texture_height / vt_scale, 0, 0);

		//load matrices
		MatrixMath.load_perspective(perspective_matrix, (float)screen_buffer[0].width / (float)screen_buffer[0].height * 34.5f, 0.01f, 12.5f, (float)screen_buffer[0].width / (float)screen_buffer[0].height);
		MatrixMath.load_orthographic(orthographic_matrix, 0, screen_buffer[0].width, 0, screen_buffer[0].height, 0, 1);

		//load screen drawing geometry for buffer object
		{
			GeometryBuffer geometry_static_screen = new GeometryBuffer(4, 6);
			geometry_static_screen.add(VectorMath.create_rectangle(0, 0, screen_buffer[0].width, screen_buffer[0].height, 0, 0, 0, 0, 0), 4);
			geometry_static_screen.update();

			screen_buffer_object = geometry_static_screen.generate_array_buffer();
		}

		//finish creating the game state
		view.game.view_open();

		if (state_function == null)
		{
			//add different types of input, updating, and rendering
			state_play = new StatePlay(this, view.game);
			state_main_menu = new StateMainMenu(this, view.game);
			state_options = new StateOptions(this, view.game);

			state_function = state_main_menu;
		}

		//Main.print("surface changed to " + native_width + "w " + native_height +  "h with " + screen_buffer[0].width + "vw " + screen_buffer[0].height + "vh at " + System.currentTimeMillis());
	}

	private void sleep()
	{
		sleep_time = (int)(fixed_step - (System.currentTimeMillis() - begin_time));

		if (sleep_time > 0) 
		{
			try 
			{
				Thread.sleep(sleep_time);
			} 
			catch (Exception e)
			{

			}
		}

		begin_time = System.currentTimeMillis();
	}

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{
		sleep();

		state_function.update();
		state_function.draw();
	}

	public void draw_2d_options()
	{
		set_shader(s_pre_texture);
		orthographic_matrix(0, 0);

		GLES20.glEnable(GLES20.GL_BLEND);

		//render protagonist hud
		if (view.game.protagonist.draw())
		{
			geometry_dynamic_other.clear();

			geometry_dynamic_other.add(view.game.protagonist.image(), 4);
			geometry_dynamic_other.update();

			u_texture(0, t_hand);
			draw_batch(geometry_dynamic_other);
		}

		//protagonist's coloring
		if (view.game.protagonist.overlay_a > 0.0f)
		{
			set_shader(s_pre_color);
			orthographic_matrix(0, 0);

			//color overlay
			geometry_dynamic_other.clear();
			geometry_dynamic_other.add(VectorMath.create_rectangle(0, 0, screen_buffer[0].width, screen_buffer[0].height, view.game.protagonist.overlay_r, view.game.protagonist.overlay_g, view.game.protagonist.overlay_b, view.game.protagonist.overlay_a), 4);
			geometry_dynamic_other.update();

			draw_batch(geometry_dynamic_other);
			//

			set_shader(s_pre_texture);
			orthographic_matrix(0, 0);
		}

		//render input banners
		geometry_dynamic_other.clear();

		for (int i = 0; i < state_options.inputs.length; i++)
			geometry_dynamic_other.add(state_options.inputs[i].image(), 4);

		geometry_dynamic_other.update();

		u_texture(0, t_input_banners);
		draw_batch(geometry_dynamic_other);
		//

		GLES20.glDisable(GLES20.GL_BLEND);
	}

	public void draw_2d_welcome()
	{
		set_shader(s_pre_texture);
		orthographic_matrix(0, 0);

		GLES20.glEnable(GLES20.GL_BLEND);

		//render protagonist hud
		if (view.game.protagonist.draw())
		{
			geometry_dynamic_other.clear();

			geometry_dynamic_other.add(view.game.protagonist.image(), 4);
			geometry_dynamic_other.update();

			u_texture(0, t_hand);
			draw_batch(geometry_dynamic_other);
		}

		//protagonist's coloring
		if (view.game.protagonist.overlay_a > 0.0f)
		{
			set_shader(s_pre_color);
			orthographic_matrix(0, 0);

			//color overlay
			geometry_dynamic_other.clear();
			geometry_dynamic_other.add(VectorMath.create_rectangle(0, 0, screen_buffer[0].width, screen_buffer[0].height, view.game.protagonist.overlay_r, view.game.protagonist.overlay_g, view.game.protagonist.overlay_b, view.game.protagonist.overlay_a), 4);
			geometry_dynamic_other.update();

			draw_batch(geometry_dynamic_other);
			//

			set_shader(s_pre_texture);
			orthographic_matrix(0, 0);
		}

		//render input banners
		geometry_dynamic_other.clear();

		for (int i = 0; i < state_main_menu.inputs.length; i++)
			geometry_dynamic_other.add(state_main_menu.inputs[i].image(), 4);

		geometry_dynamic_other.update();

		u_texture(0, t_input_banners);
		draw_batch(geometry_dynamic_other);
		//

		//render final overlay
		if (state_main_menu.ending_alpha > 0.0f)
		{
			set_shader(s_pre_color);
			orthographic_matrix(0, 0);

			//color overlay
			geometry_dynamic_other.clear();
			geometry_dynamic_other.add(VectorMath.create_rectangle(0, 0, screen_buffer[0].width, screen_buffer[0].height, 0.0f, 0.0f, 0.0f, state_main_menu.ending_alpha), 4);
			geometry_dynamic_other.update();

			draw_batch(geometry_dynamic_other);
			//
		}

		GLES20.glDisable(GLES20.GL_BLEND);
	}

	public void draw_2d()
	{
		set_shader(s_pre_texture);
		orthographic_matrix(0, 0);

		GLES20.glEnable(GLES20.GL_BLEND);

		//render protagonist hud
		if (view.game.protagonist.draw())
		{
			geometry_dynamic_other.clear();

			geometry_dynamic_other.add(view.game.protagonist.image(), 4);
			geometry_dynamic_other.update();

			u_texture(0, t_hand);
			draw_batch(geometry_dynamic_other);
		}

		if (view.game.protagonist.overlay_a > 0.0f)
		{
			set_shader(s_pre_color);
			orthographic_matrix(0, 0);

			//color overlay
			geometry_dynamic_other.clear();
			geometry_dynamic_other.add(VectorMath.create_rectangle(0, 0, screen_buffer[0].width, screen_buffer[0].height, view.game.protagonist.overlay_r, view.game.protagonist.overlay_g, view.game.protagonist.overlay_b, view.game.protagonist.overlay_a), 4);
			geometry_dynamic_other.update();

			draw_batch(geometry_dynamic_other);
			//

			if (view.game.protagonist.escaped != null && view.game.protagonist.overlay_a == 1.0f)
			{
				set_shader(s_pre_texture);
				orthographic_matrix(0, 0);

				//render final text
				geometry_dynamic_other.clear();
				VectorMath.add_complex_text(geometry_dynamic_other, view.game.protagonist.escaped.message(), 10, screen_buffer[0].height - VectorMath.font_size_y * view_scale - 10, view_scale, 1.0f, 1.0f, 1.0f, view.game.protagonist.escaped.alpha);
				geometry_dynamic_other.update();

				u_texture(0, t_font);
				draw_batch(geometry_dynamic_other);
				//
			}
		}

		GLES20.glDisable(GLES20.GL_BLEND);
	}

	public void draw_3d(int buffer, Shader shader)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, draw_buffer[buffer].frame_buffer());

		GLES20.glViewport(draw_buffer[buffer].offset_w, draw_buffer[buffer].offset_h, draw_buffer[buffer].width, draw_buffer[buffer].height);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		perspective_matrix(view.game.view_current);

		//particles
		if (view.game.light_current != null)
		{
			set_shader(s_pre_color_fire);
			uniform_mvp_matrix();

			u_texture(0, t_fire_ramp);

			geometry_dynamic_other.clear();

			FireEngine e = view.game.light_current.engine;

			for (int p = 0; p < e.particles.length; p++)
				geometry_dynamic_other.add(e.particles[p].image(), 4);

			geometry_dynamic_other.update(); 
			draw_batch(geometry_dynamic_other);
		}

		if (view.game.dmg_engine.active)
		{
			set_shader(s_pre_color);
			uniform_mvp_matrix();

			geometry_dynamic_other.clear();

			for (int p = 0; p < view.game.dmg_engine.particles.length; p++)
				geometry_dynamic_other.add(view.game.dmg_engine.particles[p].image(), 4);

			geometry_dynamic_other.update(); 
			draw_batch(geometry_dynamic_other);
		}
		//

		//world and objects rendering
		set_shader(shader);
		uniform_mvp_matrix();

		//set light uniforms
		if (view.game.light_current != null)
			view.game.light_current.set_light_uniforms(s_current.id);
		//

		//render world
		u_texture(0, t_cubes);

		ArrayList<ArrayBuffer> world_list = view.game.world.portal_map.render_list(view.game.view_current);

		for (int i = 0; i < world_list.size(); i++)
			draw_batch(world_list.get(i));

		//render boxes
		geometry_dynamic_other.clear();

		for (int i = 0; i < view.game.boxes.size(); i++)
		{
			Box b = view.game.boxes.get(i);

			for (int s = 0; s < Box.box_sides; s++)
				geometry_dynamic_other.add(b.image(s), 4);
		}

		if (view.game.light_current != null)
		{
			Torch b = view.game.light_current.torch_box;

			for (int s = 0; s < Box.box_sides; s++)
				geometry_dynamic_other.add(b.image(s), 4);
		}

		geometry_dynamic_other.update(); 
		draw_batch(geometry_dynamic_other);
		//

		GLES20.glEnable(GLES20.GL_BLEND);

		//render items and scenery
		geometry_dynamic_other.clear();

		for (int i = 0; i < view.game.items.size(); i++)
			geometry_dynamic_other.add(view.game.items.get(i).image(), 4);

		for (int i = 0; i < view.game.scenery.size(); i++)
			geometry_dynamic_other.add(view.game.scenery.get(i).image(), 4);

		geometry_dynamic_other.update(); 
		draw_batch(geometry_dynamic_other);
		//

		//render monsters
		geometry_dynamic_other.clear();

		Collections.sort(view.game.fiends, view.game.thing_sorter);

		for (int i = 0; i < view.game.fiends.size(); i++)
			geometry_dynamic_other.add(view.game.fiends.get(i).image(), 4);

		for (int i = 0; i < view.game.fire_balls.size(); i++)
			geometry_dynamic_other.add(view.game.fire_balls.get(i).image(), 4);

		geometry_dynamic_other.update();

		u_texture(0, t_fiends);
		draw_batch(geometry_dynamic_other);
		//

		GLES20.glDisable(GLES20.GL_BLEND);
	}

	public void draw_screen(int buffer, Shader shader, int texture0)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, screen_buffer[buffer].frame_buffer());

		GLES20.glViewport(screen_buffer[buffer].offset_w, screen_buffer[buffer].offset_h, screen_buffer[buffer].width, screen_buffer[buffer].height);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		set_shader(shader);
		orthographic_matrix(0, 0);

		GLES20.glUniform1f(uniform("u_width"), screen_buffer[buffer].width);
		GLES20.glUniform1f(uniform("u_height"), screen_buffer[buffer].height);
		GLES20.glUniform1f(uniform("u_offset_width"), screen_buffer[buffer].offset_w);
		GLES20.glUniform1f(uniform("u_offset_height"), screen_buffer[buffer].offset_h);

		u_texture(0, texture0);

		draw_batch(screen_buffer_object);
	}

	public void draw_screen(int buffer, Shader shader, int texture0, int texture1)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, screen_buffer[buffer].frame_buffer());

		GLES20.glViewport(screen_buffer[buffer].offset_w, screen_buffer[buffer].offset_h, screen_buffer[buffer].width, screen_buffer[buffer].height);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		set_shader(shader);
		orthographic_matrix(0, 0);

		GLES20.glUniform1f(uniform("u_width"), screen_buffer[buffer].width);
		GLES20.glUniform1f(uniform("u_height"), screen_buffer[buffer].height);
		GLES20.glUniform1f(uniform("u_offset_width"), screen_buffer[buffer].offset_w);
		GLES20.glUniform1f(uniform("u_offset_height"), screen_buffer[buffer].offset_h);

		u_texture(0, texture0);
		u_texture(1, texture1);

		draw_batch(screen_buffer_object);
	}

	public void draw_batch(GeometryBuffer gb)
	{
		s_current.pointers(gb);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, gb.ic(), GLES20.GL_UNSIGNED_SHORT, gb.ib());
	}

	public void draw_batch(ArrayBuffer ab)
	{
		ab.bind();

		s_current.pointers();
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, ab.ic(), GLES20.GL_UNSIGNED_SHORT, 0);

		ab.unbind();
	}

	private void perspective_matrix(MovingThing v)
	{
		MatrixMath.load_identity(modelview_matrix);
		v.set_matrix(modelview_matrix);

		MatrixMath.multiply(mvp_matrix, perspective_matrix, modelview_matrix);
		//culler.update_frustum_planes(mvp_matrix);
	}

	private void uniform_mvp_matrix()
	{
		GLES20.glUniformMatrix4fv(uniform("u_mvp"), 1, false, mvp_matrix, 0);
		GLES20.glUniformMatrix4fv(uniform("u_mv"), 1, false, modelview_matrix, 0);
	}

	/*private void perspective_matrix(float x, float y, float z, float rx, float ry, float rz)
	{
		MatrixMath.load_identity(modelview_matrix);
		MatrixMath.rotate_x(modelview_matrix, VectorMath.deg_to_rad(rx));
		MatrixMath.rotate_y(modelview_matrix, VectorMath.deg_to_rad(ry));
		MatrixMath.rotate_z(modelview_matrix, VectorMath.deg_to_rad(rz));
		MatrixMath.translate(modelview_matrix, x, y, z);

		MatrixMath.multiply(mvp_matrix, perspective_matrix, modelview_matrix);
		GLES20.glUniformMatrix4fv(uniform("u_mvp"), 1, false, mvp_matrix, 0);
		GLES20.glUniformMatrix4fv(uniform("u_mv"), 1, false, modelview_matrix, 0);
	}*/

	private void orthographic_matrix(float x, float y)
	{
		MatrixMath.load_identity(modelview_matrix);
		MatrixMath.translate(modelview_matrix, x, y, -1);

		MatrixMath.multiply(mvp_matrix, orthographic_matrix, modelview_matrix);
		GLES20.glUniformMatrix4fv(uniform("u_mvp"), 1, false, mvp_matrix, 0);
	}

	public void enable_3d()
	{
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}

	public void disable_3d()
	{
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
	}

	private void u_texture(int i, int t)
	{
		switch (i)
		{
		case 0:
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
			GLES20.glUniform1i(uniform("u_texture0"), 0);
			break;
		case 1:
			GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
			GLES20.glUniform1i(uniform("u_texture1"), 1);
			break;
		case 2:
			GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
			GLES20.glUniform1i(uniform("u_texture2"), 2);
			break;
		case 3:
			GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
			GLES20.glUniform1i(uniform("u_texture3"), 3);
			break;
		case 4:
			GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
			GLES20.glUniform1i(uniform("u_texture4"), 4);
			break;
		}
	}

	private int uniform(String u)
	{
		return GLES20.glGetUniformLocation(s_current.id, u);
	}

	private void set_shader(Shader s)
	{
		s_current = s;
		s_current.use();
	}

	private int generate_shader(String s, int t)
	{
		int shader = GLES20.glCreateShader(t);

		GLES20.glShaderSource(shader, s);
		GLES20.glCompileShader(shader);

		int[] status = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);

		if (status[0] == 0) 
		{
			android.util.Log.e("printout", "could not compile shader " +  t + ": " + s);
			android.util.Log.e("printout", "info log: " + GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);

			throw new RuntimeException("error compiling shader");
		}

		return shader;
	}

	private Shader generate_program(int v, int f, int[] attributes)
	{
		int vertex = generate_shader(Art.load_text(view.context, v), GLES20.GL_VERTEX_SHADER);
		int fragment = generate_shader(Art.load_text(view.context, f), GLES20.GL_FRAGMENT_SHADER);

		int program = GLES20.glCreateProgram();

		GLES20.glAttachShader(program, vertex);			
		GLES20.glAttachShader(program, fragment);

		for (int i = 0; i < attributes.length; i++)
			GLES20.glBindAttribLocation(program, i, VectorMath.attribute_name[attributes[i]]);

		GLES20.glLinkProgram(program);

		int[] status = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);

		if (status[0] == 0) 
		{
			android.util.Log.e("printout", "could not link program: ");
			android.util.Log.e("printout", GLES20.glGetProgramInfoLog(program));
			GLES20.glDeleteProgram(program);

			throw new RuntimeException("error compiling shader program");
		}

		return new Shader(program, attributes);
	}
}