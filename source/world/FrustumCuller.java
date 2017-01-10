package com.gameinbucket.world;

public class FrustumCuller
{
	private float[][] frustum;

	public FrustumCuller()
	{
		frustum = new float[6][4];
	}

	private void normalize(float[] f)
	{
		float n = (float)Math.sqrt(f[0] * f[0] + f[1] * f[1] + f[2] * f[2]);
		f[0] /= n;
		f[1] /= n;
		f[2] /= n;
		f[3] /= n;
	}

	public boolean point_in_frustum(float x, float y, float z)
	{
		for (int p = 0; p < 6; p++)
		{
			if (frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3] <= 0)
				return false;
		}

		return true;
	}
	
	public boolean square_in_frustum(float x, float y, float size)
	{
		for (int p = 0; p < 6; p++)
		{
			if (frustum[p][0] * (x - size) + frustum[p][2] * (y - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][2] * (y - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x - size) + frustum[p][2] * (y + size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][2] * (y + size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x - size) + frustum[p][2] * (y - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][2] * (y - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x - size) + frustum[p][2] * (y + size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][2] * (y + size) + frustum[p][3] > 0)
				continue;

			return false;
		}

		return true;
	}

	public boolean sphere_in_frustum(float x, float y, float z, float radius)
	{
		for (int p = 0; p < 6; p++)
		{
			if (frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3] <= -radius)
				return false;
		}

		return true;
	}

	public float distance_sphere_in_frustum(float x, float y, float z, float radius)
	{
		float dist = 0;

		for (int p = 0; p < 6; p++)
		{
			dist = frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3];

			if (dist <= -radius)
				return 0;
		}

		return dist + radius;
	}

	public int partial_sphere_in_frustum(float x, float y, float z, float radius)
	{
		float dist = 0;
		int planes = 0;

		for (int p = 0; p < 6; p++)
		{
			dist = frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3];

			if (dist <= -radius)
				return 0;

			if (dist > radius)
				planes++;
		}

		//0 none, 1 partial, 2 complete
		return (planes == 6) ? 2 : 1;
	}

	public boolean cube_in_frustum(float x, float y, float z, float size)
	{
		for (int p = 0; p < 6; p++)
		{
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y - size) + frustum[p][2] * (z - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y - size) + frustum[p][2] * (z - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y + size) + frustum[p][2] * (z - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y + size) + frustum[p][2] * (z - size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y - size) + frustum[p][2] * (z + size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y - size) + frustum[p][2] * (z + size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x - size) + frustum[p][1] * (y + size) + frustum[p][2] * (z + size) + frustum[p][3] > 0)
				continue;
			if (frustum[p][0] * (x + size) + frustum[p][1] * (y + size) + frustum[p][2] * (z + size) + frustum[p][3] > 0)
				continue;

			return false;
		}

		return true;
	}

	public boolean polygon_in_frustum(float[] x, float[] y, float[] z)
	{
		int p;

		for (int f = 0; f < 6; f++)
		{
			for (p = 0; p < x.length; p++)
			{
				if (frustum[f][0] * x[p] + frustum[f][1] * y[p] + frustum[f][2] * z[p] + frustum[f][3] > 0)
					break;
			}

			if (p == x.length)
				return false;
		}

		return true;
	}

	public void update_frustum_planes(float[] mvp)
	{
		//right
		frustum[0][0] = mvp[3] - mvp[0]; 
		frustum[0][1] = mvp[7] - mvp[4];
		frustum[0][2] = mvp[11] - mvp[8];
		frustum[0][3] = mvp[15] - mvp[12];
		normalize(frustum[0]);

		//left
		frustum[1][0] = mvp[3] + mvp[0]; 
		frustum[1][1] = mvp[7] + mvp[4];
		frustum[1][2] = mvp[11] + mvp[8];
		frustum[1][3] = mvp[15] + mvp[12];
		normalize(frustum[1]);

		//bottom
		frustum[2][0] = mvp[3] + mvp[1];
		frustum[2][1] = mvp[7] + mvp[5];
		frustum[2][2] = mvp[11] + mvp[9];
		frustum[2][3] = mvp[15] + mvp[13];
		normalize(frustum[2]);

		//top
		frustum[3][0] = mvp[3] - mvp[1]; 
		frustum[3][1] = mvp[7] - mvp[5];
		frustum[3][2] = mvp[11] - mvp[9];
		frustum[3][3] = mvp[15] - mvp[13];
		normalize(frustum[3]);

		//far
		frustum[4][0] = mvp[3] - mvp[2]; 
		frustum[4][1] = mvp[7] - mvp[6];
		frustum[4][2] = mvp[11] - mvp[10];
		frustum[4][3] = mvp[15] - mvp[14];
		normalize(frustum[4]);

		//near
		frustum[5][0] = mvp[3] + mvp[2];
		frustum[5][1] = mvp[7] + mvp[6];
		frustum[5][2] = mvp[11] + mvp[10];
		frustum[5][3] = mvp[15] + mvp[14];
		normalize(frustum[5]);
	}
}