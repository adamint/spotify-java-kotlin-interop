package com.adamratzman;

import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyAppApi;
import com.adamratzman.spotify.models.PagingObject;
import com.adamratzman.spotify.models.SimpleAlbum;
import com.adamratzman.spotify.models.SpotifyCategory;
import kotlin.Unit;

public class SpotifyBrowseApiInterop {
    public static void main(String[] args) throws InterruptedException {
        SpotifyAppApi api = SpotifyApiBuilderKt.spotifyAppApi(Const.clientId, Const.clientSecret).buildRestAction(true).complete();
        PagingObject<SimpleAlbum> browseReleases = api.getBrowse().getNewReleasesRestAction(20, null, null).complete();
        for (SimpleAlbum album : browseReleases.getItems()) {
            System.out.println(album.getName());
        }
        api.getBrowse().getCategoryListRestAction(20, null, null, null).queue(throwable -> {
            throwable.printStackTrace();
            return Unit.INSTANCE;
        }, spotifyCategories -> {
            for (SpotifyCategory category : spotifyCategories.getItems()) System.out.println(category.getName());
            return Unit.INSTANCE;
        });

        Thread.sleep(3000);
    }
}
