package com.adamratzman;

import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyAppApi;
import com.adamratzman.spotify.models.Album;

import java.util.concurrent.ExecutionException;

public class SpotifyAlbumApiInterop {
    static SpotifyAppApi api;

    public static void main(String[] args) {
        SpotifyAppApi api = SpotifyApiBuilderKt.spotifyAppApi(Const.clientId, Const.clientSecret).buildRestAction(true).complete();
        Album album = api.getAlbums().getAlbumRestAction("spotify:album:0b23AHutIA1BOW0u1dZ6wM", null).complete();
        System.out.println("Album name is: " + album.getName());
    }
}
