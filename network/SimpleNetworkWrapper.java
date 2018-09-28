package com.nianor.tinkersarsenal.network;

import com.nianor.tinkersarsenal.TinkersArsenal;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class SimpleNetworkWrapper {
    public static final net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TinkersArsenal.MODID);
}
