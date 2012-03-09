package se.fearlessgames.fear.collada;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.fear.collada.data.AssetData;
import se.fearlessgames.fear.collada.data.Node;
import se.fearlessgames.fear.math.Vector3;
import se.fearlessgames.fear.mesh.IndexMode;
import se.fearlessgames.fear.mesh.MeshData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ColladaImporterTest {
	ColladaImporter colladaImporter;

	@Before
	public void setUp() throws Exception {
		colladaImporter = new ColladaImporter();
	}

	@Test
	public void testLoadAssetData() {
		ColladaStorage colladaStorage = colladaImporter.load(getInputData("fearless-render-collada/src/test/resources/boll.dae"));
		assertNotNull("Collada storage was null", colladaStorage);
		AssetData assetdata = colladaStorage.getAssetdata();

		assertNotNull("Asset Data was null", assetdata);
		assertEquals("spoo", assetdata.getAuthor());
		assertEquals("this is a test structure", assetdata.getComments());
		assertEquals("FBX COLLADA exporter", assetdata.getAuthoringTool());
		assertEquals("1.0", assetdata.getRevision());
		assertEquals("Test Ball", assetdata.getTitle());
		assertEquals("this is a test", assetdata.getSubject());
		assertEquals("test", assetdata.getKeywords());
		assertEquals(1.000f, assetdata.getUnitMeter(), 0.002);
		assertEquals(Vector3.UNIT_Y, assetdata.getUpAxis());
	}

	@Test
	public void testLoadVertexData() {
		ColladaStorage colladaStorage = colladaImporter.load(getInputData("fearless-render-collada/src/test/resources/boll.dae"));
		assertNotNull("Collada storage was null", colladaStorage);

		Node scene = colladaStorage.getScene();
		assertEquals("RootNode", scene.getName());
		assertEquals("Sphere01", scene.getChildren().get(0).getName());
		assertEquals("Sphere01Mesh", scene.getChildren().get(0).getChildren().get(0).getName());

		MeshData meshData = scene.getChildren().get(0).getChildren().get(0).getMeshes().get(0);
		assertEquals("Sphere01Mesh[_03___Default]", meshData.getName());
		assertEquals(2880, meshData.getVertexCount());
		assertEquals(8640, meshData.getVertexBuffer().limit());
		assertEquals(8640, meshData.getNormalBuffer().limit());
		assertEquals(2880, meshData.getIndices().limit());
		assertEquals(IndexMode.Triangles, meshData.getIndexMode());
	}

	private InputStream getInputData(String fileName) {
		try {
			return new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {

			fail("Failed to find file " + e.getMessage());
		}
		return null;

	}
}

