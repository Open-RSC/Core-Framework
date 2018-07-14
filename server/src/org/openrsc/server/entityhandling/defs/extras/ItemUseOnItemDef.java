package org.openrsc.server.entityhandling.defs.extras;

public class ItemUseOnItemDef {
	private int item1;
	private int item1Amount;
	private int item2;
	private int item2Amount;
	private int result1;
	private int result1Amount;
	private int result2;
	private int result2Amount;
	private String message1;
	private String message2;
	
	public ItemUseOnItemDef(int item1, int item1Amount, int item2, int item2Amount, int result1, int result1Amount, int result2, int result2Amount, String message1, String message2) {
		this.item1 = item1;
		this.item1Amount = item1Amount;
		this.item2 = item2;
		this.item2Amount = item2Amount;
		this.result1 = result1;
		this.result1Amount = result1Amount;
		this.result2 = result2;
		this.result2Amount = result2Amount;
	}
	
	public String getMessage1() {
		return message1;
	}
	
	public String getMessage2() {
		return message2;
	}
	
	public int getItem1() {
		return item1;
	}
	
	public int getItem1Amount() {
		return item1Amount;
	}
	
	public int getItem2() {
		return item2;
	}
	
	public int getItem2Amount() {
		return item2Amount;
	}

	public int getResult2Amount() {
		return result2Amount;
	}

	public int getResult2() {
		return result2;
	}

	public int getResult1Amount() {
		return result1Amount;
	}

	public int getResult1() {
		return result1;
	}
}
