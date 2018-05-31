package org.rscemulation.server.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.rscemulation.server.Config;

public class Captcha
{
	private static Vector<Pair<String, BufferedImage>> captchas = null;
	
	private final static Random rand = new Random(System.currentTimeMillis());
	
	static
	{
		try(Connection connection = DriverManager.getConnection("jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS))
		{
			try(Statement statement = connection.createStatement())
			{
				ResultSet result = statement.executeQuery("SELECT * FROM `"+Config.TOOLS_DB_NAME+"`.`captcha`;");
				captchas = new Vector<Pair<String, BufferedImage>>();
				while (result.next())
				{
					String word = result.getString("string");
					Blob binary = result.getBlob("captcha");
					BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(new ByteArrayInputStream(binary.getBytes(1L, (int)binary.length()))));
					captchas.add(new Pair<String, BufferedImage>(word, image));
				}
			}
		}
		catch(IOException | SQLException e)
		{
			throw (ExceptionInInitializerError)new ExceptionInInitializerError().initCause(e);
		}
	}


	public static synchronized Pair<String, BufferedImage> getCaptcha()
	{
		if (captchas.isEmpty())
		{
			return null;
		}
		return captchas.get(rand.nextInt(captchas.size()));
	}
}