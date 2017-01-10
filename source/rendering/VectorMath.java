package com.gameinbucket.rendering;

public class VectorMath 
{
	public static int shortsize = 2;
	public static int floatsize = 4;

	public static final String[] attribute_name = {"a_position", "a_color", "a_texture", "a_normal"};
	public static int[] attribute_size = {3, 4, 2, 3};
	public static int[] offset_size;
	public static int stride_size = 0;

	public static int[] offset_bytes;
	public static int stride_bytes = 0;

	public static float world_pixel = 1.0f / 16.0f;
	public static float texture_full = 1.0f;
	public static float texture_8 = 16.0f / 128.0f;
	public static float texture_4 = 16.0f / 64.0f;

	public static String font_chars = "abcdefghijklmnopqrstuvwxyz,;.!+-0123456789";
	public static int font_size_x = 8;
	public static int font_size_y = 8;
	public static float font_pixel_size_x = font_size_x / 64.0f;
	public static float font_pixel_size_y = font_size_y / 64.0f;
	public static int font_cell = 64 / font_size_x;

	public static float rad_180 = deg_to_rad(180);
	public static float rad_90 = deg_to_rad(90);

	private static float[] temporary_quad;

	//initialize vertex data
	static
	{
		offset_size = new int[attribute_size.length];
		offset_bytes = new int[attribute_size.length];

		for (int i = 0; i < attribute_size.length; i++)
		{
			for (int p = 0; p < i; p++)
				offset_size[i] += attribute_size[p];

			stride_size += attribute_size[i];
			offset_bytes[i] = offset_size[i] * floatsize;
		}

		stride_bytes = stride_size * floatsize;

		temporary_quad = new float[4 * stride_size];
	}

	public static float deg_to_rad(float d)
	{
		return d * 0.017453292519943f;
	}

	public static float rad_to_deg(float r)
	{
		return r * 57.29577951308232f;
	}

	public static float degree_range(float d)
	{
		if (d >= 360)
			d %= 360;
		else while (d < 0)
			d += 360;

		return d;
	}

	public static float radian_range(float r)
	{
		if (r >= Math.PI)
			r %= Math.PI;
		else while(r < 0)
			r += Math.PI;

		return r;
	}

	public static float lerp(float a, float b, float weight)
	{
		return (a + (b - a) * weight);
	}

	public static boolean is_power_of_two(int i)
	{
		if ((i & (i - 1)) == 0)
			return true;

		return false;
	}

	public static float[][] white_noise(int width, int height, int seed)
	{
		java.util.Random random = new java.util.Random(seed);

		float[][] noise = new float[width][height];

		for (int x  = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				noise[x][y] = random.nextFloat() % 1;

		return noise;
	}

	public static float[][] smooth_noise(int width, int height, float[][] noise, int octave)
	{
		float[][] smooth_noise = new float[width][height];

		int sample_period = 1 << octave;
		float sample_frequency = 1.0f / sample_period;

		for (int x  = 0; x <  width; x++)
		{
			int sample_x1 = x / sample_period * sample_period;
			int sample_x2 = (sample_x1 + sample_period) % width;

			float horizontal_blend = (x - sample_x1) * sample_frequency;

			for (int y = 0; y < height; y++)
			{
				int sample_y1 = y / sample_period * sample_period;
				int sample_y2 = (sample_y1 + sample_period) % height;

				float vertical_blend = (y - sample_y1) * sample_frequency;

				float top = lerp(noise[sample_x1][sample_y1], noise[sample_x2][sample_y1], horizontal_blend);
				float bottom = lerp(noise[sample_x1][sample_y2], noise[sample_x2][sample_y2], horizontal_blend);

				smooth_noise[x][y] = lerp(top, bottom, vertical_blend);
			}
		}

		return smooth_noise;
	}

	public static float[][] perlin_noise(int width, int height, float[][] noise, int octaves, float persistance)
	{
		float[][][] smooth_noise = new float[octaves][][];

		for (int i = 0; i < octaves; i++)
			smooth_noise[i] = smooth_noise(width, height, noise, i);

		float[][] perlin_noise = new float[width][height];

		float amplitude = 1.0f;
		float total_amplitude = 0.0f;

		for (int i = octaves - 1; i >= 0; i--)
		{
			amplitude *= persistance;
			total_amplitude += amplitude;

			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++)
					perlin_noise[x][y] += smooth_noise[i][x][y] * amplitude;
		}

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				perlin_noise[x][y] /= total_amplitude;

		return perlin_noise;
	}

