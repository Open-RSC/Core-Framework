 package com.openrsc.launcher.rss;
 
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 public class Feed
 {
   final String title;
   final String link;
   final String description;
   final String language;
   final String copyright;
   final String pubDate;
   final List<FeedMessage> entries = new ArrayList();
   
   public Feed(String title, String link, String description, String language, String copyright, String pubDate) {
     this.title = title;
     this.link = link;
     this.description = description;
     this.language = language;
     this.copyright = copyright;
     this.pubDate = pubDate;
   }
   
   public List<FeedMessage> getMessages() {
     return this.entries;
   }
   
   public String getTitle() {
     return this.title;
   }
   
   public String getLink() {
     return this.link;
   }
   
   public String getDescription() {
     return this.description;
   }
   
   public String getLanguage() {
     return this.language;
   }
   
   public String getCopyright() {
     return this.copyright;
   }
   
   public String getPubDate() {
     return this.pubDate;
   }
   
   public String toString()
   {
     return "Feed [copyright=" + this.copyright + ", description=" + this.description + ", language=" + this.language + ", link=" + this.link + ", pubDate=" + this.pubDate + ", title=" + this.title + "]";
   }
 }