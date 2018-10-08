using System.Linq;

using Assets.RSC.Models;

using UnityEngine;
using System.Collections;

/*[ExecuteInEditMode]*/
public class RSCModelRenderer : MonoBehaviour
{

	public string ModelName = "";
	public Material Material;

	private RSCModel model;
	private Mesh targetMesh;

	// Use this for initialization
	void Start()
	{
		return;

		var Models = RSCModelHandler.GetModels();

		//	this.GetComponent<GameObject>();
		if (string.IsNullOrEmpty(ModelName))
		{
			ModelName = "tree";
		}
		if (Material == null)
		{
			var shader = Shader.Find(" Vertex Colored");

			Material = new Material(shader);
		}
		if (Models != null && Models.Any())
		{
			model = Models.FirstOrDefault(m => m.ModelName.ToLower().Contains(ModelName.ToLower()));

			AddMeshCollider();
		}

	}

	void AddMeshCollider()
	{
		if (model != null)
		{
			targetMesh = model.GetMesh();

			var component = this.gameObject.AddComponent(typeof(MeshCollider));
			var mc = component as MeshCollider;
			if (mc != null)
			{
				mc.sharedMesh = targetMesh;
			}
		}
	}

	bool MakeSureWeHaveAModel()
	{
		if (model == null || targetMesh == null)
		{
			var Models = RSCModelHandler.GetModels();
			if (string.IsNullOrEmpty(ModelName)) return false;
			if (Models != null && Models.Count > 0)
			{
				model = Models.FirstOrDefault(m => m.ModelName.ToLower().Contains(ModelName.ToLower()));
				if (model != null)
				{
					targetMesh = model.GetMesh();

					AddMeshCollider();
					return true;
				}
			}
		}
		else if (model != null && targetMesh != null)
		{
			return true;
		}
		return false;
	}

	void OnDrawGizmos()
	{
		if (!MakeSureWeHaveAModel()) return;
		if (Material != null)
		{
			//Graphics.
			Material.SetPass(0);
			Graphics.DrawMesh(targetMesh, Matrix4x4.TRS(this.transform.position, Quaternion.identity, this.transform.localScale), Material, 0, null, 0);
			//Graphics.DrawMeshNow(mesh, Matrix4x4.TRS(Vector3.zero, Quaternion.identity, new Vector3(0.02f, 0.02f, 0.02f)));
		}
	}


	// Update is called once per frame
	void Update()
	{
		if (!MakeSureWeHaveAModel()) return;
		if (Material != null)
		{
			//Graphics.
			Material.SetPass(0);
			Graphics.DrawMesh(targetMesh, Matrix4x4.TRS(this.transform.position, Quaternion.identity, this.transform.localScale), Material, 0, null, 0);
			//Graphics.DrawMeshNow(mesh, Matrix4x4.TRS(Vector3.zero, Quaternion.identity, new Vector3(0.02f, 0.02f, 0.02f)));
		}
	}
}
