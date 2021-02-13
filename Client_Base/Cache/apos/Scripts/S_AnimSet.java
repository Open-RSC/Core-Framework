import java.util.Arrays;

public final class S_AnimSet extends Script {

	private Extension ex;
	private int anim;

	public S_AnimSet(Extension ex) {
		super(ex);
		this.ex = ex;
	}

	@Override
	public void init(String params) {
		anim = 0;
	}

	@Override
	public int main() {
		Arrays.fill(ex.wi.m, 0);
		ex.wi.m[0] = 133;
		//ex.wi.m[0] = ++anim;
		//System.out.println(anim);
		return 1000;
	}
}