	/*
	public static float[] vector_to_screen(float[] m, float[] v)
	{
		float w = m[3] * v[0] + m[7] * v[1] + m[11] * v[2] + m[15];

		float[] position_2d = new float[2];
		position_2d[0] = ((m[0] * v[0] + m[4] * v[1] + m[8] * v[2] + m[12]) / w + 1) / 2;
		position_2d[1] = ((m[1] * v[0] + m[5] * v[1] + m[9] * v[2] + m[13]) / w + 1) / 2;

		return position_2d;
	}
	 */

	/*
	public static float[] normalize(float[] v1, float[] v2, float[] v3)
	{
		float[] u = {v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};
		float[] v = {v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]};

		float[] normal = {(u[1] * v[2]) - (u[2] * v[1]), (u[2] * v[0]) - (u[0] * v[2]), (u[0] * v[1]) - (u[1] * v[0])};
		float magnitude = (float)Math.sqrt((normal[0] * normal[0]) + (normal[1] * normal[1]) + (normal[2] * normal[2]));

		normal[0] /= magnitude;
		normal[1] /= magnitude;
		normal[2] /= magnitude;

		return normal;
	}
	*/

	/*
	public static void quad_normals(float[] vertices)
	{
		int s2 = stride_size;
		int s3 = stride_size * 2;

		float[] v1 = {vertices[s2 + offset_size[3]], vertices[s2 + offset_size[3] + 1], vertices[s2 + offset_size[3] + 2]};
		float[] v2 = {vertices[s3 + offset_size[3]], vertices[s3 + offset_size[3] + 1], vertices[s3 + offset_size[3] + 2]};
		float[] v3 = {vertices[	    offset_size[3]], vertices[     offset_size[3] + 1], vertices[     offset_size[3] + 2]};

		float[] normal = normalize(v1, v2, v3);

		for (int i = 0; i < 4; i++)
		{
			int s = stride_size * i;

			vertices[s + offset_size[3]]     = normal[0];
			vertices[s + offset_size[3] + 1] = normal[1];
			vertices[s + offset_size[3] + 2] = normal[2];
		}
	}
	 */

	/*
	public static void triangle_translate(float[] vertices, float x, float y, float z)
	{
		for (int i = 0; i < vertices.length / stride_size; i++)
		{
			int s = stride_size * i;

			vertices[s] += x;
			vertices[s + 1] += y;
			vertices[s + 2] += z;
		}
	}
	 */

	public static void triangle_rotate_x(float[] vertices, float rot)
	{
		float sine = (float)Math.sin(rot);
		float cosine = (float)Math.cos(rot);

		for (int i = 0; i < vertices.length; i += stride_size)
		{
			float y = vertices[i + 1] * cosine - vertices[i + 2] * sine;
			float z = vertices[i + 1] * sine   + vertices[i + 2] * cosine;

			vertices[i + 1] = y;
			vertices[i + 2] = z;
		}
	}

	public static void triangle_rotate_y(float[] vertices, float rot)
	{
		float sine = (float)Math.sin(rot);
		float cosine = (float)Math.cos(rot);

		for (int i = 0; i < vertices.length; i += stride_size)
		{
			float x = vertices[i]     * cosine + vertices[i + 2] * sine;
			float z = vertices[i + 2] * cosine - vertices[i]     * sine;

			vertices[i] = x;
			vertices[i + 2] = z;
		}
	}

	public static void triangle_rotate_z(float[] vertices, float rot)
	{
		float sine = (float)Math.sin(rot);
		float cosine = (float)Math.cos(rot);

		for (int i = 0; i < vertices.length; i += stride_size)
		{
			float x = vertices[i] * cosine - vertices[i + 1] * sine;
			float y = vertices[i] * sine   + vertices[i + 1] * cosine;

			vertices[i] = x;
			vertices[i + 1] = y;
		}
	}

