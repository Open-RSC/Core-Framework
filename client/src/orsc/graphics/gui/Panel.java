package orsc.graphics.gui;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.model.Sprite;
import orsc.MiscFunctions;
import orsc.enumerations.PanelControlType;
import orsc.graphics.two.Fonts;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;
import orsc.util.FastMath;
import orsc.util.GenUtil;

public final class Panel {
	public int[] controlListCurrentSize;
	public int[] controlScrollAmount;
	private int button1HeldTicks = 0;
	private int colorA;
	private int colorB;
	private int colorC;
	private int colorD;
	private int colorE;
	private int colorF;
	private int colorG;
	private int colorH;
	private int colorI;
	private int colorJ;
	private int colorK;
	private int colorL;
	private int[] controlArgInt;
	private Sprite[] controlArgSprite;
	private boolean[] controlClicked;
	private int[] controlClickedListIndex;
	private int controlCount = 0;
	private boolean[] controlFlag;
	private int[] controlHeight;
	private int[] controlSpaceHeight;
	private int[] controlSpaceTextHeight;
	private int[][] controlListEntryCrown;
	private String[][] controlListEntryString;
	private String[][] controlListEntryString2;
	private String[][] controlListEntryString3;
	private int[] controlMaxSize;
	private int[] controlSelectedListIndex;
	private String[] controlText;
	private PanelControlType[] controlType;
	private boolean[] controlUseAlternativeColour;
	private boolean[] controlVisible;
	private int[] controlWidth;
	private int[] controlX;
	private int[] controlY;
	private int currentMouseButtonDown = 0;
	private int currMouseX = 0;
	private int currMouseY = 0;
	private int focusControlIndex = -1;
	private GraphicsController graphics;
	private boolean[] isScrolling;
	private int lastMouseButtonDown = 0;

	private boolean m_t = true;

