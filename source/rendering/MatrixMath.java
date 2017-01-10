package com.gameinbucket.rendering;

public class MatrixMath 
{
	private static float[] sub_m = new float[16];
	
	public static void load_identity(float[] m)
	{
		m[0] = 1.0f;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;
		
		m[4] = 0.0f;
		m[5] = 1.0f;
		m[6] = 0.0f;
	    m[7] = 0.0f;
		
		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = 1.0f;
		m[11] = 0.0f;
		
		m[12] = 0.0f;
		m[13] = 0.0f;
	    m[14] = 0.0f;
		m[15] = 1.0f;
	}
	
	public static void load_orthographic(float[] m, float left, float right, float bottom, float top, float near, float far)
	{
		m[0] = 2.0f / (right - left);
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;
		
		m[4] = 0.0f;
		m[5] = 2.0f / (top - bottom);
		m[6] = 0.0f;
		m[7] = 0.0f;

		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = -2.0f / (far - near);
		m[11] = 0.0f;

		m[12] = -((right + left) / (right - left));
		m[13] = -((top + bottom) / (top - bottom));
		m[14] = -((far + near) / (far - near));
		m[15] = 1.0f;
	}
	
	public static void load_perspective(float[] m, float fov, float near, float far, float ar)
	{
		float top = near * (float)Math.tan(fov * Math.PI / 360.0f);
		float bottom = -top;
		float left = bottom * ar;
		float right = top * ar;
	    
	    MatrixMath.load_frustrum(m, left, right, bottom, top, near, far);
	}
	
	public static void load_frustrum(float[] m, float left, float right, float bottom, float top, float near, float far)
	{
	    m[0] = (2.0f * near) / (right - left);
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;
		
		m[4] = 0.0f;
		m[5] = (2.0f * near) / (top - bottom);
		m[6] = 0.0f;
		m[7] = 0.0f;
		
		m[8] = (right + left) / (right - left);
		m[9] = (top + bottom) / (top - bottom);
		m[10] = -(far + near) / (far - near);
		m[11] = -1.0f;
		
		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = -(2.0f * far * near) / (far - near);
		m[15] = 0.0f;
	}

	public static void translate(float[] m, float x, float y, float z)
	{
		m[12] = x * m[0] + y * m[4] + z * m[8] + m[12];
		m[13] = x * m[1] + y * m[5] + z * m[9] + m[13];
		m[14] = x * m[2] + y * m[6] + z * m[10] + m[14];
		m[15] = x * m[3] + y * m[7] + z * m[11] + m[15];
	}

	public static void rotate_x(float[] m, float r)
	{
		sub_m[0] = 1.0f;
		sub_m[1] = 0.0f;
		sub_m[2] = 0.0f;
		sub_m[3] = 0.0f;

		sub_m[4] = 0.0f;
		sub_m[5] = (float)Math.cos(r);
		sub_m[6] = (float)Math.sin(r);
		sub_m[7] = 0.0f;

		sub_m[8] = 0.0f;
		sub_m[9] = -(float)Math.sin(r);
		sub_m[10] = (float)Math.cos(r);
		sub_m[11] = 0.0f;

		sub_m[12] = 0.0f;
		sub_m[13] = 0.0f;
		sub_m[14] = 0.0f;
		sub_m[15] = 1.0f;

		MatrixMath.multiply(m, sub_m);
	}

	public static void rotate_y(float[] m, float r)
	{
		sub_m[0] = (float)Math.cos(r);
		sub_m[1] = 0.0f;
		sub_m[2] = -(float)Math.sin(r);
		sub_m[3] = 0.0f;

		sub_m[4] = 0.0f;
		sub_m[5] = 1.0f;
		sub_m[6] = 0.0f;
		sub_m[7] = 0.0f;

		sub_m[8] = (float)Math.sin(r);
		sub_m[9] = 0.0f;
		sub_m[10] = (float)Math.cos(r);
		sub_m[11] = 0.0f;

		sub_m[12] = 0.0f;
		sub_m[13] = 0.0f;
		sub_m[14] = 0.0f;
		sub_m[15] = 1.0f;

		MatrixMath.multiply(m, sub_m);
	}

	public static void rotate_z(float[] m, float r)
	{
		sub_m[0] = (float)Math.cos(r);
		sub_m[1] = (float)Math.sin(r);
		sub_m[2] = 0.0f;
		sub_m[3] = 0.0f;

		sub_m[4] = -(float)Math.sin(r); 
		sub_m[5] = (float)Math.cos(r);
		sub_m[6] = 0.0f;
		sub_m[7] = 0.0f;

		sub_m[8] = 0.0f;
		sub_m[9] = 0.0f;
		sub_m[10] = 1.0f;
		sub_m[11] = 0.0f;

		sub_m[12] = 0.0f;
		sub_m[13] = 0.0f;
		sub_m[14] = 0.0f;
		sub_m[15] = 1.0f;

		MatrixMath.multiply(m, sub_m);
	}
	
