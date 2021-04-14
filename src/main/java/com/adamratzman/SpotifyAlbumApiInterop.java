package com.adamratzman;

import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyAppApi;
import com.adamratzman.spotify.SpotifyClientApi;
import com.adamratzman.spotify.SpotifyUserAuthorization;
import com.adamratzman.spotify.models.Album;

import java.util.concurrent.ExecutionException;

public class SpotifyAlbumApiInterop {
    static SpotifyAppApi api;

    public static void main(String[] args) {
        SpotifyClientApi api = SpotifyApiBuilderKt.spotifyClientApi(Const.clientId, Const.clientSecret, null,
                new SpotifyUserAuthorization(null, "", null, null, null), spotifyApiOptions -> null).buildRestAction(true).complete();
        Album album = api.getAlbums().getAlbumRestAction("spotify:album:0b23AHutIA1BOW0u1dZ6wM", null).complete();
        System.out.println("Album name is: " + album.getName());
    }
}
