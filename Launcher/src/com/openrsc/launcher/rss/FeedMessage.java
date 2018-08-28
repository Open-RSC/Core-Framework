 package com.openrsc.launcher.rss;
 
 
 public class FeedMessage
 {
   String item;
   
   String title;
   String description;
   String link;
   String author;
   String guid;
   String pubDate;
   
   public String getTitle()
   {
     return this.title;
   }
   
   public String getPubDate() {
     return this.pubDate;
   }
   
   public void setItem(String item) {
     this.item = item;
   }
   
   public String getItem() {
     return this.item;
   }
   
   public String getSplitDate() {
     String[] parts = this.pubDate.split(" ");
     return "" + parts[2] + " " + parts[1] + " " + parts[3] + " " + parts[4] + "";
   }
   
   public void setPubDate(String date) {
     this.pubDate = date;
   }
   
   public void setTitle(String title) {
     this.title = title;
   }
   
   public String getDescription() {
     return this.description;
   }
   
   public String getCleanDesc() {
     return 
     
 
 
 
       this.description.replace("<p>", "\n").replace("</p>", "").replaceAll("\\<.*?\\>", "").replace("\n", "<br>").replace("&nbsp;", " ").replace("<br><br>", "<br>");
   }
   
   public void setDescription(String description) {
     this.description = description;
   }
   
   public String getLink() {
     return this.link;
   }
   
   public void setLink(String link) {
     this.link = link;
   }
   
   public String getAuthor() {
     return this.author;
   }
   
   public void setAuthor(String author) {
     this.author = author;
   }
   
   public String getGuid() {
     return this.guid;
   }
   
   public void setGuid(String guid) {
     this.guid = guid;
   }
   
   public String toString()
   {
     return "FeedMessage [title=" + this.title + ", description=" + this.description + ", link=" + this.link + ", author=" + this.author + ", guid=" + this.guid + "]";
   }
 }