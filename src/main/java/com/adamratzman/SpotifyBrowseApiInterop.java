package com.adamratzman;

import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyAppApi;
import com.adamratzman.spotify.javainterop.SpotifyContinuation;
import com.adamratzman.spotify.models.PagingObject;
import com.adamratzman.spotify.models.SimpleAlbum;
import org.jetbrains.annotations.NotNull;

public class SpotifyBrowseApiInterop {
    static SpotifyAppApi api;

    public static void main(String[] args) throws InterruptedException {
        SpotifyApiBuilderKt.spotifyAppApi(Const.clientId, Const.clientSecret).build(true, new SpotifyContinuation<>() {
            @Override
            public void onSuccess(SpotifyAppApi spotifyAppApi) {
                api = spotifyAppApi;

                api.getBrowse().getNewReleases(20, null, null, new SpotifyContinuation<>() {
                    @Override
                    public void onSuccess(PagingObject<SimpleAlbum> simpleAlbums) {
                            for (SimpleAlbum album : simpleAlbums) {
                                System.out.println(album.getName());
                            }
                    }

                    @Override
                    public void onFailure(@NotNull Throwable throwable) {

                    }
                });
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        Thread.sleep(1000000);
    }
}
