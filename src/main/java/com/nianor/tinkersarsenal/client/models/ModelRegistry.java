package com.nianor.tinkersarsenal.client.models;

import com.nianor.tinkersarsenal.TinkersArsenal;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import static com.nianor.tinkersarsenal.TinkersArsenal.MODID;

public class ModelRegistry {
    public PartModel simpleBarrel;
    public PartModel simpleBolt;
    public PartModel simpleBoltHandle;

    public ModelRegistry() {
        try {
            simpleBarrel = new PartModel(new ResourceLocation(MODID, "models/item/dynamic/simplebarrel.obj"));
            simpleBolt = new PartModel(new ResourceLocation(MODID, "models/item/dynamic/simplebolt.obj"));
            simpleBoltHandle = new PartModel(new ResourceLocation(MODID, "models/item/dynamic/simplebolthandle.obj"));
        }
        catch (IOException e) {
            TinkersArsenal.logger.info("Error while registering models: {}", e);
        }
    }
}
