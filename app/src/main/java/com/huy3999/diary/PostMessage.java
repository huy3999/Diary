package com.huy3999.diary;

import java.sql.Timestamp;

public class PostMessage {
    public PostMessage(int id, String auth, String content, String background, Timestamp timestamp) {
        this.id = id;
        this.auth = auth;
        this.content = content;
        this.background = background;
        this.timestamp = timestamp;
    }

    public  final int id;
    public  final String auth, content,background;
    public  final Timestamp timestamp;
}