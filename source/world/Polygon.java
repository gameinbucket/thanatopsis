package com.gameinbucket.world;

public class Polygon extends Shape
{
	public Vertex[] vertices;

	public Polygon(Vertex[] v)
	{
		vertices = v;
		find_center();
	}

	private void find_center()
	{
		float area = 0;
		double sum_x = 0;
		double sum_y = 0;

		Vertex v0;
		Vertex v1;

		for (int i = 0; i < vertices.length - 1; i++)
		{
			v0 = vertices[i];
			v1 = vertices[i + 1];

			area += v0.x * v1.y - v1.x * v0.y;
			sum_x += Math.floor((v0.x + v1.x) * (v0.x * v1.y - v1.x * v0.y));
			sum_y += Math.floor((v0.y + v1.y) * (v0.x * v1.y - v1.x * v0.y));
		}

		v0 = this.vertices[vertices.length - 1];
		v1 = this.vertices[0];

		area += v0.x * v1.y - v1.x * v0.y;
		sum_x += Math.floor((v0.x + v1.x) * (v0.x * v1.y - v1.x * v0.y));
		sum_y += Math.floor((v0.y + v1.y) * (v0.x * v1.y - v1.x * v0.y));

		area = area / 2;

		center_x = (float)Math.floor(sum_x / (6 * area));
		center_y = (float)Math.floor(sum_y / (6 * area));
	}

	public void translate(float x, float y)
	{
		for (int i = 0; i < vertices.length; i++)
		{
			vertices[i].x += x;
			vertices[i].y += y;
		}

		find_center();
	}
	
	public void translate(Vertex v)
	{
		for (int i = 0; i < vertices.length; i++)
		{
			vertices[i].x += v.x;
			vertices[i].y += v.y;
		}

		find_center();
	}

	public void translate_to(float x, float y)
	{
		for (int i = 0; i < vertices.length; i++)
		{
			vertices[i].x = vertices[i].x - center_x + x;
			vertices[i].y = vertices[i].y - center_y + y;
		}

		find_center();
	}

	public float[] find_min_max(Vertex axis)
	{
		float[] min_max = new float[2];

		min_max[0] = vertices[0].dot(axis);
		min_max[1] = min_max[0];

		for (int i = 1; i < vertices.length; i++)
		{
			float d = vertices[i].dot(axis);

	        if (d < min_max[0]) min_max[0] = d;
	        else if (d > min_max[1]) min_max[1] = d;
	    }

	    return min_max;
	}
	
	public Vertex collision(Circle circle)
	{
		Vertex mtd = null;
	    float mtd_length = Float.MAX_VALUE;
	    
	    float distance = Float.MAX_VALUE;
	    Vertex closest_vertex = vertices[0];
	    
	    for (int i = 1; i < vertices.length; i++)
	    {
	        float test_distance = (circle.center_x - vertices[i].x) * (circle.center_x - vertices[i].x) + (circle.center_y - vertices[i].y) * (circle.center_y - vertices[i].y);

	        if (test_distance < distance)
	        {
	            distance = test_distance;
	            closest_vertex = vertices[i];
	        }
	    }
	    
	    Vertex axis = new Vertex(closest_vertex.x - circle.center_x, closest_vertex.y - circle.center_y);
	    
	    float[] minmax0 = circle.find_min_max(axis);
	    float[] minmax1 = find_min_max(axis);

	    float d0 = minmax1[1] - minmax0[0];
	    float d1 = minmax1[0] - minmax0[1];

	    if (d0 < 0.0f || d1 > 0.0f)
	        return null;
	    
	    float overlap = (d0 < -d1) ? d0 : d1;
	    float axis_length_squared = axis.dot(axis);

	    Vertex sep = new Vertex(axis.x * overlap / axis_length_squared, axis.y * overlap / axis_length_squared);
	    float sep_length_squared = sep.dot(sep);

	    if (sep_length_squared < mtd_length)
	    {
	        mtd_length = sep_length_squared;
	        mtd = sep;
	    }
	    
	    for (int j = vertices.length - 1, i = 0; i < vertices.length; j = i, i++)
	    {
	        Vertex v0 = vertices[j];
	        Vertex v1 = vertices[i];

	        axis = new Vertex(v1.x - v0.x, v1.y - v0.y);
	        axis.perp();

	        minmax0 = circle.find_min_max(axis);
	        minmax1 = find_min_max(axis);

	        d0 = minmax1[1] - minmax0[0];
	        d1 = minmax1[0] - minmax0[1];

	        if (d0 < 0.0f || d1 > 0.0f)
	            return null;

	        overlap = (d0 < -d1) ? d0 : d1;
	        axis_length_squared = axis.dot(axis);

	        sep = new Vertex(axis.x * overlap / axis_length_squared, axis.y * overlap / axis_length_squared);
	        sep_length_squared = sep.dot(sep);

	        if (sep_length_squared < mtd_length)
	        {
	            mtd_length = sep_length_squared;
	            mtd = sep;
	        }
	    }
	    
	    return mtd;
	}

	public Vertex collision(Polygon poly)
	{
		Vertex mtd = null;
		float mtd_length = Float.MAX_VALUE;

		for (int j = vertices.length - 1, i = 0; i < vertices.length; j = i, i++)
		{
			Vertex v0 = vertices[j];
			Vertex v1 = vertices[i];

			Vertex axis = new Vertex(v1.x - v0.x, v1.y - v0.y);
			axis.perp();

			float[] minmax0 = find_min_max(axis);
			float[] minmax1 = poly.find_min_max(axis);

			float d0 = minmax1[1] - minmax0[0];
			float d1 = minmax1[0] - minmax0[1];

			if (d0 < 0.0f || d1 > 0.0f)
				return null;

			float overlap = (d0 < -d1) ? d0 : d1;
			float axis_length_squared = axis.dot(axis);

			Vertex sep = new Vertex(axis.x * overlap / axis_length_squared, axis.y * overlap / axis_length_squared);
			float sep_length_squared = sep.dot(sep);

			if (sep_length_squared < mtd_length)
			{
				mtd_length = sep_length_squared;
				mtd = sep;
			}
		}

		for (int j = poly.vertices.length - 1, i = 0; i < poly.vertices.length; j = i, i++)
		{
			Vertex v0 = poly.vertices[j];
			Vertex v1 = poly.vertices[i];

			Vertex axis = new Vertex(v1.x - v0.x, v1.y - v0.y);
			axis.perp();

			float[] minmax0 = find_min_max(axis);
			float[] minmax1 = poly.find_min_max(axis);

			float d0 = minmax1[1] - minmax0[0];
			float d1 = minmax1[0] - minmax0[1];

			if (d0 < 0.0f || d1 > 0.0f)
				return null;

			float overlap = (d0 < -d1) ? d0 : d1;
			float axis_length_squared = axis.dot(axis);

			Vertex sep = new Vertex(axis.x * overlap / axis_length_squared, axis.y * overlap / axis_length_squared);
			float sep_length_squared = sep.dot(sep);

			if (sep_length_squared < mtd_length)
			{
				mtd_length = sep_length_squared;
				mtd = sep;
			}
		}

		return mtd;
	}
}
