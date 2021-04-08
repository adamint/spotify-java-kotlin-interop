package com.adamratzman;

import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyAppApi;
import com.adamratzman.spotify.javainterop.SpotifyContinuation;
import com.adamratzman.spotify.models.Album;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class SpotifyAlbumApiInterop {
    static SpotifyAppApi api;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpotifyApiBuilderKt.spotifyAppApi(Const.clientId, Const.clientSecret).build(true, new SpotifyContinuation<>() {
            @Override
            public void onSuccess(SpotifyAppApi spotifyAppApi) {
                api = spotifyAppApi;
                runAlbumSearch();
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        Thread.sleep(1000000);
    }

    public static void runAlbumSearch() {
        api.getAlbums().getAlbum("spotify:album:0b23AHutIA1BOW0u1dZ6wM", null, new SpotifyContinuation<>() {
            @Override
            public void onSuccess(Album album) {
                System.out.println("Album name is: " + album.getName());

                System.exit(0);
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
