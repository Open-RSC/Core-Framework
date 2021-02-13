public final class HopTest extends Script {

	public HopTest(Extension ex) {
		super(ex);
	}

	@Override
	public void init(String params) {
	}

	@Override
	public int main() {
		autohop(false);
		stopScript();
		return 1000;
	}

	@Override
	public void paint() {
	}

	@Override
	public void onServerMessage(String str) {
	}
}