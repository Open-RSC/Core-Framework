package com.loader.openrsc.net.xml;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.elements.ArchiveBox;
import com.loader.openrsc.frame.elements.NewsBox;

import java.awt.*;

public class XMLReader
{
    private static Feed news;
    private static Feed archives;
    
    public static void init(final NewsBox newsBox) {
        try {
            XMLReader.news = getNews();
            XMLReader.archives = getArchivedNews();
            newsBox.getTitle().setText((XMLReader.news == null) ? "No news available" : XMLReader.news.getMessages().get(0).getTitle());
            final String newsText = (XMLReader.news == null) ? "-----" : XMLReader.news.getMessages().get(0).getCleanDesc();
            String[] split;
            for (int length = (split = newsText.split("<br>")).length, i = 0; i < length; ++i) {
                final String line = split[i];
                newsBox.append("<p style='color:#AAAAAA;margin:0;font-size:8px;font-family:arial;'> " + line + " </p>");
            }
            if (XMLReader.archives == null) {
                return;
            }
            int startY = 0;
            for (final FeedMessage msg : XMLReader.archives.getMessages()) {
                newsBox.add(new ArchiveBox(msg, new Rectangle(246, startY, 194, 39)));
                startY += 50;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Feed getArchivedNews() {
        final RSSFeedParser parser = new RSSFeedParser(Constants.rss_url); // Archived news RSS feed URL
        final Feed feed = parser.readFeed();
        return feed;
    }
    
    public static Feed getNews() {
        final RSSFeedParser parser = new RSSFeedParser(Constants.rss_url); // Current news RSS feed URL
        final Feed feed = parser.readFeed();
        return feed;
    }
}
