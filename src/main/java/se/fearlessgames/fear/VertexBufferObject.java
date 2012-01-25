package se.fearlessgames.fear;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBufferObject {
	private final int vertexBufferId;
	private final int indexBufferId;
	private final FloatBuffer vertices;
	private final IntBuffer indices;

	public VertexBufferObject(float[] vertices, int[] indices) {
		this(createFloatBuffer(vertices), createIntBuffer(indices));
	}

	private static IntBuffer createIntBuffer(int[] indices) {
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices);
		indexBuffer.flip();
		return indexBuffer;
	}

	private static FloatBuffer createFloatBuffer(float[] vertices) {
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length * 3);
		vertexBuffer.put(vertices);
		vertexBuffer.flip();
		return vertexBuffer;
	}

	public VertexBufferObject(FloatBuffer vertices, IntBuffer indices) {
		this.vertices = vertices;
		this.indices = indices;
		vertexBufferId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

		indexBufferId = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, GL11.GL_NONE);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL11.GL_NONE);
	}


	public void draw() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);

		GL11.glDrawElements(GL11.GL_QUADS, vertices.capacity(), GL11.GL_UNSIGNED_INT, 0);

		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
}
