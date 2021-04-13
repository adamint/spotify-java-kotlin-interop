package com.adamratzman.webserverauth;

import com.adamratzman.Const;
import com.adamratzman.spotify.SpotifyApiBuilderKt;
import com.adamratzman.spotify.SpotifyClientApi;
import com.adamratzman.spotify.SpotifyScope;
import com.adamratzman.spotify.SpotifyUserAuthorization;
import com.adamratzman.spotify.javainterop.SpotifyContinuation;
import com.adamratzman.spotify.models.PagingObject;
import com.adamratzman.spotify.models.SimplePlaylist;
import org.jetbrains.annotations.NotNull;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static spark.Spark.get;
import static spark.Spark.port;

public class SpotifyWebClientAuthServer {
    private static String redirectUri = "http://localhost/spotify-callback";
    private static String authorizationUrl = SpotifyApiBuilderKt.getSpotifyAuthorizationUrl(
            new SpotifyScope[]{SpotifyScope.PLAYLIST_READ_PRIVATE, SpotifyScope.PLAYLIST_MODIFY_PRIVATE},
            Const.clientId,
            redirectUri,
            false,
            true,
            null
    );

   /* public static void main(String[] args) {
        port(80);

        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            Object apiObject = request.session().attribute("api");
            model.put("api", apiObject);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/submit-playlist-for-modifications", (request, response) -> {
            String playlistName = request.queryParams("playlistName");
            String valenceLevelString = request.queryParams("valenceLevel");
            Map<String, Object> model = new HashMap<>();
            try {
                float valenceLevel = Float.parseFloat(valenceLevelString);
                SpotifyClientApi api = request.session().attribute("api");
                AtomicBoolean finished = new AtomicBoolean(false);
                api.getPlaylists().getClientPlaylists(50, 0, new SpotifyContinuation<>() {
                    @Override
                    public void onSuccess(PagingObject<SimplePlaylist> simplePlaylists) {

                    }

                    @Override
                    public void onFailure(@NotNull Throwable throwable) {
                        throwable.printStackTrace();
                        finished.set(true);
                    }
                });
                while (!finished.get()) {
                }

            } catch (Exception ignored) { // i'm lazy
                model.put("success", false);
            }

            System.out.println(request.queryParams());
            return new ModelAndView(model, "modification.hbs");
        }, new HandlebarsTemplateEngine());

        get("/redirect-to-auth", (request, response) -> {
            response.redirect(authorizationUrl);
            return null;
        });

        get("/spotify-callback", (request, response) -> {
            if (request.queryParams("code") == null) response.redirect(authorizationUrl);
            else {
                AtomicBoolean loaded = new AtomicBoolean(false);
                AtomicBoolean success = new AtomicBoolean(false);
                String code = request.queryParams("code");
                SpotifyApiBuilderKt.spotifyClientApi(Const.clientId,
                        Const.clientSecret,
                        redirectUri,
                        new SpotifyUserAuthorization(code, null, null, null, null),
                        spotifyApiOptions -> null).build(true, new SpotifyContinuation<>() {
                    @Override
                    public void onSuccess(SpotifyClientApi spotifyClientApi) {
                        request.session().attribute("api", spotifyClientApi);
                        loaded.set(true);
                        success.set(true);
                    }

                    @Override
                    public void onFailure(@NotNull Throwable throwable) {
                        loaded.set(true);
                        success.set(false);
                    }
                });
                while (!loaded.get()) {
                }
                if (success.get()) response.redirect("/");
                else response.redirect(redirectUri);
            }
            return null;
        });
    }*/
}
