package net.ltxprogrammer.changed.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OnlineResource extends Resource {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public OnlineResource(PackResources packResources, URI onlineLocation) {
        super(packResources, () -> {
            HttpRequest request = HttpRequest.newBuilder(onlineLocation).GET().build();

            try {
                return CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream()).body();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    public static Resource of(PackResources packResources, URI uri) {
        return new OnlineResource(packResources, uri);
    }
}
