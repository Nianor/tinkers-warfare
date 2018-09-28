//Made with Blockbench, by Nianor

package com.nianor.tinkersarsenal.client.models;

import com.nianor.tinkersarsenal.TinkersArsenal;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;

public class ModelProjectile extends ModelBase {

    //fields

    ModelRenderer e0;
    ModelRenderer e1;
    ModelRenderer e2;

    float caliber;

    float xMaxNeck;
    float xMaxTip;
    float xMaxBody;
    float xMinBody;
    float yMaxBody;
    float yMinBody;
    float zMaxBody;
    float zMinBody;


    public ModelProjectile() {
        this.textureWidth = 32;
        this.textureHeight = 32;

        this.e0 = new ModelRenderer(this, 0, 0);
        this.e0.addBox(0F, -2.5F, 3.5F, 5, 5, 4, 1.0F*caliber);
        this.e1 = new ModelRenderer(this, 0, 0);
        this.e1.addBox(xMaxNeck, -1.5F, 7.5F, 3, 3, 2, 1.0F*caliber);
        this.e2 = new ModelRenderer(this, 0, 0);
        this.e2.addBox(xMaxBody, -0.5F, 9.5F, 1, 1, 1, 1.0F*caliber);

    }


    public void render(Entity entity, float caliber, float neckLength, float tipLength, float f5) {

        float xMaxNeck = neckLength/3;
        float xMaxTip = tipLength/3;
        float xMaxBody = (caliber/2)/3;
        float xMinBody = 0;
        float yMaxBody = (caliber/2)/3;
        float yMinBody = -(caliber/2)/3;
        float zMaxBody = (caliber/2)/3;
        float zMinBody = -(caliber/2)/3;

        if (isTest) {TinkersArsenal.logger.info("Model commencing rendering");}
        this.e0.render(f5);
        this.e1.render(f5);
        this.e2.render(f5);

    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void setDimensions(float cal/*, float len*/) {
        caliber = (float)(cal/12.3);
    }
}