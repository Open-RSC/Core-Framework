 package com.openrsc.launcher.rss;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import javax.xml.stream.XMLEventReader;
 import javax.xml.stream.XMLInputFactory;
 import javax.xml.stream.XMLStreamException;
 import javax.xml.stream.events.Characters;
 import javax.xml.stream.events.XMLEvent;
 
 public class RSSFeedParser
 {
   static final String TITLE = "title";
   static final String DESCRIPTION = "description";
   static final String CHANNEL = "channel";
   static final String LANGUAGE = "language";
   static final String COPYRIGHT = "copyright";
   static final String LINK = "link";
   static final String AUTHOR = "author";
   static final String ITEM = "item";
   static final String PUB_DATE = "pubDate";
   static final String GUID = "guid";
   final URL url;
   
   public RSSFeedParser(String feedUrl)
   {
     try
     {
       this.url = new URL(feedUrl);
     } catch (MalformedURLException e) {
       throw new RuntimeException(e);
     }
   }
   
   public Feed readFeed() {
     Feed feed = null;
     try {
       boolean isFeedHeader = true;
       
       String description = "";
       String title = "";
       String link = "";
       String language = "";
       String copyright = "";
       String author = "";
       String pubdate = "";
       String guid = "";
       
       XMLInputFactory inputFactory = XMLInputFactory.newInstance();
       java.io.InputStream in = read();
       XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
       
       while (eventReader.hasNext()) {
         XMLEvent event = eventReader.nextEvent();
         if (event.isStartElement()) {
           String localPart = event.asStartElement().getName().getLocalPart();
           switch (localPart) {
           case "item": 
             if (isFeedHeader) {
               isFeedHeader = false;
               feed = new Feed(title, link, description, language, copyright, pubdate);
             }
             event = eventReader.nextEvent();
             break;
           case "title": 
             title = getCharacterData(event, eventReader);
             break;
           case "description": 
             description = getCharacterData(event, eventReader);
             break;
           case "link": 
             link = getCharacterData(event, eventReader);
             break;
           case "guid": 
             guid = getCharacterData(event, eventReader);
             break;
           case "language": 
             language = getCharacterData(event, eventReader);
             break;
           case "author": 
             author = getCharacterData(event, eventReader);
             break;
           case "pubDate": 
             pubdate = getCharacterData(event, eventReader);
             break;
           case "copyright": 
             copyright = getCharacterData(event, eventReader);
           }
         }
         else if ((event.isEndElement()) && 
           (event.asEndElement().getName().getLocalPart() == "item")) {
           FeedMessage message = new FeedMessage();
           message.setAuthor(author);
           message.setDescription(description);
           message.setGuid(guid);
           message.setLink(link);
           message.setPubDate(pubdate);
           message.setTitle(title);
           feed.getMessages().add(message);
           event = eventReader.nextEvent();
         }
       }
     }
     catch (XMLStreamException e)
     {
       e.printStackTrace();
       return null;
     }
     return feed;
   }
   
   private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
     String result = "";
     event = eventReader.nextEvent();
     if ((event instanceof Characters)) {
       result = event.asCharacters().getData();
     }
     return result;
   }
   
   private java.io.InputStream read() {
     try {
       return this.url.openStream();
     } catch (java.io.IOException e) {
       throw new RuntimeException(e);
     }
   }
 }