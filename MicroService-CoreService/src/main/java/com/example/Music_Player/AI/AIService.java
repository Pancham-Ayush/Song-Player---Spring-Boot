
package com.example.Music_Player.AI;

import com.example.Music_Player.DTO.SONG_YT_DTO;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AIService {
    @Autowired
    @Qualifier("myMistralChatModel")
    ChatClient MistralAiChatModel;

    public boolean AISongVerification(String message) {
        res r = (res)this.MistralAiChatModel
                .prompt()
                .system("You are a verification assistant.\nGiven the input text containing title, description, artist/singer, and optionally a link,\ndecide whether the content represents any kind of music-related material such as\na song, music video, folklore performance, pop song , rock music, spiritual song or any type of beat.\nand if its a short or reel  its not a song\n\nRespond only with a single word:\n\"true\" if it is music-related,\n\"false\" if it is not.\n")
                .user(message)
                .call()
                .entity(res.class);
        return r.b();
    }

    public SONG_YT_DTO AiSongMapping(String message) {

        String prompt = "You are a music data extractor.\nGiven the following YouTube video details, extract:\n- songName: name/title of the song\n- artistName: singer, creator, or channel\n- description: a summary of the content be specific\n- genre: one of [music, song, bhajan, devotional, lofi, instrumental, podcast, other]\n= youtubeUrl: url only of youtube from the description\nIf data is missing, infer intelligently.\nInput:\n%s\nOutput in JSON format.\n".formatted(message);
        SONG_YT_DTO obj = (SONG_YT_DTO) this.MistralAiChatModel
                .prompt()
                .system("Extract structured information about the music content from the text provided.")
                .user(prompt)
                .call()
                .entity(SONG_YT_DTO.class);
        if (obj == null)
            return null;
        else {
            return obj;
        }
    }

    static record res(boolean b) {}
}
