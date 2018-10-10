package com.loader.openrsc.net.xml;

public class FeedMessage {
    String item;
    String title;
    String description;
    String link;
    String author;
    String guid;
    String pubDate;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getPubDate() {
        return this.pubDate;
    }

    public void setPubDate(final String date) {
        this.pubDate = date;
    }

    public String getItem() {
        return this.item;
    }

    public void setItem(final String item) {
        this.item = item;
    }

    public String getSplitDate() {
        final String[] parts = this.pubDate.split(" ");
        return parts[2] + " " + parts[1] + " " + parts[3] + " " + parts[4];
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getCleanDesc() {
        return this.description.replace("<p>", "\n").replace("</p>", "").replaceAll("\\<.*?\\>", "").replace("\n", "<br>").replace("&nbsp;", " ").replace("<br><br>", "<br>");
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "FeedMessage [title=" + this.title + ", description=" + this.description + ", link=" + this.link + ", author=" + this.author + ", guid=" + this.guid + "]";
    }
}
