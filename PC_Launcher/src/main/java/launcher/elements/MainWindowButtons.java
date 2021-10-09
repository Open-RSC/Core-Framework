package launcher.elements;

import launcher.Settings;
import launcher.Utils.Defaults;

import javax.swing.*;
import java.awt.*;

public class MainWindowButtons {
	public JLabel version_number;
	public LinkButton advanced_settings_button;
	public LinkButton openrsc_logo;
	public JLabel robotIcon;
	public LaunchButton fleaCircusEgg;
	public CheckboxButton robotCheckbox;
	public LinkButton chat_header;
	public LinkButton libera;
	public LinkButton discord;
	public LinkButton cockroach;
	public LinkButton bugs_header;
	public ControlButton main_minimize;
	public ControlButton main_close;
	public ControlButton delete_cache;
	public LinkButton bots_header;
	public LinkButton question_mark;
	public LinkButton about_our_servers;
	public LinkButton gear;
	public LinkButton client_settings;
    public LinkButton forums_header;
	public LinkButton reddit;
	public LinkButton openrsc_forums;

	public JLabel addAll(JLabel background) {
		if (null != version_number)
			background.remove(version_number);
		if (null != advanced_settings_button)
			background.remove(advanced_settings_button);

		background.add(delete_cache);
		background.add(robotIcon);
		background.add(fleaCircusEgg);
		background.add(robotCheckbox);
		background.add(bots_header);
		background.add(question_mark);
		background.add(about_our_servers);
		background.add(gear);
		background.add(client_settings);
		if (Settings.undecoratedWindow) {
			background.add(main_minimize);
			background.add(main_close);
		}
		return background;
	}

	public JLabel removeAll(JLabel background) {
		background.add(version_number);
		background.add(advanced_settings_button);
		if (null != delete_cache)
			background.remove(delete_cache);
		if (null != fleaCircusEgg)
			background.remove(fleaCircusEgg);
		if (null != openrsc_logo)
			background.remove(openrsc_logo);
		if (null != bots_header)
			background.remove(bots_header);
		if (null != robotIcon)
			background.remove(robotIcon);
		if (null != robotCheckbox)
			background.remove(robotCheckbox);
		if (null != question_mark)
			background.remove(question_mark);
		if (null != about_our_servers)
			background.remove(about_our_servers);
		if (null != gear)
			background.remove(gear);
		if (null != client_settings)
			background.remove(client_settings);
		if (null != main_minimize)
			background.remove(main_minimize);
		if (null != main_close)
			background.remove(main_close);
		return background;
	}

	public JLabel addConstantUIElements(JLabel background) {
		background.add(chat_header);
		background.add(libera);
		background.add(discord);
		background.add(forums_header);
		background.add(openrsc_forums);
		background.add(reddit);
		background.add(bugs_header);
		background.add(cockroach);
		return background;
	}
}
