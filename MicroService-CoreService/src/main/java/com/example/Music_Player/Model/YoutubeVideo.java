//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.Music_Player.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class YoutubeVideo {
    private String title;
    private String url;
    private String thumbnailUrl;
    private String channelName;
    private String description;
    public String toString() {
        String var10000 = this.getTitle();
        return "YoutubeVideo(title=" + var10000 + ", url=" + this.getUrl() + ", thumbnailUrl=" + this.getThumbnailUrl() + ", channelName=" + this.getChannelName() + ", description=" + this.getDescription() + ")";
    }
}
