package se.fearlessgames.fear.example;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.fear.*;
import se.fearlessgames.fear.gl.*;
import se.fearlessgames.fear.light.DirectionalLight;
import se.fearlessgames.fear.light.DirectionalLightRenderState;
import se.fearlessgames.fear.math.PerspectiveBuilder;
import se.fearlessgames.fear.math.Quaternion;
import se.fearlessgames.fear.math.Vector3;
import se.fearlessgames.fear.mesh.Mesh;
import se.fearlessgames.fear.mesh.MeshData;
import se.fearlessgames.fear.mesh.MeshRenderer;
import se.fearlessgames.fear.mesh.MeshType;
import se.fearlessgames.fear.shape.SphereFactory;
import se.fearlessgames.fear.texture.*;
import se.fearlessgames.fear.vbo.VaoBuilder;
import se.fearlessgames.fear.vbo.VertexArrayObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumSet;

/*
* Sets up the Display, the GL context, and runs the main game
loop.
*/
public class Main {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private boolean done = false; //game runs until done is set to true
	private PerspectiveBuilder perspectiveBuilder;
	private final FearGl fearGl;
	private final Scene scene;
	private final ExampleRenderer renderer;
	private final TextureLoader textureManager;
	private double rot;
	private Transformation camera = new Transformation(Vector3.ZERO, Quaternion.IDENTITY, Vector3.ONE);
	private ShaderProgram shaderProgram;

	public Main() throws IOException {
		fearGl = DebuggingFearLwjgl.create();
		textureManager = new FearlessTextureLoader(fearGl);

		init();
		log.info(org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VERSION));

		shaderProgram = createShaderProgram();
		renderer = new ExampleRenderer(new MeshRenderer(fearGl, perspectiveBuilder));
		scene = createScene();
		scene.getRoot().setPosition(new Vector3(0, 0, -4));


		long t1 = System.nanoTime();
		long t2;
		int c = 0;
		int x = 0, y = 0, z = 0;
		while (!done) {
			if (Display.isCloseRequested()) {
				done = true;
			}


			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				x++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				x--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				y--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				y++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				z--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
				z++;
			}
			camera = new Transformation(new Vector3(x, y, z), Quaternion.IDENTITY, Vector3.ONE);

			render();

			Display.update();
			t2 = System.nanoTime();
			if ((c++ & 127) == 0) {
				log.info("FPS: {}", 1000000000.0d / (t2 - t1));
			}
			t1 = t2;
		}

		Display.destroy();

	}

	private void render() {
		rot += 0.0005d;
		scene.getRoot().setRotation(Quaternion.fromEulerAngles(rot / 2, rot, 0));

		fearGl.glClear(EnumSet.of(ClearBit.GL_COLOR_BUFFER_BIT, ClearBit.GL_DEPTH_BUFFER_BIT));
		scene.render(renderer, camera);
	}

	private Scene createScene() throws IOException {
		MeshData meshData = new SphereFactory(100, 100, 1.5, SphereFactory.TextureMode.PROJECTED).create();

		VertexArrayObject vertexArrayObject = VaoBuilder.fromMeshData(fearGl, shaderProgram, meshData).build();

		Node root = new Node("root");

		String textureName = "src/main/resources/texture/earth.png";
		Texture texture = textureManager.load(textureName, TextureFileType.GUESS, new FileInputStream(textureName), TextureType.TEXTURE_2D, true);

		MeshType earthMeshType = new MeshType(shaderProgram, renderer.opaqueBucket, new DirectionalLightRenderState(new SunLight()), new SingleTextureRenderState(texture));
		Mesh earth = new Mesh(vertexArrayObject, earthMeshType);

		Node boxNode = new Node("Box", earth);
		//boxNode.setScale(new Vector3(1, 1.4, 0.2));
		root.addChild(boxNode);

		return new Scene(root);
	}


	private ShaderProgram createShaderProgram() {
		ShaderProgram shaderProgram = new ShaderProgram(fearGl);
		shaderProgram.loadAndCompile("src/main/resources/shaders/textured.vert", ShaderType.VERTEX_SHADER);
		shaderProgram.loadAndCompile("src/main/resources/shaders/textured.frag", ShaderType.FRAGMENT_SHADER);
		shaderProgram.attachToProgram(ShaderType.VERTEX_SHADER);
		shaderProgram.attachToProgram(ShaderType.FRAGMENT_SHADER);
		return shaderProgram;
	}

	private void init() {
		int w = 800;
		int h = 600;

		try {


			DisplayUtil.create(w, h, "Shader Setup");

		} catch (Exception e) {
			log.error("Error setting up display", e);
			System.exit(0);
		}

		fearGl.glViewport(0, 0, w, h);
		perspectiveBuilder = new PerspectiveBuilder(45.0f, ((float) w / (float) h), 0.1f, 200.0f);

		fearGl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		fearGl.glClearDepth(1.0f);
		fearGl.glEnable(Capability.GL_DEPTH_TEST);
		fearGl.glDepthFunc(DepthFunction.GL_LEQUAL);
	}

	public static void main(String[] args) throws IOException {
		new Main();
	}

	private class SunLight implements DirectionalLight {
		private final Vector3 location = new Vector3(20f, 20f, 0f);
		private final ColorRGBA diffuse = new ColorRGBA(0.8f, 0.8f, 0.8f, 0f);
		private final ColorRGBA ambient = new ColorRGBA(0.1f, 0.1f, 0.1f, 0f);
		private final ColorRGBA specular = new ColorRGBA(0.1f, 0.1f, 0.1f, 0f);

		@Override
		public Vector3 getLocation() {
			return location;
		}

		@Override
		public ColorRGBA getDiffuse() {
			return diffuse;
		}

		@Override
		public ColorRGBA getAmbient() {
			return ambient;
		}

		@Override
		public ColorRGBA getSpecular() {
			return specular;
		}
	}
}