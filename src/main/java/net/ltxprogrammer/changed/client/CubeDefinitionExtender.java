package net.ltxprogrammer.changed.client;

import net.minecraft.core.Direction;

public interface CubeDefinitionExtender {
    void removeFaces(Direction... directions);
    void copyFaceUVStart(Direction from, Direction to);
}