	public static void triangle_color(float[] vertices, float r, float g, float b, float a)
	{
		for (int i = 0; i < vertices.length; i += stride_size)
		{
			vertices[i + offset_size[1]]     = r;
			vertices[i + offset_size[1] + 1] = g;
			vertices[i + offset_size[1] + 2] = b;
			vertices[i + offset_size[1] + 3] = a;
		}
	}

	public static void add_text(GeometryBuffer gb, String text, float x, float y, float s, float r, float g, float b)
	{
		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == ' ')
				continue;

			int charLocation = font_chars.indexOf(text.charAt(i));
			int ty = (int)Math.floor(charLocation / font_cell);
			int tx = (int)Math.floor(charLocation % font_cell);

			float top = font_pixel_size_y * ty;
			float bottom = font_pixel_size_y * (ty + 1);

			float left = font_pixel_size_x * tx;
			float right = font_pixel_size_x * (tx + 1);

			int pos_x = (int)(x + i * font_size_x * s);

			temporary_quad[0] = pos_x;
			temporary_quad[1] = y + font_size_y * s;
			temporary_quad[2] = 0.0f;
			temporary_quad[3] = r;
			temporary_quad[4] = g;
			temporary_quad[5] = b;
			temporary_quad[6] = 1.0f;
			temporary_quad[7] = left;
			temporary_quad[8] = top;
			temporary_quad[9] = 0.0f;
			temporary_quad[10] = 0.0f;
			temporary_quad[11] = 1.0f;

			temporary_quad[12] = pos_x;
			temporary_quad[13] = y;
			temporary_quad[14] = 0.0f;
			temporary_quad[15] = r;
			temporary_quad[16] = g;
			temporary_quad[17] = b;
			temporary_quad[18] = 1.0f;
			temporary_quad[19] = left;
			temporary_quad[20] = bottom;
			temporary_quad[21] = 0.0f;
			temporary_quad[22] = 0.0f;
			temporary_quad[23] = 1.0f;

			temporary_quad[24] = pos_x + font_size_x * s;
			temporary_quad[25] = y;
			temporary_quad[26] = 0.0f;
			temporary_quad[27] = r;
			temporary_quad[28] = g;
			temporary_quad[29] = b;
			temporary_quad[30] = 1.0f;
			temporary_quad[31] = right;
			temporary_quad[32] = bottom;
			temporary_quad[33] = 0.0f;
			temporary_quad[34] = 0.0f;
			temporary_quad[35] = 1.0f;

			temporary_quad[36] = pos_x + font_size_x * s;
			temporary_quad[37] = y + font_size_y * s;
			temporary_quad[38] = 0.0f;
			temporary_quad[39] = r;
			temporary_quad[40] = g;
			temporary_quad[41] = b;
			temporary_quad[42] = 1.0f;
			temporary_quad[43] = right;
			temporary_quad[44] = top;
			temporary_quad[45] = 0.0f;
			temporary_quad[46] = 0.0f;
			temporary_quad[47] = 1.0f;

