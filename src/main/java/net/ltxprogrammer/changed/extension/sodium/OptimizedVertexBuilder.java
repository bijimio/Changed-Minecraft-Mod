package net.ltxprogrammer.changed.extension.sodium;

import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;

public class OptimizedVertexBuilder implements VertexConsumer {
    private final ChunkVertexEncoder.Vertex[] vertices;
    private final ChunkModelBuilder wrapped;
    private final Material material;
    private int index = 0;

    public OptimizedVertexBuilder(ChunkVertexEncoder.Vertex[] vertices, ChunkModelBuilder wrapped, Material material) {
        this.vertices = vertices;
        this.wrapped = wrapped;
        this.material = material;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        final var vert = vertices[index];
        vert.x = (float)x;
        vert.y = (float)y;
        vert.z = (float)z;
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        final var vert = vertices[index];
        vert.color = a << 24 | r << 16 | g << 8 | b;
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        final var vert = vertices[index];
        vert.u = u;
        vert.v = v;
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        final var vert = vertices[index];
        vert.light = u | v << 16;
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this;
    }

    @Override
    public void endVertex() {
        if (++index >= vertices.length) {
            final var vertexBuffer = wrapped.getVertexBuffer(ModelQuadFacing.UNASSIGNED);
            vertexBuffer.push(vertices, material);
            index = 0;
        }
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {

    }

    @Override
    public void unsetDefaultColor() {

    }
}
