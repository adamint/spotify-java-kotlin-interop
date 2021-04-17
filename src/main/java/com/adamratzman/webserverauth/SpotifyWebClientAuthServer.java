package com.adamratzman.webserverauth;

import com.adamratzman.Const;
import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyClientApi;
import com.adamratzman.spotify.SpotifyScope;
import com.adamratzman.spotify.SpotifyUserAuthorization;
import com.adamratzman.spotify.endpoints.pub.TrackAttribute;
import com.adamratzman.spotify.endpoints.pub.TuneableTrackAttribute;
import com.adamratzman.spotify.models.PagingObject;
import com.adamratzman.spotify.models.RecommendationResponse;
import com.adamratzman.spotify.models.SimplePlaylist;
import com.adamratzman.spotify.utils.Market;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.port;

public class SpotifyWebClientAuthServer {
    private static final String redirectUri = "http://localhost/spotify-callback";
    private static final String authorizationUrl = SpotifyApiBuilderKt.getSpotifyAuthorizationUrl(
            new SpotifyScope[]{
                    SpotifyScope.PLAYLIST_MODIFY_PRIVATE,
                    SpotifyScope.PLAYLIST_MODIFY_PUBLIC
            },
            Const.clientId,
            redirectUri,
            false,
            true,
            null
    );

    public static void main(String[] args) {
        port(80);

        get("/", SpotifyWebClientAuthServer::handleHomeRequest, new HandlebarsTemplateEngine());
        get("/submit-playlist-for-modifications", SpotifyWebClientAuthServer::handleModifyPlaylistRequest, new HandlebarsTemplateEngine());
        get("/redirect-to-auth", SpotifyWebClientAuthServer::handleAuthRedirectRequest);

        get("/spotify-callback", SpotifyWebClientAuthServer::handleSpotifyCallbackRequest);
    }

    private static ModelAndView handleHomeRequest(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        Object apiObject = request.session().attribute("api");
        model.put("api", apiObject);
        return new ModelAndView(model, "index.hbs");
    }

    private static ModelAndView handleModifyPlaylistRequest(Request request, Response response) {
        String playlistName = request.queryParams("playlistName");
        String valenceLevelString = request.queryParams("valenceLevel");
        Map<String, Object> model = new HashMap<>();
        try {
            float valenceLevel = Float.parseFloat(valenceLevelString);
            SpotifyClientApi api = request.session().attribute("api");
            PagingObject<SimplePlaylist> clientPlaylists = api.getPlaylists().getClientPlaylistsRestAction(50, 0).complete();
            SimplePlaylist playlistToAddTo = clientPlaylists.stream().filter(simplePlaylist -> simplePlaylist.getName().equalsIgnoreCase(playlistName))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Couldn't find a playlist with that name."));
            playlistToAddTo.toFullPlaylistRestAction(Market.US).complete().getTracks().forEach(System.out::println);
            RecommendationResponse recommendationResponse = api.getBrowse().getTrackRecommendationsRestAction(
                    null,
                    Collections.singletonList("pop"),
                    null,
                    1,
                    null,
                    Collections.singletonList(TrackAttribute.Companion.create(
                            TuneableTrackAttribute.Valence.INSTANCE,
                            valenceLevel
                    )),
                    Collections.emptyList(),
                    Collections.emptyList()
            ).complete();
            api.getPlaylists().addTracksToClientPlaylistRestAction(
                    playlistToAddTo.getId(),
                    new String[] { recommendationResponse.getTracks().get(0).getId() },
                    null
            ).complete();
            model.put("success", true);
            model.put("playlist", playlistToAddTo);
            model.put("track", recommendationResponse.getTracks().get(0));
        } catch (Exception exception) { // i'm lazy
            exception.printStackTrace();
            model.put("success", false);
            model.put("message", exception.getMessage());
        }

        System.out.println(request.queryParams());
        return new ModelAndView(model, "modification.hbs");
    }

    private static Object handleAuthRedirectRequest(Request request, Response response) {
        response.redirect(authorizationUrl);
        return null;
    }

    private static Object handleSpotifyCallbackRequest(Request request, Response response) {
        if (request.queryParams("code") == null) response.redirect(authorizationUrl);
        else {
            try {
                String code = request.queryParams("code");
                SpotifyClientApi spotifyClientApi = SpotifyApiBuilderKt.spotifyClientApi(Const.clientId,
                        Const.clientSecret,
                        redirectUri,
                        new SpotifyUserAuthorization(code, null, null, null, null),
                        spotifyApiOptions -> null).buildRestAction(true).complete();

                request.session().attribute("api", spotifyClientApi);
                response.redirect("/");
            } catch (Exception e) {
                response.redirect(authorizationUrl);
            }
        }
        return null;
    }
}
