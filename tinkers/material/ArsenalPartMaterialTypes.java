package com.nianor.tinkersarsenal.tinkers.material;

import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;

public class ArsenalPartMaterialTypes {
    public static PartMaterialType core(IToolPart part ) {
        return new PartMaterialType(part, ArsenalMaterialTypes.CORE);
    }
    public static PartMaterialType face(IToolPart part ) {
        return new PartMaterialType(part, ArsenalMaterialTypes.FACE);
    }
    public static PartMaterialType mech(IToolPart part ) {
        return new PartMaterialType(part, ArsenalMaterialTypes.MECH);
    }
    public static PartMaterialType powder(IToolPart part) {
        return new PartMaterialType(part, ArsenalMaterialTypes.POWDER);
    }

}
