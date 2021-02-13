// Imports required by the settings creator.
import com.aposbot.JSONgui;

public final class JSON_Fighter extends Script {
	
	// THIS IS RELEVANT.
	// stores the configuration of the script.
	// defaults are specified here. please make them sensible.
	// Settings cannot be "final".
	public static final class Config {
		public int[] npcs = { 1, 2 };
		public int food_id = 546;
		public boolean sleep = true;
	}
	
	// THIS IS RELEVANT. This has to be here. It is used to access your settings.
	private final Config config = new Config();
	
	// THIS IS RELEVANT. Displayed when the user clicks the "Help" button.
	private static final String[] help_contents = {
		"- Example script (JSON Fighter) by S -",
		"",
		"\"npcs\" - a list of NPC IDs to attack, separated with commas.",
		"\"food_id\" - the ID of the food you want to eat.",
		"\"sleep\" - set to \"true\" to use the sleeping bag, otherwise false.",
		"",
		"IDs can be found with the debugger, or inside the APOS \"ids\" folder."
	};
	
	public static void main(String[] argv) {
		new JSON_Fighter(null).init(null);
	}

	public JSON_Fighter(Extension ex) {
		super(ex);
	}
	
	@Override
	public void init(String params) {
		// THIS IS RELEVANT. Required line.
		new JSONgui(getClass().getSimpleName(),
				config, help_contents, new Runnable() {
		public void run() {
			// THIS IS RELEVANT. Will be executed when the user
			// presses "ok". You can do post-processing here
			// if need be.
			System.out.println("Press \"start script\" to begin.");
		}
	}).showFrame();
	}

	@Override
	public int main() {
		// THIS IS RELEVANT. The "sleep", "food_id", "npcs", settings are accessed through "config".
		if (config.sleep && getFatigue() > 95) {
			useSleepingBag();
			return random(1000, 2000);
		}
		if (getHpPercent() <= 50) {
			int food = getInventoryIndex(config.food_id);
			if (food != -1) {
				useItem(food);
			}
			return random(600, 800);
		}
		int[] npc = getNpcById(config.npcs);
		if (npc[0] != -1) {
			if (distanceTo(npc[1], npc[2]) > 5) {
				walkTo(npc[1], npc[2]);
			} else {
				attackNpc(npc[0]);
			}
		}
		return random(600, 1000);
	}

	@Override
	public void paint() {
	}
	
	@Override
	public void onServerMessage(String str) {
	}
}
