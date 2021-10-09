package launcher.elements;

import launcher.Utils.Utils;

import javax.swing.*;

public class ClientSettingsCard {
	public JComboBox clientChooser;
	public LaunchButton logo;
	public JLabel clientLogoSmall;
	public LinkButton clientLogoLarge;
	public JLabel[] explanationTexts;
	public boolean isPrerelease;
	public JLabel constructionLogo;

	public String[] clientNames;
	public int activeExplanation;

	public static final String RSCPLUS = "RSC+";
	public static final String WINRUNE = "WinRune";
	public static final String WEBCLIENT = "Web Client";
	public static final String OPENRSC = "Open RSC Client";
	public static final String MUD38 = "Mudclient 38";
	public static final String IDLERSC = "IdleRSC";
	public static final String RSCTIMES = "RSCx"; // RSC×
	public static final String APOSBOT = "APOSbot";

	public JLabel addAll(JLabel to) {
		to.add(clientChooser);
		to.add(clientLogoLarge);
		to.add(explanationTexts[activeExplanation]);
		to.add(logo);
		to.setComponentZOrder(logo, 5);
		to.add(clientLogoSmall);
		to.setComponentZOrder(clientLogoSmall, 3);
		if (isPrerelease) {
			to.add(constructionLogo);
			to.setComponentZOrder(constructionLogo, 3);
		}
		return to;
	}

	public JLabel removeAll(JLabel from) {
		if (null != clientChooser)
			from.remove(clientChooser);
		if (null != clientLogoSmall)
			from.remove(clientLogoSmall);
		if (null != clientLogoLarge)
			from.remove(clientLogoLarge);
		if (null != explanationTexts[activeExplanation])
			from.remove(explanationTexts[activeExplanation]);
		if (null != logo)
			from.remove(logo);
		if (null != constructionLogo)
			from.remove(constructionLogo);
		return from;
	}
}
