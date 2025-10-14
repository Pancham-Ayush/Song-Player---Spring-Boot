//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.Music_Player.Model;

public class YoutubeVideo {
    private String title;
    private String url;
    private String thumbnailUrl;
    private String channelName;
    private String description;

    public String getTitle() {
        return this.title;
    }

    public String getUrl() {
        return this.url;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setChannelName(final String channelName) {
        this.channelName = channelName;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public YoutubeVideo(final String title, final String url, final String thumbnailUrl, final String channelName, final String description) {
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.channelName = channelName;
        this.description = description;
    }

    public YoutubeVideo() {
    }

    public String toString() {
        String var10000 = this.getTitle();
        return "YoutubeVideo(title=" + var10000 + ", url=" + this.getUrl() + ", thumbnailUrl=" + this.getThumbnailUrl() + ", channelName=" + this.getChannelName() + ", description=" + this.getDescription() + ")";
    }
}
