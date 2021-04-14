package com.adamratzman.webserverauth;

import com.adamratzman.Const;
import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyClientApi;
import com.adamratzman.spotify.SpotifyScope;
import com.adamratzman.spotify.SpotifyUserAuthorization;
import com.adamratzman.spotify.models.PagingObject;
import com.adamratzman.spotify.models.SimplePlaylist;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.port;

public class SpotifyWebClientAuthServer {
    private static final String redirectUri = "http://localhost/spotify-callback";
    private static final String authorizationUrl = SpotifyApiBuilderKt.getSpotifyAuthorizationUrl(
            new SpotifyScope[]{SpotifyScope.PLAYLIST_READ_PRIVATE, SpotifyScope.PLAYLIST_MODIFY_PRIVATE},
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

        } catch (Exception ignored) { // i'm lazy
            model.put("success", false);
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
