package com.loader.openrsc.net.xml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSFeedParser {
    static final String TITLE = "title";
    static final String COMMENTS = "comments";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String CREATOR = "creator";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";
    static final String DESCRIPTION = "description";
    final URL url;

    public RSSFeedParser(final String feedUrl) {
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Feed readFeed() {
        Feed feed = null;
        try {
            boolean isFeedHeader = true;
            String creator = "";
            String title = "";
            String link = "";
            String comments = "";
            String author = "";
            String pubdate = "";
            String guid = "";
            String description = "";
            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            final InputStream in = this.read();
            final XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName().getLocalPart();
                    switch (localPart) {
                        case DESCRIPTION: {
                            description = this.getCharacterData(event, eventReader);
                            continue;
                        }
                        case AUTHOR: {
                            author = this.getCharacterData(event, eventReader);
                            continue;
                        }
                        case COMMENTS: {
                            comments = this.getCharacterData(event, eventReader);
                            continue;
                        }
                        case PUB_DATE: {
                            pubdate = this.getCharacterData(event, eventReader);
                            continue;
                        }
                        case GUID: {
                            guid = this.getCharacterData(event, eventReader);
                            continue;
                        }
                        case ITEM: {
                            if (isFeedHeader) {
                                isFeedHeader = false;
                                feed = new Feed(title, link, author, comments, creator, pubdate);
                            }
                            event = eventReader.nextEvent();
                            continue;
                        }
                        case LINK: {
                            link = this.getCharacterData(event, eventReader);
                            continue;
                        }
                        case TITLE: {
                            title = this.getCharacterData(event, eventReader);
                            continue;
                        }
                        case CREATOR: {
                            creator = this.getCharacterData(event, eventReader);
                            continue;
                        }
                    }
                } else {
                    if (!event.isEndElement() || event.asEndElement().getName().getLocalPart() != (ITEM)) {
                        continue;
                    }
                    final FeedMessage message = new FeedMessage();
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
        } catch (XMLStreamException e) {
            return null;
        }
        return feed;
    }

    private String getCharacterData(XMLEvent event, final XMLEventReader eventReader) throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }

    private InputStream read() {
        try {
            return this.url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
