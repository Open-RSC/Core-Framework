package com.rscdaemon.scripting.quest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.rscdaemon.scripting.ScriptError;
import com.rscdaemon.scripting.Skill;

public class QuestFactory
{
	private final class QuestFactoryContentHandler
		extends
			DefaultHandler
	{
		private final Map<Integer, Quest> quests = new TreeMap<Integer, Quest>();
		private List<QuestReward> rewards = new ArrayList<QuestReward>();
		private boolean _id, _name, _points;
		
		private int id, questPoints;
		private String name;
		
		@Override
	    public final void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts)
	    	throws
	    	SAXException
	    {
			switch(localName.toUpperCase())
			{
			case "ID":
				_id = true;
				break;
			case "NAME":
				_name = true;
				break;
			case "POINTS":
				_points = true;
				break;
			case "EXPERIENCE":
				rewards.add(new DefaultExperience(Skill.valueOf(atts.getValue("stat").toUpperCase()), Integer.parseInt(atts.getValue("amount"))));
				break;
			case "ITEM":
				rewards.add(new DefaultItem(Integer.parseInt(atts.getValue("id")), Integer.parseInt(atts.getValue("amount")), atts.getValue("message")));
				break;
			}
	    }
		
		@Override
		public final void endElement(String namespaceURI, String localName, String qualifiedName)
		{
			switch(localName.toUpperCase())
			{
				case "QUEST":
					quests.put(id, new DefaultQuest(id, name, questPoints, rewards.toArray(new QuestReward[rewards.size()])));
					rewards.clear();
					break;
			}
		}
		
		public void characters(char ch[], int start, int length) throws SAXException {
			 if(_id)
			 {
				 _id = false;
				 id = Integer.parseInt(new String(ch, start, length));
			 }
			 else if(_name)
			 {
				 _name = false;
				 name = new String(ch, start, length);
			 }
			 else if(_points)
			 {
				 _points = false;
				 questPoints = Integer.parseInt(new String(ch, start, length));
			 }
		}
	}
	
	private Map<Integer, Quest> quests;
	
	public QuestFactory(String configFile)
	{
		try(InputStream fis = new FileInputStream(configFile))
        {
			QuestFactoryContentHandler cfch = new QuestFactoryContentHandler();
			XMLReader parser = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");			
			parser.setContentHandler(cfch);
			parser.parse(new InputSource(fis));
			this.quests = cfch.quests;
		}
        catch (IOException | SAXException e)
        {
			throw (ScriptError)new ScriptError(null, "").initCause(e);
		}
	}
	
	public Quest getQuest(int id)
	{
		Quest quest = quests.get(id);
		if(quest != null)
		{
			quest = new DefaultQuest(quest);
		}
		return quest;
	}
}