	public static void multiply(float[] m, float[] mm)
	{
		float m0 = m[0];
		float m1 = m[1];
		float m2 = m[2];
		float m3 = m[3];
		float m4 = m[4];
		float m5 = m[5];
		float m6 = m[6];
		float m7 = m[7];
		float m8 = m[8];
		float m9 = m[9];
		float m10 = m[10];
		float m11 = m[11];
		float m12 = m[12];
		float m13 = m[13];
		float m14 = m[14];
		float m15 = m[15];
		
		m[0] = m0 * mm[0] + m4 * mm[1] + m8 * mm[2] + m12 * mm[3];
		m[1] = m1 * mm[0] + m5 * mm[1] + m9 * mm[2] + m13 * mm[3];
		m[2] = m2 * mm[0] + m6 * mm[1] + m10 * mm[2] + m14 * mm[3];
		m[3] = m3 * mm[0] + m7 * mm[1] + m11 * mm[2] + m15 * mm[3];

		m[4] = m0 * mm[4] + m4 * mm[5] + m8 * mm[6] + m12 * mm[7];
		m[5] = m1 * mm[4] + m5 * mm[5] + m9 * mm[6] + m13 * mm[7];
		m[6] = m2 * mm[4] + m6 * mm[5] + m10 * mm[6] + m14 * mm[7];
		m[7] = m3 * mm[4] + m7 * mm[5] + m11 * mm[6] + m15 * mm[7];

		m[8] = m0 * mm[8] + m4 * mm[9] + m8 * mm[10] + m12 * mm[11];
		m[9] = m1 * mm[8] + m5 * mm[9] + m9 * mm[10] + m13 * mm[11];
		m[10] = m2 * mm[8] + m6 * mm[9] + m10 * mm[10] + m14 * mm[11];
		m[11] = m3 * mm[8] + m7 * mm[9] + m11 * mm[10] + m15 * mm[11];

		m[12] = m0 * mm[12] + m4 * mm[13] + m8 * mm[14] + m12 * mm[15];
		m[13] = m1 * mm[12] + m5 * mm[13] + m9 * mm[14] + m13 * mm[15];
		m[14] = m2 * mm[12] + m6 * mm[13] + m10 * mm[14] + m14 * mm[15];
		m[15] = m3 * mm[12] + m7 * mm[13] + m11 * mm[14] + m15 * mm[15];
	}

	public static void multiply(float[] m, float[] m1, float[] m2)
	{
		m[0] = m1[0] * m2[0] + m1[4] * m2[1] + m1[8] * m2[2] + m1[12] * m2[3];
		m[1] = m1[1] * m2[0] + m1[5] * m2[1] + m1[9] * m2[2] + m1[13] * m2[3];
		m[2] = m1[2] * m2[0] + m1[6] * m2[1] + m1[10] * m2[2] + m1[14] * m2[3];
		m[3] = m1[3] * m2[0] + m1[7] * m2[1] + m1[11] * m2[2] + m1[15] * m2[3];

		m[4] = m1[0] * m2[4] + m1[4] * m2[5] + m1[8] * m2[6] + m1[12] * m2[7];
		m[5] = m1[1] * m2[4] + m1[5] * m2[5] + m1[9] * m2[6] + m1[13] * m2[7];
		m[6] = m1[2] * m2[4] + m1[6] * m2[5] + m1[10] * m2[6] + m1[14] * m2[7];
		m[7] = m1[3] * m2[4] + m1[7] * m2[5] + m1[11] * m2[6] + m1[15] * m2[7];

		m[8] = m1[0] * m2[8] + m1[4] * m2[9] + m1[8] * m2[10] + m1[12] * m2[11];
		m[9] = m1[1] * m2[8] + m1[5] * m2[9] + m1[9] * m2[10] + m1[13] * m2[11];
		m[10] = m1[2] * m2[8] + m1[6] * m2[9] + m1[10] * m2[10] + m1[14] * m2[11];
		m[11] = m1[3] * m2[8] + m1[7] * m2[9] + m1[11] * m2[10] + m1[15] * m2[11];

		m[12] = m1[0] * m2[12] + m1[4] * m2[13] + m1[8] * m2[14] + m1[12] * m2[15];
		m[13] = m1[1] * m2[12] + m1[5] * m2[13] + m1[9] * m2[14] + m1[13] * m2[15];
		m[14] = m1[2] * m2[12] + m1[6] * m2[13] + m1[10] * m2[14] + m1[14] * m2[15];
		m[15] = m1[3] * m2[12] + m1[7] * m2[13] + m1[11] * m2[14] + m1[15] * m2[15];
	}
}
