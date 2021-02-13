import static java.lang.String.format;

public final class ListenTest extends Script {

	public ListenTest(Extension ex)
	{
		super(ex);
	}

	@Override
	public void init(String params)
	{
	}

	@Override
	public int main()
	{
		return 1000;
	}

	@Override
	public void paint()
	{
	}

	@Override
	public void onServerMessage(String str)
	{
		writeLine(format("Server message: %s", str));
	}

	@Override
	public void onTradeRequest(String name)
	{
		writeLine(format("Traded by: %s", name));
	}

	@Override
	public void onChatMessage(String msg, String name, boolean mod,
		boolean admin)
	{
		writeLine(format("Public chat: %s %s", name, msg));
	}

	@Override
	public void onPrivateMessage(String msg, String name, boolean mod,
		boolean admin)
	{
		writeLine(format("Private chat: %s %s", name, msg));
	}

	@Override
	public void onKeyPress(int keycode)
	{
		writeLine(format("Key pressed: %d", keycode));
	}
}