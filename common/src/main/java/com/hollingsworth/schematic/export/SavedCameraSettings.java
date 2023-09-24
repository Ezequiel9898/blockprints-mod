package com.hollingsworth.schematic.export;

record SavedCameraSettings(float rotationX, float rotationY, float rotationZ, float offsetX, float offsetY,
        float zoom) {
    SavedCameraSettings() {
        this(0, 0, 0, 0, 0, 1);
    }
}
