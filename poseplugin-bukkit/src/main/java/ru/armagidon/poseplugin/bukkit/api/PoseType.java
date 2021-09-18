package ru.armagidon.poseplugin.bukkit.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.Pose;
import ru.armagidon.poseplugin.api.utility.PropertyMap;

@SuppressWarnings("SpellCheckingInspection")
public enum PoseType implements Pose<Player> {
    SITTING {
        @Override
        public void apply(@NotNull Player player) {

        }

        @Override
        public void cancel(@NotNull Player player) {

        }

        @Override
        public @NotNull PropertyMap getPropertyMap() {
            return null; // ???
        }
    };

    // TO - DO:
    //    STANDING("stand"),
    //    SITTING("sit"),
    //    LYING("lay"),
    //    CRAWLING("crawl"),
    //    PRAYING("praying"),
    //    WAVING("wave"),
    //    HANDSHAKING("handshake"),
    //    POINTING("point"),
    //    CLAPPING("clap"),
    //    SPINJITSU("spinjitsu");

    @Override
    public abstract void apply(final @NotNull Player player);

    @Override
    public abstract void cancel(final @NotNull Player player);

    @Override
    public abstract @NotNull PropertyMap getPropertyMap();
}