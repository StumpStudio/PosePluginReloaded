package ru.armagidon.poseplugin.api.pose;

public enum PoseType
{
    STANDING("stand"),
    SITTING("sit"),
    LYING("lay"),
    CRAWLING("crawl"),
    PRAYING("praying"),
    WAVING("wave"),
    HANDSHAKING("handshake"),
    POINTING("point"),
    CLAPPING("clap"),
    SPINJITSU("spinjitsu");

    private final String rawName;

    PoseType(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return rawName;
    }
}
