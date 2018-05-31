package org.rscemulation.client.entityhandling.defs;

public class ItemDef extends EntityDef {
	public String command;
	public int basePrice;
	public int sprite;
	public boolean stackable;
	public boolean wieldable;
	public int pictureMask;
	public boolean quest;
	private long lastChange = System.currentTimeMillis();
    private int lastCol = 10030000;
    private boolean reverse = false;
    private boolean note = false;
    private int orgId = -1;
	private final int baseTokenPrice;

    
	public ItemDef(String name, String description, String command, int basePrice, int baseTokenPrice, int sprite, boolean stackable, boolean wieldable, int pictureMask, boolean quest, int id)
	{
		super(name, description, id);
		this.command = command;
		this.basePrice = basePrice;
		this.baseTokenPrice = baseTokenPrice;
		this.sprite = sprite;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.pictureMask = pictureMask;
		this.quest = quest;
		this.id = id;
	}
	
	public ItemDef(ItemDef item, int id, int orgId) {
		super(item.name+" Note", "", id);
		this.command = "";
		this.basePrice = item.basePrice;
		this.baseTokenPrice = item.baseTokenPrice;
		this.sprite = item.sprite;
		this.stackable = item.stackable;
		this.wieldable = false;
		this.pictureMask = item.pictureMask;
		this.quest = item.quest;
		this.id = id;
		this.note = true;
		this.orgId = orgId;
	}
	
	public final int getBaseTokenPrice()
	{
		return baseTokenPrice;
	}

	public String getCommand() {
		return command;
	}

	public int getSprite() {
		return sprite;
	}

	public int getBasePrice() {
		return basePrice;
	}
	
	public boolean isStackable() {
		return stackable || note;
	}
	
	public boolean isWieldable() {
		return wieldable;
	}
	
	public int getPictureMask() {
		if (id == 1335 || id == 1336)
			return (int)(Math.random() * 167772150);
		
		/*else if (id == 1352) {
    	    if (System.currentTimeMillis() - lastChange > 10) {
    	        lastChange = System.currentTimeMillis();
    	        if (!reverse)
    	        	lastCol += 250;
    	        else
    	        	lastCol -= 250;
    	        if (lastCol > 10035000 || lastCol < 10030000)
    	        	reverse = !reverse;
    	    }
	        return lastCol;
		}*/
		else
			return pictureMask;
	}

	public boolean isNote() {
		if (this.getName().endsWith(" Note"))
			return true;
		return note;
	}

	public void setNote(boolean note) {
		this.note = note;
	}
}
