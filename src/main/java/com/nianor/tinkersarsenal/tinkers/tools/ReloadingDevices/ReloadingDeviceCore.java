package com.nianor.tinkersarsenal.tinkers.tools.ReloadingDevices;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;

import java.util.List;

public abstract class ReloadingDeviceCore extends TinkerToolCore {
    public ReloadingDeviceCore(PartMaterialType... requiredComponents)
    {
        super(requiredComponents);
        // super(new PartMaterialType(TinkersArsenal.YOYO_CORD, YoyoMaterialTypes.CORD), new PartMaterialType(TinkersYoyos.YOYO_BODY, YoyoMaterialTypes.BODY), new PartMaterialType(TinkersYoyos.YOYO_BODY, YoyoMaterialTypes.BODY), new PartMaterialType(TinkersYoyos.YOYO_AXLE, YoyoMaterialTypes.AXLE));
        addCategory(Category.NO_MELEE, Category.TOOL);
    }
    @Override
    protected abstract ToolNBT buildTagData(List<Material> materials);

    @Override
    public float damagePotential() {
        return 0;
    }

    @Override
    public double attackSpeed() {return 1; }
}
