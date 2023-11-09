package com.hollingsworth.schematic.api.blockprints.download;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class DownloadResponse {
    public final String previewImage;
    public final String playerName;
    public final String structureName;
    public final String description;
    public final List<String> mods;
    public final String schematicLink;
    public final List<Tuple<ResourceLocation, Integer>> blockCounts;

    public DownloadResponse(JsonObject jsonObject) {
        mods = new ArrayList<>();
        blockCounts = new ArrayList<>();
        this.previewImage = jsonObject.get("smallPreviewImage").getAsString();
        this.playerName = jsonObject.get("playerName").getAsString();
        this.structureName = jsonObject.get("name").getAsString();
        this.description = jsonObject.get("description").getAsString();
        this.schematicLink = jsonObject.get("schematic").getAsString();
        for (var mod : jsonObject.get("mods").getAsJsonArray()) {
            mods.add(mod.getAsString());
        }

        for (var entry : jsonObject.get("blockCount").getAsJsonObject().entrySet()) {
            blockCounts.add(new Tuple<>(new ResourceLocation(entry.getKey()), entry.getValue().getAsInt()));
        }
    }
}
