package net.ltxprogrammer.changed.client.latexparticles;

import net.minecraft.client.model.geom.builders.UVPair;
import org.joml.Vector3f;

public record SurfacePoint(Vector3f normal, Vector3f tangent, Vector3f position, UVPair uv) {
}
