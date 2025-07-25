package net.ltxprogrammer.changed.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.Set;

public interface CubeExtender {
    Vector3f getVisualMin();
    Vector3f getVisualMax();
    UVPair getUV(Vector3f surfacePoint);
    void removeSides(Set<Direction> directions);
    void copyUVStarts(Set<Pair<Direction, Direction>> directions);
    ModelPart.Polygon[] getPolygons();
    void copyPolygonsFrom(ModelPart.Cube cube);
    void extendCube(float x, float y, float z);
}
