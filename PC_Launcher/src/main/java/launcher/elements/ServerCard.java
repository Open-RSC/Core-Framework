package launcher.elements;

import javax.swing.*;

public class ServerCard {
	public LaunchButton logo;
	public JButton rscText;
	public JButton serverName;
	public JLabel wikiText;
	public JButton hiscore;
	public JLabel onlineText;
	public JLabel clientLogo;
	public LaunchButton wikiLogo;
	public JLabel playersLogo;
	public JLabel horizontalRule;
	public JLabel bannerSticker;
    public JLabel underConstruction;
    public JLabel comingSoonPlaceholder;

    public JLabel removeAll(JLabel background) {
		if (null != this.logo)
			background.remove(this.logo);
		if (null != this.rscText)
			background.remove(this.rscText);
		if (null != this.serverName)
			background.remove(this.serverName);
		if (null != this.wikiText)
			background.remove(this.wikiText);
		if (null != this.hiscore)
			background.remove(this.hiscore);
		if (null != this.onlineText)
			background.remove(this.onlineText);
		if (null != this.clientLogo)
			background.remove(this.clientLogo);
		if (null != this.wikiLogo)
			background.remove(this.wikiLogo);
		if (null != this.playersLogo)
			background.remove(this.playersLogo);
		if (null != this.horizontalRule)
			background.remove(this.horizontalRule);
		if (null != this.bannerSticker)
			background.remove(this.bannerSticker);
		if (null != this.underConstruction)
			background.remove(this.underConstruction);
		if (null != this.comingSoonPlaceholder)
			background.remove(this.comingSoonPlaceholder);
		return background;
	}

    public JButton getServerName() {
		return new JButtonCopy(this.serverName).getCopy();
    }

    public JButton getRscText() {
		return new JButtonCopy(this.rscText).getCopy();
	}
}
