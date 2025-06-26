package net.ltxprogrammer.changed.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ResourceUtil {
    public interface ResourceConsumer<T> {
        void accept(T builder, ResourceLocation fullResourceName, ResourceLocation registryName, Resource resource);
    }

    public interface JSONResourceConsumer<T> {
        void accept(T builder, ResourceLocation fullResourceName, ResourceLocation registryName, JsonObject root) throws Exception;
    }

    public interface JSONFileConsumer<T> {
        void accept(T builder, ResourceLocation fullResourceName, JsonObject root) throws Exception;
    }

    public static <T> T processResources(T builder, @NotNull ResourceManager resources, @NotNull String path, @NotNull String extension, ResourceConsumer<T> consumer) {
        resources.listResources(path, filename -> ResourceLocation.isValidResourceLocation(filename.getPath()) && filename.getPath().endsWith(extension))
                .forEach((filename, resource) -> {
                    ResourceLocation registryName = ResourceLocation.fromNamespaceAndPath(filename.getNamespace(),
                            Path.of(path).relativize(Path.of(filename.getPath())).toString()
                                    .replace(extension, "")
                                    .replace('\\', '/'));

                    consumer.accept(builder, filename, registryName, resource);
                });

        return builder;
    }

    public static <T> T processJSONResources(T builder, @NotNull ResourceManager resources, @NotNull String path, JSONResourceConsumer<T> consumer, BiConsumer<Exception, ResourceLocation> onException) {
        resources.listResources(path, filename -> ResourceLocation.isValidResourceLocation(filename.getPath()) && filename.getPath().endsWith(".json"))
                .forEach((filename, resource) -> {
                    ResourceLocation registryName = ResourceLocation.fromNamespaceAndPath(filename.getNamespace(),
                            Path.of(path).relativize(Path.of(filename.getPath())).toString()
                                    .replace(".json", "")
                                    .replace('\\', '/'));


                    try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                        consumer.accept(builder, filename, registryName, JsonParser.parseReader(reader).getAsJsonObject());
                    } catch (Exception e) {
                        onException.accept(e, filename);
                    }
                });

        return builder;
    }

    public static <T> T processJSONFiles(T builder, @NotNull ResourceManager resources, @NotNull String fullName, JSONFileConsumer<T> consumer, BiConsumer<Exception, ResourceLocation> onException) {
        resources.getNamespaces().stream().map(namespace -> ResourceLocation.fromNamespaceAndPath(namespace, fullName))
                .map(filename -> Pair.of(filename, resources.getResource(filename).orElse(null)))
                .filter(pair -> pair.getSecond() != null)
                .forEach(pair -> {
                    try (Reader reader = new InputStreamReader(pair.getSecond().open(), StandardCharsets.UTF_8)) {
                        consumer.accept(builder, pair.getFirst(), JsonParser.parseReader(reader).getAsJsonObject());
                    } catch (Exception e) {
                        onException.accept(e, pair.getFirst());
                    }
                });

        return builder;
    }
}