	public Panel(GraphicsController var1, int maxControls) {
		try {
			this.controlListEntryString2 = new String[maxControls][];
			this.controlUseAlternativeColour = new boolean[maxControls];
			this.controlY = new int[maxControls];
			this.controlFlag = new boolean[maxControls];
			this.controlArgInt = new int[maxControls];
			this.controlArgSprite = new Sprite[maxControls];
			this.controlListCurrentSize = new int[maxControls];
			this.controlWidth = new int[maxControls];
			this.controlVisible = new boolean[maxControls];
			this.graphics = var1;
			this.controlSelectedListIndex = new int[maxControls];
			this.controlClickedListIndex = new int[maxControls];
			this.controlType = new PanelControlType[maxControls];
			this.controlListEntryCrown = new int[maxControls][];
			this.controlText = new String[maxControls];
			this.isScrolling = new boolean[maxControls];
			this.controlScrollAmount = new int[maxControls];
			this.controlListEntryString = new String[maxControls][];
			this.controlHeight = new int[maxControls];
			this.controlSpaceHeight = new int[maxControls];
			this.controlSpaceTextHeight = new int[maxControls];
			this.controlClicked = new boolean[maxControls];
			this.controlListEntryString3 = new String[maxControls][];
			this.controlX = new int[maxControls];
			this.controlMaxSize = new int[maxControls];
			this.colorA = this.buildColor(114, 114, 176);
			this.colorB = this.buildColor(14, 14, 62);
			this.colorC = this.buildColor(200, 208, 232);
			this.colorD = this.buildColor(96, 129, 184);
			this.colorE = this.buildColor(53, 95, 115);
			this.colorF = this.buildColor(117, 142, 171);
			this.colorG = this.buildColor(98, 122, 158);
			this.colorH = this.buildColor(86, 100, 136);
			this.colorI = this.buildColor(135, 146, 179);
			this.colorJ = this.buildColor(97, 112, 151);
			this.colorK = this.buildColor(88, 102, 136);
			this.colorL = this.buildColor(84, 93, 120);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
				"qa.<init>(" + (var1 != null ? "{...}" : "null") + ',' + maxControls + ')');
		}
	}

	public final int addButton(int x, int y, int width, int height) {
		try {
			this.controlType[this.controlCount] = PanelControlType.BUTTON;

			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlX[this.controlCount] = x - width / 2;
			this.controlY[this.controlCount] = y - height / 2;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			return this.controlCount++;
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "qa.G(" + x + ',' + width + ',' + y + ',' + "dummy" + ',' + height + ')');
		}
	}

	public final int addButtonBackground(int x, int y, int width, int height) {
		try {

			this.controlType[this.controlCount] = PanelControlType.BUTTON_BACKGROUND;
			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlX[this.controlCount] = x - width / 2;
			this.controlY[this.controlCount] = y - height / 2;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			return this.controlCount++;
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "qa.B(" + "dummy" + ',' + width + ',' + height + ',' + x + ',' + y + ')');
		}
	}

	public final int addCenteredText(int x, int y, String text, int font, boolean useAltColour) {
		try {
			this.controlType[this.controlCount] = PanelControlType.CENTERED_TEXT;

			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlArgInt[this.controlCount] = font;
			this.controlUseAlternativeColour[this.controlCount] = useAltColour;
			this.controlX[this.controlCount] = x;
			this.controlY[this.controlCount] = y;
			this.controlText[this.controlCount] = text;
			return this.controlCount++;
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "qa.S(" + useAltColour + ',' + "dummy" + ',' + font + ',' + x + ','
				+ (text != null ? "{...}" : "null") + ',' + y + ')');
		}
	}

	public final int addCenteredTextEntry(int x, int y, int width, int maxSize, int height, int font, boolean maskText,
										  boolean useAltColor) {
		try {
			this.controlType[this.controlCount] = PanelControlType.CENTERED_TEXT_ENTRY;

			this.controlVisible[this.controlCount] = true;
			this.controlFlag[this.controlCount] = maskText;
			this.controlClicked[this.controlCount] = false;
			this.controlArgInt[this.controlCount] = font;
			this.controlUseAlternativeColour[this.controlCount] = useAltColor;
			this.controlX[this.controlCount] = x;
			this.controlY[this.controlCount] = y;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			this.controlMaxSize[this.controlCount] = maxSize;
			this.controlText[this.controlCount] = "";
			return this.controlCount++;
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "qa.U(" + "dummy" + ',' + maxSize + ',' + width + ',' + useAltColor + ','
				+ y + ',' + font + ',' + height + ',' + maskText + ',' + x + ')');
		}
	}

	public final int addDecoratedBox(int x, int y, int width, int height) {
		try {

			this.controlType[this.controlCount] = PanelControlType.DECORATED_BOX;
			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlX[this.controlCount] = x - width / 2;
			this.controlY[this.controlCount] = y - height / 2;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			return this.controlCount++;
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "qa.J(" + height + ',' + x + ',' + width + ',' + "dummy" + ',' + y + ')');
		}
	}

	public final int addLeftTextEntry(int x, int y, int width, int height, int font, int maxSize, boolean maskText,
									  boolean useAltColor) {
		try {

			this.controlType[this.controlCount] = PanelControlType.LEFT_TEXT_ENTRY;
			this.controlVisible[this.controlCount] = true;
			this.controlFlag[this.controlCount] = maskText;
			this.controlClicked[this.controlCount] = false;
			this.controlArgInt[this.controlCount] = font;
			this.controlUseAlternativeColour[this.controlCount] = useAltColor;
			this.controlX[this.controlCount] = x;
			this.controlY[this.controlCount] = y;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			this.controlMaxSize[this.controlCount] = maxSize;
			this.controlText[this.controlCount] = "";
			return this.controlCount++;
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "qa.L(" + maxSize + ',' + height + ',' + maskText + ',' + x + ',' + font
				+ ',' + y + ',' + "dummy" + ',' + width + ',' + useAltColor + ')');
		}
	}

	public final int addScrollingList3(int x, int y, int width, int height, int listSize, int font,
									   boolean useAltColor, int spaceHeight, int spaceTextHeight) {
		try {
			this.controlType[this.controlCount] = PanelControlType.SCROLLING_LIST3;

			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlArgInt[this.controlCount] = font;
			this.controlUseAlternativeColour[this.controlCount] = useAltColor;
			this.controlX[this.controlCount] = x;
			this.controlY[this.controlCount] = y;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			this.controlSpaceHeight[this.controlCount] = spaceHeight;
			this.controlSpaceTextHeight[this.controlCount] = spaceTextHeight;
			this.controlMaxSize[this.controlCount] = listSize;
			this.controlListEntryString[this.controlCount] = new String[listSize];
			this.controlListEntryCrown[this.controlCount] = new int[listSize];
			this.controlListEntryString2[this.controlCount] = new String[listSize];
			this.controlListEntryString3[this.controlCount] = new String[listSize];
			this.controlListCurrentSize[this.controlCount] = 0;
			this.controlScrollAmount[this.controlCount] = 0;
			this.controlClickedListIndex[this.controlCount] = -1;
			this.controlSelectedListIndex[this.controlCount] = -1;
			return this.controlCount++;
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "qa.AA(" + x + ',' + width + ',' + height + ',' + useAltColor + ','
				+ "dummy" + ',' + listSize + ',' + y + ',' + font + ')');
		}
	}

	public final int addScrollingList(int x, int y, int width, int height, int listSize, int font,
									  boolean useAltColor) {
		try {
			this.controlType[this.controlCount] = PanelControlType.SCROLLING_LIST;

			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlArgInt[this.controlCount] = font;
			this.controlUseAlternativeColour[this.controlCount] = useAltColor;
			this.controlX[this.controlCount] = x;
			this.controlY[this.controlCount] = y;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			this.controlMaxSize[this.controlCount] = listSize;
			this.controlListEntryString[this.controlCount] = new String[listSize];
			this.controlListEntryCrown[this.controlCount] = new int[listSize];
			this.controlListEntryString2[this.controlCount] = new String[listSize];
			this.controlListEntryString3[this.controlCount] = new String[listSize];
			this.controlListCurrentSize[this.controlCount] = 0;
			this.controlScrollAmount[this.controlCount] = 0;
			this.controlClickedListIndex[this.controlCount] = -1;
			this.controlSelectedListIndex[this.controlCount] = -1;
			return this.controlCount++;
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "qa.AA(" + x + ',' + width + ',' + height + ',' + useAltColor + ','
				+ "dummy" + ',' + listSize + ',' + y + ',' + font + ')');
		}
	}

	public final int addScrollingList2(int x, int y, int width, int height, int maxSize, int font, boolean altColor) {
		try {

			this.controlType[this.controlCount] = PanelControlType.SCROLLING_LIST_2;
			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlX[this.controlCount] = x;
			this.controlY[this.controlCount] = y;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			this.controlUseAlternativeColour[this.controlCount] = altColor;
			this.controlArgInt[this.controlCount] = font;
			this.controlMaxSize[this.controlCount] = maxSize;
			this.controlListCurrentSize[this.controlCount] = 0;
			this.controlScrollAmount[this.controlCount] = 0;
			this.controlListEntryString[this.controlCount] = new String[maxSize];
			this.controlListEntryCrown[this.controlCount] = new int[maxSize];
			this.controlListEntryString2[this.controlCount] = new String[maxSize];
			this.controlListEntryString3[this.controlCount] = new String[maxSize];
			return this.controlCount++;
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "qa.E(" + width + ',' + height + ',' + x + ',' + maxSize + ',' + y + ','
				+ font + ',' + "dummy" + ',' + altColor + ')');
		}
	}

	public final int addSprite(int x, int y, Sprite e) {
		try {

			int width = e.getWidth();
			int height = e.getHeight();
			this.controlType[this.controlCount] = PanelControlType.SPRITE;
			this.controlVisible[this.controlCount] = true;
			this.controlClicked[this.controlCount] = false;
			this.controlX[this.controlCount] = x - width / 2;
			this.controlY[this.controlCount] = y - height / 2;
			this.controlWidth[this.controlCount] = width;
			this.controlHeight[this.controlCount] = height;
			this.controlArgSprite[this.controlCount] = e;
			return this.controlCount++;
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "qa.PA(" + e.getID() + ',' + y + ',' + x + ',' + "dummy" + ')');
		}
	}

	public final void addToList(String str, boolean scrollToEnd, int crownId, String str3, String str2, int control) {
		try {

			int size = this.controlListCurrentSize[control]++;
			if (size >= this.controlMaxSize[control]) {
				--this.controlListCurrentSize[control];
				--size;

				for (int i = 0; size > i; ++i) {
					this.controlListEntryString[control][i] = this.controlListEntryString[control][1 + i];
					this.controlListEntryCrown[control][i] = this.controlListEntryCrown[control][i + 1];
					this.controlListEntryString2[control][i] = this.controlListEntryString2[control][i + 1];
					this.controlListEntryString3[control][i] = this.controlListEntryString3[control][i + 1];
				}
			}

			this.controlListEntryString[control][size] = str;
			this.controlListEntryCrown[control][size] = crownId;

			this.controlListEntryString2[control][size] = str2;
			this.controlListEntryString3[control][size] = str3;
			if (scrollToEnd) {
				this.controlScrollAmount[control] = 999999;
			}

		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
				"qa.JA(" + (str != null ? "{...}" : "null") + ',' + scrollToEnd + ',' + crownId + ','
					+ (str3 != null ? "{...}" : "null") + ',' + (str2 != null ? "{...}" : "null") + ','
					+ "dummy" + ',' + control + ')');
		}
	}

	private int buildColor(int r, int g, int b) {
		try {

			return GenUtil.buildColor(r, g, b);
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "qa.QA(" + b + ',' + g + ',' + r + ',' + "dummy" + ')');
		}
	}

	public final void clearList(int control) {
		try {
			this.controlListCurrentSize[control] = 0;

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.P(" + "dummy" + ',' + control + ')');
		}
	}

	public final void drawPanel() {
		try {

			for (int i = 0; i < this.controlCount; ++i) {
				if (this.controlVisible[i]) {
					switch (this.controlType[i]) {
						case CENTERED_TEXT:
							this.renderText(i,
								this.controlX[i]
									- this.graphics.stringWidth(this.controlArgInt[i], this.controlText[i]) / 2,
								this.controlY[i], this.controlArgInt[i], this.controlText[i], 0);
							break;
						case BUTTON_BACKGROUND:
							this.renderButtonBackground(this.controlX[i], this.controlY[i], this.controlWidth[i],
								this.controlHeight[i]);
							break;
						case CENTERED_LIST:
							this.renderCenteredList(i, this.controlX[i], this.controlY[i], this.controlArgInt[i],
								this.controlListEntryString[i]);
							break;
						case SCROLLING_LIST:
							this.renderScrollingList(i, this.controlX[i], this.controlY[i], this.controlWidth[i],
								this.controlHeight[i], this.controlArgInt[i], this.controlListCurrentSize[i],
								this.controlListEntryString[i], this.controlListEntryCrown[i],
								this.controlScrollAmount[i]);
							break;
						case TOGGLE_BUTTON:
							this.renderToggleButton(i, this.controlX[i], this.controlY[i], this.controlWidth[i],
								this.controlHeight[i]);
							break;
						case SPRITE:
							this.renderSprite(this.controlX[i], this.controlY[i], this.controlArgSprite[i]);
							break;
						case DECORATED_BOX:
							this.renderDecoratedBox(this.controlX[i], this.controlY[i], this.controlWidth[i],
								this.controlHeight[i]);
							break;
						case HORIZONTAL_LIST:
							this.renderHorizontalList(i, this.controlX[i], this.controlY[i], this.controlArgInt[i],
								this.controlListEntryString[i]);
							break;
						case LEFT_TEXT_ENTRY:
						case CENTERED_TEXT_ENTRY:
							this.renderTextEntry(i, this.controlX[i], this.controlY[i], this.controlWidth[i],
								this.controlHeight[i], this.controlArgInt[i], this.controlText[i]);
							break;
						case SCROLLING_LIST_2:
							this.renderScrollingList2(i, this.controlX[i], this.controlY[i], this.controlWidth[i],
								this.controlHeight[i], this.controlArgInt[i], this.controlListCurrentSize[i],
								this.controlListEntryString[i], this.controlListEntryCrown[i],
								this.controlScrollAmount[i]);
							break;
						case SCROLLING_LIST3:
							this.renderScrollingList3(i, this.controlX[i], this.controlY[i], this.controlWidth[i],
								this.controlHeight[i], this.controlArgInt[i], this.controlListCurrentSize[i],
								this.controlListEntryString[i], this.controlListEntryCrown[i],
								this.controlScrollAmount[i], this.controlSpaceHeight[i], this.controlSpaceTextHeight[i]);
							break;
						case HORIZ_LINE:
							this.renderHorizLine(this.controlX[i], this.controlY[i], (int) this.controlWidth[i], 0);
							break;
						case LEFT_TEXT:
							this.renderText(i, this.controlX[i], this.controlY[i], this.controlArgInt[i],
								this.controlText[i], 0);
							break;
						default:
							// System.err.println("Bad control type " +
							// this.controlType[i]);
					}
				}
			}

			this.lastMouseButtonDown = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "qa.HA(" + "dummy" + ')');
		}
	}

	public final int getControlClickedListIndex(int control) {
		try {

			return this.controlClickedListIndex[control];
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.CA(" + "dummy" + ',' + control + ')');
		}
	}

	public final String getControlListString2(int control, int index) {
		try {

			return this.controlListEntryString2[control][index];
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "qa.T(" + index + ',' + "dummy" + ',' + control + ')');
		}
	}

	public final String getControlListString3(int control, int entry) {
		try {

			return this.controlListEntryString3[control][entry];
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "qa.I(" + entry + ',' + "dummy" + ',' + control + ')');
		}
	}

	public final int getControlSelectedListIndex(int controlID) {
		try {

			return this.controlSelectedListIndex[controlID];
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.C(" + controlID + ',' + "dummy" + ')');
		}
	}

	public final int getControlSelectedListInt(int controlID, int entryID) {
		try {

			return this.controlListEntryCrown[controlID][entryID];
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.C(" + controlID + ',' + "dummy" + ')');
		}
	}

	public final String getControlText(int controlID) {
		try {

			return null == this.controlText[controlID] ? "null" : this.controlText[controlID];
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.BA(" + controlID + ',' + "dummy" + ')');
		}
	}

	public final int getControlCount(int controlID) {
		try {

			return this.controlListCurrentSize[controlID];
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.BA(" + controlID + ',' + "dummy" + ')');
		}
	}

	public final void handleMouse(int mouseX, int mouseY, int currentMouseButtonDown, int lastMouseButtonDown) {
		try {
			this.currMouseY = mouseY;
			this.currMouseX = mouseX;
			if (lastMouseButtonDown != 0) {
				this.lastMouseButtonDown = lastMouseButtonDown;
			}

			this.currentMouseButtonDown = currentMouseButtonDown;

			if (lastMouseButtonDown == 1) {
				for (int i = 0; i < this.controlCount; ++i) {
					if (this.controlVisible[i] && this.controlType[i] == PanelControlType.BUTTON
						&& this.controlX[i] <= this.currMouseX && this.currMouseY >= this.controlY[i]
						&& this.controlWidth[i] + this.controlX[i] >= this.currMouseX
						&& this.currMouseY <= this.controlHeight[i] + this.controlY[i]) {
						this.controlClicked[i] = true;


					}

					if (this.controlVisible[i] && this.controlType[i] == PanelControlType.TOGGLE_BUTTON
						&& this.currMouseX >= this.controlX[i] && this.currMouseY >= this.controlY[i]
						&& this.controlWidth[i] + this.controlX[i] >= this.currMouseX
						&& this.controlHeight[i] + this.controlY[i] >= this.currMouseY) {
						this.controlClickedListIndex[i] = 1 - this.controlClickedListIndex[i];
					}
				}
			}

			if (currentMouseButtonDown == 1) {
				++this.button1HeldTicks;
			} else {
				this.button1HeldTicks = 0;
			}

			if (lastMouseButtonDown == 1 || this.button1HeldTicks > 20) {
				for (int i = 0; this.controlCount > i; ++i) {
					if (this.controlVisible[i] && this.controlType[i] == PanelControlType.TYPE_15
						&& this.currMouseX >= this.controlX[i] && this.currMouseY >= this.controlY[i]
						&& this.controlWidth[i] + this.controlX[i] >= this.currMouseX
						&& this.currMouseY <= this.controlY[i] + this.controlHeight[i]) {
						this.controlClicked[i] = true;
					}
				}

				this.button1HeldTicks -= 5;
			}

		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "qa.O(" + currentMouseButtonDown + ',' + mouseY + ',' + "dummy" + ','
				+ lastMouseButtonDown + ',' + mouseX + ')');
		}
	}

	public final void hide(int control) {
		try {
			this.controlVisible[control] = false;

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.EA(" + "dummy" + ',' + control + ')');
		}
	}

	public final boolean isClicked(int control) {
		try {

			if (this.controlVisible[control] && this.controlClicked[control]) {
				this.controlClicked[control] = false;
				return true;
			} else {
				return false;
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.W(" + "dummy" + ',' + control + ')');
		}
	}

	public final void keyPress(int key) {
		try {

			if (key != 0) {
				if (this.focusControlIndex != -1 && null != this.controlText[this.focusControlIndex]
					&& this.controlVisible[this.focusControlIndex]) {
					int currLen = this.controlText[this.focusControlIndex].length();
					if (key == '\b' && currLen > 0) {
						this.controlText[this.focusControlIndex] = this.controlText[this.focusControlIndex].substring(0,
							currLen - 1);
					}

					if ((key == 10 || key == 13) && currLen > 0) {
						this.controlClicked[this.focusControlIndex] = true;
					}

					String var4 = Fonts.inputFilterChars;
					if (currLen < this.controlMaxSize[this.focusControlIndex]) {
						for (int var5 = 0; var5 < var4.length(); ++var5) {
							if (key == var4.charAt(var5)) {
								this.controlText[this.focusControlIndex] = this.controlText[this.focusControlIndex]
									+ (char) key;
							}
						}
					}

					if (key == '\t') {
						do {
							this.focusControlIndex = (1 + this.focusControlIndex) % this.controlCount;
						} while (this.controlType[this.focusControlIndex] != PanelControlType.LEFT_TEXT_ENTRY
							&& this.controlType[this.focusControlIndex] != PanelControlType.CENTERED_TEXT_ENTRY);
					}
				}

			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "qa.H(" + "dummy" + ',' + key + ')');
		}
	}

	private void renderButtonBackground(int x, int y, int width, int height) {
		try {
			this.graphics.setClip(x, x + width, y + height, y);

			this.graphics.drawVerticalGradient(x, y, width, height, this.colorI, this.colorL);
			if (MiscFunctions.drawBackgroundArrow) {
				for (int xi = x - (y & 63); xi < width + x; xi += 128) {
					for (int yi = y - (31 & y); y + height > yi; yi += 128) {
						this.graphics.a(this.graphics.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.LEFTARROW.id())), 0, xi, 128, (int) yi);
					}
				}
			}

			this.graphics.drawLineHoriz(x, y, width, this.colorI);
			this.graphics.drawLineHoriz(1 + x, y + 1, width - 2, this.colorI);
			this.graphics.drawLineHoriz(2 + x, 2 + y, width - 4, this.colorJ);
			this.graphics.drawLineVert(x, y, this.colorI, height);
			this.graphics.drawLineVert(1 + x, y + 1, this.colorI, height - 2);
			this.graphics.drawLineVert(x + 2, y + 2, this.colorJ, height - 4);
			this.graphics.drawLineHoriz(x, y + (height - 1), width, this.colorL);
			this.graphics.drawLineHoriz(x + 1, y + (height - 2), width - 2, this.colorL);
			this.graphics.drawLineHoriz(2 + x, y + height - 3, width - 4, this.colorK);
			this.graphics.drawLineVert(width + x - 1, y, this.colorL, height);
			this.graphics.drawLineVert(width + x - 2, y + 1, this.colorL, height - 2);
			this.graphics.drawLineVert(x + (width - 3), y + 2, this.colorK, height - 4);
			this.graphics.clearClip();
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "qa.F(" + width + ',' + "dummy" + ',' + height + ',' + x + ',' + y + ')');
		}
	}

	private void renderCenteredList(int controlIndex, int x, int y, int font, String[] entries) {
		try {

			int count = entries.length;
			int lineY = y - (count - 1) * this.graphics.fontHeight(font) / 2;

			for (int i = 0; count > i; ++i) {
				int color;
				if (this.controlUseAlternativeColour[controlIndex]) {
					color = 16777215;
				} else {
					color = 0;
				}

				int width = this.graphics.stringWidth(font, entries[i]);
				if (this.currMouseX >= x - width / 2 && this.currMouseX <= x + width / 2 && this.currMouseY - 2 <= lineY
					&& lineY - this.graphics.fontHeight(font) < this.currMouseY - 2) {
					if (this.controlUseAlternativeColour[controlIndex]) {
						color = 8421504;
					} else {
						color = 16777215;
					}

					if (this.lastMouseButtonDown == 1) {
						this.controlClickedListIndex[controlIndex] = i;
						this.controlClicked[controlIndex] = true;
					}
				}

				if (i == this.controlClickedListIndex[controlIndex]) {
					if (!this.controlUseAlternativeColour[controlIndex]) {
						color = 12582912;
					} else {
						color = 16711680;
					}
				}

				this.graphics.drawColoredString(x - width / 2, lineY, entries[i], font, color, 0);
				lineY += this.graphics.fontHeight(font);
			}

		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13, "qa.FA(" + (entries != null ? "{...}" : "null") + ',' + controlIndex
				+ ',' + "dummy" + ',' + font + ',' + x + ',' + y + ')');
		}
	}

	private void renderDecoratedBox(int x, int y, int width, int height) {
		try {
			this.graphics.drawBox(x, y, width, height, 0);

			this.graphics.drawBoxBorder(x, width, y, height, this.colorF);
			this.graphics.drawBoxBorder(1 + x, width - 2, 1 + y, height - 2, this.colorG);
			this.graphics.drawBoxBorder(x + 2, width - 4, 2 + y, height - 4, this.colorH);

			this.graphics.drawSprite(this.graphics.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.DECORATEDBOXUL.id())), x, y);
			this.graphics.drawSprite(this.graphics.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.DECORATEDBOXUR.id())), width + x - 7, y);
			this.graphics.drawSprite(this.graphics.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.DECORATEDBOXLL.id())), x, y - (7 - height));
			this.graphics.drawSprite(this.graphics.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.DECORATEDBOXLR.id())), x + width - 7, height - 7 + y);
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "qa.V(" + y + ',' + width + ',' + "true" + ',' + x + ',' + height + ')');
		}

	}

	private void renderHorizLine(int x, int y, int width, int color) {
		try {

			this.graphics.drawLineHoriz(x, y, width, color);
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "qa.RA(" + y + ',' + color + ',' + x + ',' + width + ')');
		}
	}

	private void renderHorizontalList(int controlIndex, int x, int y, int font, String[] entiresString) {
		try {

			int totalWidth = 0;
			int var8 = entiresString.length;
			int lineX;
			for (lineX = 0; var8 > lineX; ++lineX) {
				totalWidth += this.graphics.stringWidth(font, entiresString[lineX]);
				if (lineX < var8 - 1) {
					totalWidth += this.graphics.stringWidth(font, "  ");
				}
			}

			lineX = x - totalWidth / 2;
			int lineY = this.graphics.fontHeight(font) / 3 + y;

			for (int var11 = 0; var8 > var11; ++var11) {
				int color;
				if (!this.controlUseAlternativeColour[controlIndex]) {
					color = 0;
				} else {
					color = 16777215;
				}

				if (this.currMouseX >= lineX
					&& lineX + this.graphics.stringWidth(font, entiresString[var11]) >= this.currMouseX
					&& this.currMouseY <= lineY && lineY - this.graphics.fontHeight(font) < this.currMouseY) {
					if (this.controlUseAlternativeColour[controlIndex]) {
						color = 8421504;
					} else {
						color = 16777215;
					}

					if (this.lastMouseButtonDown == 1) {
						this.controlClickedListIndex[controlIndex] = var11;
						this.controlClicked[controlIndex] = true;
					}
				}

				if (var11 == this.controlClickedListIndex[controlIndex]) {
					if (this.controlUseAlternativeColour[controlIndex]) {
						color = 16711680;
					} else {
						color = 12582912;
					}
				}

				this.graphics.drawColoredString(lineX, lineY, entiresString[var11], font, color, 0);
				lineX += this.graphics.stringWidth(font, entiresString[var11] + "  ");
			}
		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13, "qa.DA(" + x + ',' + font + ',' + "dummy" + ','
				+ (entiresString != null ? "{...}" : "null") + ',' + controlIndex + ',' + y + ')');
		}
	}

	private void renderScrollbar(int x, int y, int width, int height, int barDragSize, int barDragPos) {
		try {

			int barX = x + width - 12;
			this.graphics.drawBoxBorder(barX, 12, y, height, 0);
			this.graphics.drawSprite(this.graphics.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.MINIARROWUP.id())), 1 + barX, y + 1);
			this.graphics.drawSprite(this.graphics.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.MINIARROWDOWN.id())), 1 + barX, height - 12 + y);
			this.graphics.drawLineHoriz(barX, 13 + y, 12, 0);
			this.graphics.drawLineHoriz(barX, y - 13 + height, 12, 0);
			this.graphics.drawVerticalGradient(1 + barX, 14 + y, 11, height - 27, this.colorB, this.colorA);
			this.graphics.drawBox(barX + 3, 14 + y + barDragPos, 7, barDragSize, this.colorD);
			this.graphics.drawLineVert(barX + 2, y + barDragPos + 14, this.colorC, barDragSize);
			this.graphics.drawLineVert(barX + 10, 14 + barDragPos + y, this.colorE, barDragSize);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "qa.A(" + barDragSize + ',' + barDragPos + ',' + x + ',' + width + ','
				+ height + ',' + y + ',' + "dummy" + ')');
		}
	}


	private void renderScrollingList(int controlIndex, int x, int y, int width, int height, int font,
									 int entryCount, String[] entriesString, int[] entriesCrowns, int scroll) {
		try {

			int maxLines = height / this.graphics.fontHeight(font);
			if (entryCount <= maxLines) {
				scroll = 0;
				this.controlScrollAmount[controlIndex] = 0;
			} else {
				int scrollBarStartX = width - 12 + x;
				int barDraggerSize = maxLines * (height - 27) / entryCount;
				if (barDraggerSize < 6) {
					barDraggerSize = 6;
				}

				int barDraggerY = (height - 27 - barDraggerSize) * scroll / (entryCount - maxLines);
				if (this.currentMouseButtonDown == 1 && this.currMouseX >= scrollBarStartX
					&& 12 + scrollBarStartX >= this.currMouseX) {
					if (y < this.currMouseY && this.currMouseY < 12 + y && scroll > 0) {
						--scroll;
					}

					if (this.currMouseY > height - 12 + y && this.currMouseY < y + height
						&& scroll < entryCount - maxLines) {
						++scroll;
					}

					this.controlScrollAmount[controlIndex] = scroll;
				}

				if (this.currentMouseButtonDown == 1
					&& (this.currMouseX >= scrollBarStartX && 12 + scrollBarStartX >= this.currMouseX
					|| this.currMouseX >= scrollBarStartX - 12 && scrollBarStartX + 24 >= this.currMouseX
					&& this.isScrolling[controlIndex])) {
					if (this.currMouseY > 12 + y && y - (12 - height) > this.currMouseY) {
						this.isScrolling[controlIndex] = true;
						int var16 = this.currMouseY - 12 - (y + barDraggerSize / 2);
						scroll = entryCount * var16 / (height - 24);
						if (scroll < 0) {
							scroll = 0;
						}

						if (entryCount - maxLines < scroll) {
							scroll = entryCount - maxLines;
						}

						this.controlScrollAmount[controlIndex] = scroll;
					}
				} else {
					this.isScrolling[controlIndex] = false;
				}

				barDraggerY = (height - barDraggerSize - 27) * scroll / (entryCount - maxLines);
				this.renderScrollbar(x, y, width, height, barDraggerSize, barDraggerY);
			}
			int lineY;

			this.controlSelectedListIndex[controlIndex] = -1;
			int heightWhitespace = height - this.graphics.fontHeight(font) * maxLines;
			lineY = this.graphics.fontHeight(font) * 5 / 6 + y + heightWhitespace / 2;

			for (int line = scroll; line < entryCount; ++line) {
				int color;
				if (this.controlUseAlternativeColour[controlIndex]) {
					color = 16777215;
				} else {
					color = 0;
				}

				if (this.currMouseX >= 2 + x
					&& this.currMouseX <= this.graphics.stringWidth(font, entriesString[line]) + 2 + x
					&& this.currMouseY - 2 <= lineY
					&& this.currMouseY - 2 > lineY - this.graphics.fontHeight(font)) {
					if (this.controlUseAlternativeColour[controlIndex]) {
						color = 8421504;
					} else {
						color = 16777215;
					}

					this.controlSelectedListIndex[controlIndex] = line;
					if (this.lastMouseButtonDown == 1) {
						this.controlClickedListIndex[controlIndex] = line;
						this.controlClicked[controlIndex] = true;
					}
				}

				if (line == this.controlClickedListIndex[controlIndex] && this.m_t) {
					color = 16711680;
				}

				this.graphics.drawColoredString(x + 2, lineY, entriesString[line], font, color, entriesCrowns[line]);
				lineY += this.graphics.fontHeight(font);
				if (lineY >= height + y) {
					break;
				}
			}

		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17,
				"qa.D(" + height + ',' + x + ',' + controlIndex + ',' + (entriesString != null ? "{...}" : "null")
					+ ',' + entryCount + ',' + font + ',' + y + ',' + (entriesCrowns != null ? "{...}" : "null")
					+ ',' + "dummy" + ',' + scroll + ',' + width + ')');
		}
	}

	private void renderScrollingList3(int controlIndex, int x, int y, int width, int height, int font,
									  int entryCount, String[] entriesString, int[] entriesInt, int scroll, int spaceHeight, int spaceHeightText) {
		try {

			int maxLines = (height - (spaceHeightText + spaceHeight)) / this.graphics.fontHeight(font);
			if (entryCount <= maxLines) {
				scroll = 0;
				this.controlScrollAmount[controlIndex] = 0;
			} else {
				int scrollBarStartX = width - 12 + x;
				int barDraggerSize = maxLines * (height - 27 - (spaceHeightText + spaceHeight)) / entryCount;
				if (barDraggerSize < 6) {
					barDraggerSize = 6;
				}

				int barDraggerY = (height - 27 - (spaceHeightText + spaceHeight) - barDraggerSize) * scroll / (entryCount - maxLines);
				if (this.currentMouseButtonDown == 1 && this.currMouseX >= scrollBarStartX
					&& 12 + scrollBarStartX >= this.currMouseX) {
					if (y < this.currMouseY && this.currMouseY < 12 + y && scroll > 0) {
						--scroll;
					}

					if (this.currMouseY > height - (spaceHeightText + spaceHeight) - 12 + y + spaceHeight && this.currMouseY < y + height + (spaceHeightText + spaceHeight)
						&& scroll < entryCount - maxLines) {
						++scroll;
					}

					this.controlScrollAmount[controlIndex] = scroll;
				}

				if (this.currentMouseButtonDown == 1
					&& (this.currMouseX >= scrollBarStartX && 12 + scrollBarStartX >= this.currMouseX
					|| this.currMouseX >= scrollBarStartX - 12 && scrollBarStartX + 24 >= this.currMouseX
					&& this.isScrolling[controlIndex])) {
					if (this.currMouseY > 12 + y + (spaceHeightText + spaceHeight) && y - (12 - height - (spaceHeightText + spaceHeight)) > this.currMouseY) {
						this.isScrolling[controlIndex] = true;
						int var16 = this.currMouseY - 12 - (spaceHeightText + spaceHeight) - (y + barDraggerSize / 2);
						scroll = entryCount * var16 / (height - 24 - (spaceHeightText + spaceHeight));
						if (scroll < 0) {
							scroll = 0;
						}

						if (entryCount - maxLines < scroll) {
							scroll = entryCount - maxLines;
						}

						this.controlScrollAmount[controlIndex] = scroll;
					}
				} else {
					this.isScrolling[controlIndex] = false;
				}

				barDraggerY = (height - barDraggerSize - 27) * scroll / (entryCount - maxLines);
				this.renderScrollbar(x, y, width, height, barDraggerSize, barDraggerY);
			}
			int lineY;

			this.controlSelectedListIndex[controlIndex] = -1;
			int heightWhitespace = (height - (spaceHeightText + spaceHeight)) - this.graphics.fontHeight(font) * maxLines;
			lineY = this.graphics.fontHeight(font) * 5 / 6 + y + heightWhitespace / 2;

			for (int line = scroll; line < entryCount; ++line) {
				int color;
				if (this.controlUseAlternativeColour[controlIndex]) {
					color = 16777215;
				} else {
					color = 0;
				}

				if (this.currMouseX >= 2 + x
					&& this.currMouseX <= this.graphics.stringWidth(font, entriesString[line]) + 2 + x
					&& this.currMouseY - 2 <= lineY
					&& this.currMouseY - 2 > lineY - this.graphics.fontHeight(font)) {
					if (this.controlUseAlternativeColour[controlIndex]) {
						color = 8421504;
					} else {
						color = 16777215;
					}

					this.controlSelectedListIndex[controlIndex] = line;
					if (this.lastMouseButtonDown == 1) {
						this.controlClickedListIndex[controlIndex] = line;
						this.controlClicked[controlIndex] = true;
					}
				}

				if (line == this.controlClickedListIndex[controlIndex] && this.m_t) {
					color = 16711680;
				}

				this.graphics.drawColoredString(x + 2, lineY - (spaceHeightText), entriesString[line], font, color, 0);
				lineY += this.graphics.fontHeight(font) + spaceHeight;
				if (lineY >= height + y) {
					break;
				}
			}

		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17,
				"qa.D(" + height + ',' + x + ',' + controlIndex + ',' + (entriesString != null ? "{...}" : "null")
					+ ',' + entryCount + ',' + font + ',' + y + ',' + (entriesInt != null ? "{...}" : "null")
					+ ',' + "dummy" + ',' + scroll + ',' + width + ')');
		}
	}

	private void renderScrollingList2(int controlIndex, int x, int y, int width, int height, int font,
									  int entryCount, String[] entriesString, int[] entriesInt, int scroll) {
		try {

			int var12 = height / this.graphics.fontHeight(font);
			if (scroll > entryCount - var12) {
				scroll = entryCount - var12;
			}

			if (scroll < 0) {
				scroll = 0;
			}

			this.controlScrollAmount[controlIndex] = scroll;
			int var13;
			int var14;
			int var15;
			if (entryCount > var12) {
				var13 = width - 12 + x;
				var14 = var12 * (height - 27) / entryCount;
				if (var14 < 6) {
					var14 = 6;
				}

				var15 = (-var14 - 27 + height) * scroll / (entryCount - var12);
				if (this.currentMouseButtonDown == 1 && var13 <= this.currMouseX && this.currMouseX <= 12 + var13) {
					if (this.currMouseY > y && y + 12 > this.currMouseY && scroll > 0) {
						--scroll;
					}

					if (height + y - 12 < this.currMouseY && this.currMouseY < height + y
						&& entryCount - var12 > scroll) {
						++scroll;
					}

					this.controlScrollAmount[controlIndex] = scroll;
				}

				if (this.currentMouseButtonDown == 1
					&& (this.currMouseX >= var13 && this.currMouseX <= var13 + 12 || this.currMouseX >= var13 - 12
					&& 24 + var13 >= this.currMouseX && this.isScrolling[controlIndex])) {
					if (this.currMouseY > 12 + y && this.currMouseY < y + height - 12) {
						this.isScrolling[controlIndex] = true;
						int var16 = this.currMouseY - var14 / 2 - y - 12;
						scroll = var16 * entryCount / (height - 24);
						if (entryCount - var12 < scroll) {
							scroll = entryCount - var12;
						}

						if (scroll < 0) {
							scroll = 0;
						}

						this.controlScrollAmount[controlIndex] = scroll;
					}
				} else {
					this.isScrolling[controlIndex] = false;
				}

				var15 = scroll * (height - 27 - var14) / (entryCount - var12);
				this.renderScrollbar(x, y, width, height, var14, var15);
			}

			var13 = height - this.graphics.fontHeight(font) * var12;
			var14 = y + this.graphics.fontHeight(font) * 5 / 6 + var13 / 2;

			for (var15 = scroll; entryCount > var15; ++var15) {
				if (this.lastMouseButtonDown != 0 && 2 + x <= this.currMouseX
					&& this.graphics.stringWidth(font, entriesString[var15]) + x + 2 >= this.currMouseX
					&& var14 >= this.currMouseY - 2
					&& this.currMouseY - 2 > var14 - this.graphics.fontHeight(font)) {
					this.controlClicked[controlIndex] = true;
					this.controlClickedListIndex[controlIndex] = FastMath.bitwiseOr(this.lastMouseButtonDown << 16,
						var15);
				}

				this.renderString(controlIndex, 2 + x, var14, font, entriesInt[var15], entriesString[var15]);
				var14 += this.graphics.fontHeight(font) - MiscFunctions.textListEntryHeightMod;
				if (var14 >= y + height) {
					break;
				}
			}

		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17,
				"qa.M(" + (entriesInt != null ? "{...}" : "null") + ',' + scroll + ',' + controlIndex + ',' + x
					+ ',' + y + ',' + width + ',' + height + ',' + entryCount + ','
					+ (entriesString != null ? "{...}" : "null") + ',' + font + ',' + "false" + ')');
		}
	}

	private void renderSprite(int x, int y, Sprite sprite) {
		try {
			this.graphics.drawSprite(sprite, x, y);

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "qa.LA(" + "dummy" + ',' + y + ',' + x + ',' + sprite.getID() + ')');
		}
	}

	private void renderString(int control, int x, int y, int font, int spriteHeader, String str) {
		try {

			int color;
			if (!this.controlUseAlternativeColour[control]) {
				color = 0;
			} else {
				color = 16777215;
			}
			this.graphics.drawColoredString(x, y, str, font, color, spriteHeader);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "qa.IA(" + font + ',' + control + ',' + "dummy" + ',' + spriteHeader + ','
				+ x + ',' + y + ',' + (str != null ? "{...}" : "null") + ')');
		}
	}

	private void renderText(int control, int x, int y, int font, String str, int spriteHeader) {
		try {

			int yReal = y + this.graphics.fontHeight(font) / 3;
			this.renderString(control, x, yReal, font, spriteHeader, str);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "qa.R(" + (str != null ? "{...}" : "null") + ',' + y + ',' + "dummy" + ','
				+ font + ',' + x + ',' + control + ',' + spriteHeader + ')');
		}
	}

	private void renderTextEntry(int controlIndex, int x, int y, int width, int height, int font, String text) {
		try {

			if (this.controlFlag[controlIndex]) {
				int len = text.length();
				text = "";

				for (int i = 0; i < len; ++i) {
					text = text + "X";
				}
			}

			if (this.controlType[controlIndex] == PanelControlType.LEFT_TEXT_ENTRY) {
				if (this.lastMouseButtonDown == 1 && x <= this.currMouseX && y - height / 2 <= this.currMouseY
					&& this.currMouseX <= x + width && height / 2 + y >= this.currMouseY) {
					this.focusControlIndex = controlIndex;
				}
			} else if (this.controlType[controlIndex] == PanelControlType.CENTERED_TEXT_ENTRY) {
				if (this.lastMouseButtonDown == 1 && x - width / 2 <= this.currMouseX
					&& this.currMouseY >= y - height / 2 && width / 2 + x >= this.currMouseX
					&& this.currMouseY <= y + height / 2) {
					this.focusControlIndex = controlIndex;
				}

				x -= this.graphics.stringWidth(font, text) / 2;
			}

			if (this.focusControlIndex == controlIndex) {
				text = text + "*";
			}
			int realY = this.graphics.fontHeight(font) / 3 + y;
			this.renderString(controlIndex, x, realY, font, 0, text);
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "qa.KA(" + font + ',' + (text != null ? "{...}" : "null") + ',' + width
				+ ',' + height + ',' + "dummy" + ',' + x + ',' + y + ',' + controlIndex + ')');
		}
	}

	private void renderToggleButton(int controlIndex, int x, int y, int width, int height) {
		try {
			this.graphics.drawBox(x, y, width, height, 16777215);

			this.graphics.drawLineHoriz(x, y, width, this.colorI);
			this.graphics.drawLineVert(x, y, this.colorI, height);
			this.graphics.drawLineHoriz(x, height + y - 1, width, this.colorL);
			this.graphics.drawLineVert(width + x - 1, y, this.colorL, height);
			if (this.controlClickedListIndex[controlIndex] == 1) {
				for (int i = 0; i < height; ++i) {
					this.graphics.drawLineHoriz(i + x, y + i, 1, 0);
					this.graphics.drawLineHoriz(width + x - 1 - i, i + y, 1, 0);
				}
			}
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
				"qa.K(" + "dummy" + ',' + x + ',' + height + ',' + y + ',' + width + ',' + controlIndex + ')');
		}
	}

	public final void resetList(int control) {
		try {

			this.controlScrollAmount[control] = 0;
			this.controlSelectedListIndex[control] = -1;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.N(" + control + ',' + "dummy" + ')');
		}
	}

	public final void resetListToIndex(int control, int base) {
		this.controlScrollAmount[control] = base;
		this.controlSelectedListIndex[control] = -1;
	}

	public final void setFocus(int control) {
		try {
			this.focusControlIndex = control;

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.OA(" + control + ',' + "dummy" + ')');
		}
	}

	public boolean focusOn(int i) {
		return focusControlIndex == i;
	}

	public final void setListEntry(int controlID, int entryID, String str, int crownId, String str2, String str3) {
		try {

			this.controlListEntryString[controlID][entryID] = str;
			this.controlListEntryCrown[controlID][entryID] = crownId;
			this.controlListEntryString2[controlID][entryID] = str2;
			this.controlListEntryString3[controlID][entryID] = str3;
			if (entryID + 1 > this.controlListCurrentSize[controlID]) {
				this.controlListCurrentSize[controlID] = entryID + 1;
			}
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
				"qa.Q(" + entryID + ',' + (str3 != null ? "{...}" : "null") + ',' + "dummy" + ',' + crownId + ','
					+ (str2 != null ? "{...}" : "null") + ',' + (str != null ? "{...}" : "null") + ','
					+ controlID + ')');
		}
	}

	public final void setText(int control, String str) {
		try {
			this.controlText[control] = str;

		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5,
				"qa.MA(" + control + ',' + (str != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public final void show(int control) {
		try {

			this.controlVisible[control] = true;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "qa.NA(" + control + ',' + "dummy" + ')');
		}
	}

	public int getScrollPosition(int index) {
		return controlScrollAmount[index];
	}

	public void scrollMethodList(int handle, int i) {
		int limit = controlListCurrentSize[handle] - (controlHeight[handle] / graphics.fontHeight(controlArgInt[handle]));
		int diff = Math.abs(limit - controlScrollAmount[handle]);
		if (controlScrollAmount[handle] <= limit) {
			if (i > 0)
				if (diff < i)
					controlScrollAmount[handle] += diff;
				else
					controlScrollAmount[handle] += i;
			else if (i < 0 && controlScrollAmount[handle] > 0)
				if (controlScrollAmount[handle] < -i)
					controlScrollAmount[handle] -= controlScrollAmount[handle];
				else
					controlScrollAmount[handle] += i;
		}
	}

	public void scrollMethodCustomList(int handle, int i, int cDifference) {
		int limit = controlListCurrentSize[handle] - (controlHeight[handle] / graphics.fontHeight(controlArgInt[handle])) + cDifference;
		int diff = Math.abs(limit - controlScrollAmount[handle]);
		if (controlScrollAmount[handle] <= limit) {
			if (i > 0)
				if (diff < i)
					controlScrollAmount[handle] += diff;
				else
					controlScrollAmount[handle] += i;
			else if (i < 0 && controlScrollAmount[handle] > 0)
				if (controlScrollAmount[handle] < -i)
					controlScrollAmount[handle] -= controlScrollAmount[handle];
				else
					controlScrollAmount[handle] += i;
		}
	}

	public void reposition(int id, int x, int y, int w, int h) {
		this.controlX[id] = x;
		this.controlY[id] = y;
		this.controlWidth[id] = w;
		this.controlHeight[id] = h;

	}

	public void resetScrollIndex(int auctionScrollHandle) {
		controlScrollAmount[auctionScrollHandle] = 0;
	}
}