			gb.add(temporary_quad, 4);
		}
	}

	public static void add_complex_text(GeometryBuffer gb, String text, float x, float y, float s, float r, float g, float b, float a)
	{
		int x_shift = 0;
		float y_shift = 0.0f;

		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == ' ')
				continue;

			if (text.charAt(i) == '/')
			{
				x_shift = i + 1;
				y_shift -= font_size_y * s;

				continue;
			}

			int charLocation = font_chars.indexOf(text.charAt(i));
			int ty = (int)Math.floor(charLocation / font_cell);
			int tx = (int)Math.floor(charLocation % font_cell);

			float top = font_pixel_size_y * ty;
			float bottom = font_pixel_size_y * (ty + 1);

			float left = font_pixel_size_x * tx;
			float right = font_pixel_size_x * (tx + 1);

			int pos_x = (int)(x + (i - x_shift) * font_size_x * s);

			temporary_quad[0] = pos_x;
			temporary_quad[1] = y + y_shift + font_size_y * s;
			temporary_quad[2] = 0.0f;
			temporary_quad[3] = r;
			temporary_quad[4] = g;
			temporary_quad[5] = b;
			temporary_quad[6] = a;
			temporary_quad[7] = left;
			temporary_quad[8] = top;
			temporary_quad[9] = 0.0f;
			temporary_quad[10] = 0.0f;
			temporary_quad[11] = 1.0f;

			temporary_quad[12] = pos_x;
			temporary_quad[13] = y + y_shift;
			temporary_quad[14] = 0.0f;
			temporary_quad[15] = r;
			temporary_quad[16] = g;
			temporary_quad[17] = b;
			temporary_quad[18] = a;
			temporary_quad[19] = left;
			temporary_quad[20] = bottom;
			temporary_quad[21] = 0.0f;
			temporary_quad[22] = 0.0f;
			temporary_quad[23] = 1.0f;

			temporary_quad[24] = pos_x + font_size_x * s;
			temporary_quad[25] = y + y_shift;
			temporary_quad[26] = 0.0f;
			temporary_quad[27] = r;
			temporary_quad[28] = g;
			temporary_quad[29] = b;
			temporary_quad[30] = a;
			temporary_quad[31] = right;
			temporary_quad[32] = bottom;
			temporary_quad[33] = 0.0f;
			temporary_quad[34] = 0.0f;
			temporary_quad[35] = 1.0f;

			temporary_quad[36] = pos_x + font_size_x * s;
			temporary_quad[37] = y + y_shift + font_size_y * s;
			temporary_quad[38] = 0.0f;
			temporary_quad[39] = r;
			temporary_quad[40] = g;
			temporary_quad[41] = b;
			temporary_quad[42] = a;
			temporary_quad[43] = right;
			temporary_quad[44] = top;
			temporary_quad[45] = 0.0f;
			temporary_quad[46] = 0.0f;
			temporary_quad[47] = 1.0f;

			gb.add(temporary_quad, 4);
		}
	}

	public static float[] create_rectangle(float x, float y, float w, float h, float s, float t1x, float t1y, float t2x, float t2y)
	{
		float top = s * t1y;
		float bottom = s * t2y;

		float left = s * t1x;
		float right = s * t2x;

		temporary_quad[0] = x;
		temporary_quad[1] = y + h;
		temporary_quad[2] = 0.0f;
		temporary_quad[3] = 1.0f;
		temporary_quad[4] = 1.0f;
		temporary_quad[5] = 1.0f;
		temporary_quad[6] = 1.0f;
		temporary_quad[7] = left;
		temporary_quad[8] = top;
		temporary_quad[9] = 0.0f;
		temporary_quad[10] = 0.0f;
		temporary_quad[11] = 1.0f;

		temporary_quad[12] = x;
		temporary_quad[13] = y;
		temporary_quad[14] = 0.0f;
		temporary_quad[15] = 1.0f;
		temporary_quad[16] = 1.0f;
		temporary_quad[17] = 1.0f;
		temporary_quad[18] = 1.0f;
		temporary_quad[19] = left;
		temporary_quad[20] = bottom;
		temporary_quad[21] = 0.0f;
		temporary_quad[22] = 0.0f;
		temporary_quad[23] = 1.0f;

		temporary_quad[24] = x + w;
		temporary_quad[25] = y;
		temporary_quad[26] = 0.0f;
		temporary_quad[27] = 1.0f;
		temporary_quad[28] = 1.0f;
		temporary_quad[29] = 1.0f;
		temporary_quad[30] = 1.0f;
		temporary_quad[31] = right;
		temporary_quad[32] = bottom;
		temporary_quad[33] = 0.0f;
		temporary_quad[34] = 0.0f;
		temporary_quad[35] = 1.0f;

		temporary_quad[36] = x + w;
		temporary_quad[37] = y + h;
		temporary_quad[38] = 0.0f;
		temporary_quad[39] = 1.0f;
		temporary_quad[40] = 1.0f;
		temporary_quad[41] = 1.0f;
		temporary_quad[42] = 1.0f;
		temporary_quad[43] = right;
		temporary_quad[44] = top;
		temporary_quad[45] = 0.0f;
		temporary_quad[46] = 0.0f;
		temporary_quad[47] = 1.0f;

		return temporary_quad;
	}

	public static float[] create_rectangle(float x, float y, float w, float h, float r, float g, float b, float a)
	{
		temporary_quad[0] = x;
		temporary_quad[1] = y + h;
		temporary_quad[2] = 0.0f;
		temporary_quad[3] = r;
		temporary_quad[4] = g;
		temporary_quad[5] = b;
		temporary_quad[6] = a;
		temporary_quad[7] = 0.0f;
		temporary_quad[8] = 0.0f;
		temporary_quad[9] = 0.0f;
		temporary_quad[10] = 0.0f;
		temporary_quad[11] = 1.0f;

		temporary_quad[12] = x;
		temporary_quad[13] = y;
		temporary_quad[14] = 0.0f;
		temporary_quad[15] = r;
		temporary_quad[16] = g;
		temporary_quad[17] = b;
		temporary_quad[18] = a;
		temporary_quad[19] = 0.0f;
		temporary_quad[20] = 0.0f;
		temporary_quad[21] = 0.0f;
		temporary_quad[22] = 0.0f;
		temporary_quad[23] = 1.0f;

		temporary_quad[24] = x + w;
		temporary_quad[25] = y;
		temporary_quad[26] = 0.0f;
		temporary_quad[27] = r;
		temporary_quad[28] = g;
		temporary_quad[29] = b;
		temporary_quad[30] = a;
		temporary_quad[31] = 0.0f;
		temporary_quad[32] = 0.0f;
		temporary_quad[33] = 0.0f;
		temporary_quad[34] = 0.0f;
		temporary_quad[35] = 1.0f;

		temporary_quad[36] = x + w;
		temporary_quad[37] = y + h;
		temporary_quad[38] = 0.0f;
		temporary_quad[39] = r;
		temporary_quad[40] = g;
		temporary_quad[41] = b;
		temporary_quad[42] = a;
		temporary_quad[43] = 0.0f;
		temporary_quad[44] = 0.0f;
		temporary_quad[45] = 0.0f;
		temporary_quad[46] = 0.0f;
		temporary_quad[47] = 1.0f;

		return temporary_quad;
	}

	/*
	public static float[] create_plane(float x1, float x2, float x3, float x4, float z1, float z2, float z3, float z4, float y1, float y2, float s, float t1x, float t1y, float t2x, float t2y)
	{
		float top = s * t1y;
		float bottom = s * t2y;

		float left = s * t1x;
		float right = s * t2x;

		float[] v1 = {x3, y2, z3};
		float[] v2 = {x4, y1, z4};
		float[] v3 = {x1, y1, z1};
		float[] normal = normalize(v1, v2, v3); 

		float[] a = 
			{
				x1, y2, z1, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , normal[0], normal[1], normal[2],
				x2, y1, z2, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, normal[0], normal[1], normal[2],
				x3, y1, z3, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, normal[0], normal[1], normal[2],
				x4, y2, z4, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , normal[0], normal[1], normal[2]
			};

		return a;
	}
	 */

	public static float[][] create_box(float w, float h, float l, float s, float f1x, float f1y, float f2x, float f2y, float b1x, float b1y, float b2x, float b2y, float l1x, float l1y, float l2x, float l2y, float r1x, float r1y, float r2x, float r2y, float u1x, float u1y, float u2x, float u2y, float d1x, float d1y, float d2x, float d2y)
	{
		float[][] a = 
			{
				//front
				{
					0, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f1x, s * f1y, 0.0f, 0.0f, 1.0f,
					0, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f1x, s * f2y, 0.0f, 0.0f, 1.0f,
					w, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f2x, s * f2y, 0.0f, 0.0f, 1.0f,
					w, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f2x, s * f1y, 0.0f, 0.0f, 1.0f
				},
				//back
				{
					w, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b1x, s * b1y, 0.0f, 0.0f, -1.0f,
					w, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b1x, s * b2y, 0.0f, 0.0f, -1.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b2x, s * b2y, 0.0f, 0.0f, -1.0f,
					0, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b2x, s * b1y, 0.0f, 0.0f, -1.0f
				},
				//left
				{
					0, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * l1x, s * l1y, -1.0f, 0.0f, 0.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * l1x, s * l2y, -1.0f, 0.0f, 0.0f,
					0, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * l2x, s * l2y, -1.0f, 0.0f, 0.0f,
					0, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * l2x, s * l1y, -1.0f, 0.0f, 0.0f
				},
				//right
				{
					w, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * r1x, s * r1y, 1.0f, 0.0f, 0.0f,
					w, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * r1x, s * r2y, 1.0f, 0.0f, 0.0f,
					w, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * r2x, s * r2y, 1.0f, 0.0f, 0.0f,
					w, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * r2x, s * r1y, 1.0f, 0.0f, 0.0f
				},
				//top
				{
					w, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * u1x, s * u1y, 0.0f, 1.0f, 0.0f,
					w, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * u1x, s * u2y, 0.0f, 1.0f, 0.0f,
					0, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * u2x, s * u2y, 0.0f, 1.0f, 0.0f,
					0, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * u2x, s * u1y, 0.0f, 1.0f, 0.0f
				},
				//bottom
				{
					w, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * d1x, s * d1y, 0.0f, -1.0f, 0.0f,
					w, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * d1x, s * d2y, 0.0f, -1.0f, 0.0f,
					0, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * d2x, s * d2y, 0.0f, -1.0f, 0.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * d2x, s * d1y, 0.0f, -1.0f, 0.0f
				}
			};

		return a;
	}

	/*public static float[][] create_box_inverted(float w, float h, float l, float s, float f1x, float f1y, float f2x, float f2y, float b1x, float b1y, float b2x, float b2y, float l1x, float l1y, float l2x, float l2y, float r1x, float r1y, float r2x, float r2y, float u1x, float u1y, float u2x, float u2y, float d1x, float d1y, float d2x, float d2y)
	{
		float[][] a = 
			{
				//front
				{
					0, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f1x, s * f2y, 0.0f, 0.0f, 1.0f,
					0, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f1x, s * f1y, 0.0f, 0.0f, 1.0f,
					w, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f2x, s * f1y, 0.0f, 0.0f, 1.0f,
					w, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * f2x, s * f2y, 0.0f, 0.0f, 1.0f
				},
				//back
				{
					w, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b1x, s * b2y, 0.0f, 0.0f, -1.0f,
					w, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b1x, s * b1y, 0.0f, 0.0f, -1.0f,
					0, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b2x, s * b1y, 0.0f, 0.0f, -1.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * b2x, s * b2y, 0.0f, 0.0f, -1.0f
				},
				//left
				{
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * l1x, s * l2y, -1.0f, 0.0f, 0.0f,
					0, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * l1x, s * l1y, -1.0f, 0.0f, 0.0f,
					0, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * l2x, s * l1y, -1.0f, 0.0f, 0.0f,
					0, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * l2x, s * l2y, -1.0f, 0.0f, 0.0f
				},
				//right
				{
					w, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * r1x, s * r2y, 1.0f, 0.0f, 0.0f,
					w, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * r1x, s * r1y, 1.0f, 0.0f, 0.0f,
					w, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * r2x, s * r1y, 1.0f, 0.0f, 0.0f,
					w, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * r2x, s * r2y, 1.0f, 0.0f, 0.0f
				},
				//top
				{
					w, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * u1x, s * u2y, 0.0f, 1.0f, 0.0f,
					w, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * u1x, s * u1y, 0.0f, 1.0f, 0.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * u2x, s * u1y, 0.0f, 1.0f, 0.0f,
					0, 0, l, 1.0f, 1.0f, 1.0f, 1.0f, s * u2x, s * u2y, 0.0f, 1.0f, 0.0f
				},
				//bottom
				{
					w, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * d1x, s * d2y, 0.0f, -1.0f, 0.0f,
					w, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * d1x, s * d1y, 0.0f, -1.0f, 0.0f,
					0, h, l, 1.0f, 1.0f, 1.0f, 1.0f, s * d2x, s * d1y, 0.0f, -1.0f, 0.0f,
					0, h, 0, 1.0f, 1.0f, 1.0f, 1.0f, s * d2x, s * d2y, 0.0f, -1.0f, 0.0f
				}
			};

		return a;
	}*/

	public static float[][] create_cube(float d, float s, float t1x, float t1y, float t2x, float t2y)
	{
		float top = s * t1y;
		float bottom = s * t2y;

		float left = s * t1x;
		float right = s * t2x;

		float[][] a = 
			{
				//front
				{
					0, d, d, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , 0.0f, 0.0f, 1.0f,
					0, 0, d, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, 0.0f, 0.0f, 1.0f,
					d, 0, d, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, 0.0f, 0.0f, 1.0f,
					d, d, d, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , 0.0f, 0.0f, 1.0f
				},
				//back
				{
					d, d, 0, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , 0.0f, 0.0f, -1.0f,
					d, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, 0.0f, 0.0f, -1.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, 0.0f, 0.0f, -1.0f,
					0, d, 0, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , 0.0f, 0.0f, -1.0f
				},
				//left
				{
					0, d, 0, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , -1.0f, 0.0f, 0.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, -1.0f, 0.0f, 0.0f,
					0, 0, d, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, -1.0f, 0.0f, 0.0f,
					0, d, d, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , -1.0f, 0.0f, 0.0f
				},
				//right
				{
					d, d, d, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , 1.0f, 0.0f, 0.0f,
					d, 0, d, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, 1.0f, 0.0f, 0.0f,
					d, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, 1.0f, 0.0f, 0.0f,
					d, d, 0, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , 1.0f, 0.0f, 0.0f
				},
				//top
				{
					d, d, d, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , 0.0f, 1.0f, 0.0f,
					d, d, 0, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, 0.0f, 1.0f, 0.0f,
					0, d, 0, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, 0.0f, 1.0f, 0.0f,
					0, d, d, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , 0.0f, 1.0f, 0.0f
				},
				//bottom
				{
					d, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , 0.0f, -1.0f, 0.0f,
					d, 0, d, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, 0.0f, -1.0f, 0.0f,
					0, 0, d, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, 0.0f, -1.0f, 0.0f,
					0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , 0.0f, -1.0f, 0.0f
				}
			};

		return a;
	}

	public static float[] create_sprite(float x, float y, float z, float rot, float w, float h, float s, float t1x, float t1y, float t2x, float t2y)
	{
		float top = s * t1y;
		float bottom = s * t2y;

		float left = s * t1x;
		float right = s * t2x;

		float sine = (float)(w * Math.sin(rot));
		float cosine = (float)(w * Math.cos(rot));

		float normal_x = h * sine * 2;
		float normal_z = h * cosine * 2;

		float magnitude = (float)Math.sqrt(normal_x * normal_x + normal_z * normal_z);

		normal_x /= magnitude;
		normal_z /= magnitude;

		temporary_quad[0] = x - cosine;
		temporary_quad[1] = y + h;
		temporary_quad[2] = z + sine;
		temporary_quad[3] = 1.0f;
		temporary_quad[4] = 1.0f;
		temporary_quad[5] = 1.0f;
		temporary_quad[6] = 1.0f;
		temporary_quad[7] = left;
		temporary_quad[8] = top;
		temporary_quad[9] = normal_x;
		temporary_quad[10] = 0.0f;
		temporary_quad[11] = normal_z;

		temporary_quad[12] = x - cosine;
		temporary_quad[13] = y;
		temporary_quad[14] = z + sine;
		temporary_quad[15] = 1.0f;
		temporary_quad[16] = 1.0f;
		temporary_quad[17] = 1.0f;
		temporary_quad[18] = 1.0f;
		temporary_quad[19] = left;
		temporary_quad[20] = bottom;
		temporary_quad[21] = normal_x;
		temporary_quad[22] = 0.0f;
		temporary_quad[23] = normal_z;

		temporary_quad[24] = x + cosine;
		temporary_quad[25] = y;
		temporary_quad[26] = z - sine;
		temporary_quad[27] = 1.0f;
		temporary_quad[28] = 1.0f;
		temporary_quad[29] = 1.0f;
		temporary_quad[30] = 1.0f;
		temporary_quad[31] = right;
		temporary_quad[32] = bottom;
		temporary_quad[33] = normal_x;
		temporary_quad[34] = 0.0f;
		temporary_quad[35] = normal_z;

		temporary_quad[36] = x + cosine;
		temporary_quad[37] = y + h;
		temporary_quad[38] = z - sine;
		temporary_quad[39] = 1.0f;
		temporary_quad[40] = 1.0f;
		temporary_quad[41] = 1.0f;
		temporary_quad[42] = 1.0f;
		temporary_quad[43] = right;
		temporary_quad[44] = top;
		temporary_quad[45] = normal_x;
		temporary_quad[46] = 0.0f;
		temporary_quad[47] = normal_z;

		return temporary_quad;
	}

	public static float[] create_sprite_alpha(float x, float y, float z, float rot, float w, float h, float s, float t1x, float t1y, float t2x, float t2y, float shade, float alpha)
	{
		float top = s * t1y;
		float bottom = s * t2y;

		float left = s * t1x;
		float right = s * t2x;

		float sine = (float)(w * Math.sin(rot));
		float cosine = (float)(w * Math.cos(rot));

		float normal_x = h * sine * 2;
		float normal_z = h * cosine * 2;

		float magnitude = (float)Math.sqrt(normal_x * normal_x + normal_z * normal_z);

		normal_x /= magnitude;
		normal_z /= magnitude;

		temporary_quad[0] = x - cosine;
		temporary_quad[1] = y + h;
		temporary_quad[2] = z + sine;
		temporary_quad[3] = shade;
		temporary_quad[4] = shade;
		temporary_quad[5] = shade;
		temporary_quad[6] = alpha;
		temporary_quad[7] = left;
		temporary_quad[8] = top;
		temporary_quad[9] = normal_x;
		temporary_quad[10] = 0.0f;
		temporary_quad[11] = normal_z;

		temporary_quad[12] = x - cosine;
		temporary_quad[13] = y;
		temporary_quad[14] = z + sine;
		temporary_quad[15] = shade;
		temporary_quad[16] = shade;
		temporary_quad[17] = shade;
		temporary_quad[18] = alpha;
		temporary_quad[19] = left;
		temporary_quad[20] = bottom;
		temporary_quad[21] = normal_x;
		temporary_quad[22] = 0.0f;
		temporary_quad[23] = normal_z;

		temporary_quad[24] = x + cosine;
		temporary_quad[25] = y;
		temporary_quad[26] = z - sine;
		temporary_quad[27] = shade;
		temporary_quad[28] = shade;
		temporary_quad[29] = shade;
		temporary_quad[30] = alpha;
		temporary_quad[31] = right;
		temporary_quad[32] = bottom;
		temporary_quad[33] = normal_x;
		temporary_quad[34] = 0.0f;
		temporary_quad[35] = normal_z;

		temporary_quad[36] = x + cosine;
		temporary_quad[37] = y + h;
		temporary_quad[38] = z - sine;
		temporary_quad[39] = shade;
		temporary_quad[40] = shade;
		temporary_quad[41] = shade;
		temporary_quad[42] = alpha;
		temporary_quad[43] = right;
		temporary_quad[44] = top;
		temporary_quad[45] = normal_x;
		temporary_quad[46] = 0.0f;
		temporary_quad[47] = normal_z;

		return temporary_quad;
	}

	/*
	public static float[] create_sprite_aligned(float x, float y, float z, float w, float h, float s, float t1x, float t1y, float t2x, float t2y)
	{
		float top = s * t1y;
		float bottom = s * t2y;

		float left = s * t1x;
		float right = s * t2x;

		float[] a = 
			{
				x - w, y + h, z, 1.0f, 1.0f, 1.0f, 1.0f, left , top   , 0.0f, 0.0f, 1.0f,
				x - w, y    , z, 1.0f, 1.0f, 1.0f, 1.0f, left , bottom, 0.0f, 0.0f, 1.0f,
				x + w, y    , z, 1.0f, 1.0f, 1.0f, 1.0f, right, bottom, 0.0f, 0.0f, 1.0f,
				x + w, y + h, z, 1.0f, 1.0f, 1.0f, 1.0f, right, top   , 0.0f, 0.0f, 1.0f
			};

		return a;
	}
	 */
}
