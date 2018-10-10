using System.Collections.Generic;
using System.Linq;

using Assets.RSC;
using Assets.RSC.Data;
using Assets.RSC.Models;
using Assets.RSC.Models.Formats.Ob3;

using RSC.Data;

using UnityEngine;
using System.Collections;

[ExecuteInEditMode]
public class RSCModelHandler : MonoBehaviour
{

	private sbyte[] ModelData;

	private List<Ob3Model> _rscModels = new List<Ob3Model>();

	private static List<RSCModel> Models { get; set; }

	private static bool rscConfigHasLoaded = false;

	private static bool rscModelsHasLoaded = false;

	// Use this for initialization
	public string ModelJagFile = "models";

	private bool isLoaded = false;

	public static List<RSCModel> GetModels()
	{
		return Models;
	}

	void Start()
	{
		return;

		LoadAll();
	}

	void LoadAll()
	{
		Models = new List<RSCModel>();

		Load();
	}

	void Load()
	{
		Debug.Log("LOADASYNC();");
		LoadConfig();
		Load(ModelJagFile);		
	}


	// Update is called once per frame
	void Update()
	{

	}

	private void LoadConfig()
	{
		Debug.Log("Loading config...");
		if (rscConfigHasLoaded) return;
		sbyte[] configData = null;
		Utils.UnpackData("config", ref configData);
		RSCData.load(configData);
		rscConfigHasLoaded = true;
		Debug.Log("Loading config completed.");
	}

	public void Load(string modelsFile)
	{
		// Debug.Log("Loading jag file... " + modelsFile);
		try
		{
			if (rscModelsHasLoaded) return;

			Debug.Log("Loading Models...");
			Utils.UnpackData(modelsFile, ref ModelData);

			// Debug.Log("ModelData: " + ModelData.Length);

			LoadModels();
			int loadCount = 0;
			foreach (var model in Models)
			{
				if (model.FailedToLoad)
				{
					loadCount++;
				}
			}
			Debug.Log("Loading Models completed.");
			if (loadCount > 0)
				Debug.Log(loadCount + " models failed to load due to triangle mismatch.");

			rscModelsHasLoaded = true;
		}
		catch (System.Exception exc)
		{
			Debug.Log("ERROR: " + exc);
		}
	}

	private void LoadModels()
	{
		Models.Clear();

		RSCData.getModelNameIndex("torcha2");
		RSCData.getModelNameIndex("torcha3");
		RSCData.getModelNameIndex("torcha4");
		RSCData.getModelNameIndex("skulltorcha2");
		RSCData.getModelNameIndex("skulltorcha3");
		RSCData.getModelNameIndex("skulltorcha4");
		RSCData.getModelNameIndex("firea2");
		RSCData.getModelNameIndex("firea3");
		RSCData.getModelNameIndex("fireplacea2");
		RSCData.getModelNameIndex("fireplacea3");
		RSCData.getModelNameIndex("firespell2");
		RSCData.getModelNameIndex("firespell3");
		RSCData.getModelNameIndex("lightning2");
		RSCData.getModelNameIndex("lightning3");
		RSCData.getModelNameIndex("clawspell2");
		RSCData.getModelNameIndex("clawspell3");
		RSCData.getModelNameIndex("clawspell4");
		RSCData.getModelNameIndex("clawspell5");
		RSCData.getModelNameIndex("spellcharge2");
		RSCData.getModelNameIndex("spellcharge3");
		if (ModelData == null)
		{
			return;
		}
		for (int i1 = 0; i1 < RSCData.modelCount; i1++)
		{
			try
			{
				long j1 = DataOperations.getObjectOffset(RSCData.modelName[i1] + ".ob3", ModelData);
				if (j1 != 0)
					_rscModels.Add(new Ob3Model(RSCData.modelName[i1], ModelData, (int)j1, true));
				else
					_rscModels.Add(new Ob3Model(RSCData.modelName[i1], 1, 1));
				if (RSCData.modelName[i1].Equals("giantcrystal"))
				{
					var lastModel = _rscModels.LastOrDefault();
					if (lastModel != null)
					{
						lastModel.IsGiantCrystal = true;
					}
				}
			}
			catch { }
		}
		if (Models == null) Models = new List<RSCModel>();
		foreach (var rscModel in _rscModels)
		{
			Models.Add(new RSCModel(rscModel));
		}
	}
}
