package com.gameinbucket.world;

public class Circle extends Shape
{
	public float radius;
	
	public Circle(float x, float y, float rad)
	{
		center_x = x;
		center_y = y;
		radius = rad;
	}
	
	public void translate(float x, float y)
	{
		center_x += x;
		center_y += y;
	}
	
	public void translate(Vertex v)
	{
		center_x += v.x;
		center_y += v.y;
	}

	public void translate_to(float x, float y)
	{
		center_x = x;
		center_y = y;
	}
	
	public float[] find_min_max(Vertex axis)
	{
		float[] min_max = new float[2];
		
		float center_dot = new Vertex(center_x, center_y).dot(axis);
		min_max[0] = center_dot - radius;
		min_max[1] = center_dot + radius;
		
		return min_max;
	}
	
	public Vertex collision(Circle circle)
	{
		float total_radius = radius + circle.radius;
	    float distance = (circle.center_x - center_x) * (circle.center_x - center_x) + (circle.center_y - center_y) * (circle.center_y - center_y);
	 
	    if (distance < total_radius * total_radius)
	    {
	    	double difference = total_radius - Math.sqrt(distance);
	        double angle = Math.atan2(center_y - circle.center_y, center_x - circle.center_x);
	        
	        return new Vertex((float)(Math.cos(angle) * difference), (float)(Math.sin(angle) * difference));
	    }
	    
	    return null;
	}
	
	public Vertex collision(Polygon poly)
	{
		Vertex mtd = null;
	    float mtd_length = Float.MAX_VALUE;
	    
	    float distance = Float.MAX_VALUE;
	    Vertex closest_vertex = poly.vertices[0];
	    
	    for (int i = 1; i < poly.vertices.length; i++)
	    {
	        float test_distance = (center_x - poly.vertices[i].x) * (center_x - poly.vertices[i].x) + (center_y - poly.vertices[i].y) * (center_y - poly.vertices[i].y);

	        if (test_distance < distance)
	        {
	            distance = test_distance;
	            closest_vertex = poly.vertices[i];
	        }
	    }
	    
	    Vertex axis = new Vertex(closest_vertex.x - center_x, closest_vertex.y - center_y);
	    
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
	    
	    for (int j = poly.vertices.length - 1, i = 0; i < poly.vertices.length; j = i, i++)
	    {
	        Vertex v0 = poly.vertices[j];
	        Vertex v1 = poly.vertices[i];

	        axis = new Vertex(v1.x - v0.x, v1.y - v0.y);
	        axis.perp();

	        minmax0 = find_min_max(axis);
	        minmax1 = poly.find_min_max(axis);

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
}
