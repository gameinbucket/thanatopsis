package com.gameinbucket.rendering;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GeometryBuffer 
{
	//native buffer allocations
	private ShortBuffer index_buffer;
	private FloatBuffer vertex_buffer;

	//arrays to hold primitive data
	private Vertices vertex_order;
	private Indices index_order;
	
	public GeometryBuffer(int vertex_limit, int index_limit)
	{
		vertex_limit *= VectorMath.stride_size;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(VectorMath.floatsize * vertex_limit);
		vbb.order(ByteOrder.nativeOrder());
		vertex_buffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(VectorMath.shortsize * index_limit);
		ibb.order(ByteOrder.nativeOrder());
		index_buffer = ibb.asShortBuffer();

		vertex_order = new Vertices(vertex_limit);
		index_order = new Indices(index_limit);
	}
	
	public void clear()
	{
		vertex_order.clear();
		index_order.clear();
	}
	
	public void add(float[] vertices, int indices)
	{
		vertex_order.add(vertices);
		index_order.add(indices);
	}
	
	public void update()
	{
		vertex_buffer.clear();
		vertex_buffer.put(vertex_order.vertices, 0, vertex_order.position);
		vertex_buffer.position(0);

		index_buffer.clear();
		index_buffer.put(index_order.indices, 0, index_order.position);
		index_buffer.position(0);
	}
	
	//index count
	public short ic()
	{
		return index_order.position;
	}
	
	//vertex buffer position
	public Buffer vbp(int p)
	{
		return vertex_buffer.position(p);
	}
	
	//index buffer
	public ShortBuffer ib()
	{
		return index_buffer;
	}
	
	//create a vertex and index buffer object
	public ArrayBuffer generate_array_buffer()
	{
		return new ArrayBuffer(vertex_buffer, index_buffer, vertex_order.position, index_order.position);
	}
}
