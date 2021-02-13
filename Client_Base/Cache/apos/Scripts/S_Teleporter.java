import java.util.Locale;

public final class S_Teleporter extends Script {

    private int spell_id;

    public S_Teleporter(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        try {
            if ("".equals(params)) throw new Exception();
            String lower = params.trim().toLowerCase(Locale.ENGLISH);
            if (lower.startsWith("var")) {
                spell_id = 12;
            } else if (lower.startsWith("lum")) {
                spell_id = 15;
            } else if (lower.startsWith("fal")) {
                spell_id = 18;
            } else if (lower.startsWith("cam")) {
                spell_id = 22;
            } else if (lower.startsWith("ard")) {
                spell_id = 26;
            } else if (lower.startsWith("wat")) {
                spell_id = 31;
            } else {
                spell_id = Integer.parseInt(params);
            }
        } catch (Throwable t) {
            System.out.println(
                    "Error parsing parameters. " +
                    "Example: spellid OR placename");
            return;
        }
    }

    @Override
    public int main() {
        if (getFatigue() > 90) {
            useSleepingBag();
            return random(2000, 3000);
        }
        if (canCastSpell(spell_id)) {
            castOnSelf(spell_id);
            return random(800, 1200);
        }
        System.out.println("ERROR: Can't cast spell.");
        stopScript(); setAutoLogin(false);
        return 0;
    }
}
