package se.fearlessgames.fear;

import se.fearlessgames.fear.math.Matrix4;

public class FearScene {
	private final FearNode root;

	public FearScene(FearNode root) {
		this.root = root;
	}

	public FearNode getRoot() {
		return root;
	}

	public void render(Renderer renderer) {
		renderSkybox(renderer);
		renderOpaqueObjects(renderer);
		renderTransparentObjects(renderer);
	}

	private void renderTransparentObjects(Renderer renderer) {
	}

	private void renderOpaqueObjects(Renderer renderer) {
		render(renderer, root, Matrix4.IDENTITY);
	}

	private void render(Renderer renderer, FearNode node, Matrix4 parentTransform) {
		if (!node.isVisible()) {
			return;
		}

		Transformation childTransformation = new Transformation(node.getPosition(), node.getRotation(), node.getScale());
		Matrix4 multiply = parentTransform.multiply(childTransformation.asMatrix());

		renderMesh(node.getMesh(), renderer, multiply);


		for (FearNode child : node.getChildNodes()) {
			render(renderer, child, multiply);
		}
	}

	private void renderMesh(FearMesh mesh, Renderer renderer, Matrix4 parentTransform) {
		if (mesh == null) {
			return;
		}
		renderer.render(mesh, parentTransform);
	}

	private void renderSkybox(Renderer output) {
	}

}
