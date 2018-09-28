package com.nianor.tinkersarsenal.client.models;

import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class PartModel {
    public float[] xCoords;
    public float[] yCoords;
    public float[] zCoords;
    public int[] faceList;
    private int numberOfCoords;
    private int numberOfFaces=0;

    public PartModel(ResourceLocation file) throws IOException {
        InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(file).getInputStream();
        char[] buf = new char[2048];
        Reader r = new InputStreamReader(stream, "UTF-8");
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = r.read(buf);
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }
        String[] fileList = s.toString().split("\n");
        int i=0;
        int coordNum = 0;
        String line;
        String[] lineSplit;
        Tessellator tessellator = ClientUtils.tes();
        BufferBuilder worldrenderer = ClientUtils.tes().getBuffer();
        System.out.println("File length: "+ fileList.length);
        while(i<fileList.length) {
            line = fileList[i];
            lineSplit = line.split(" ");
            System.out.println("Line: " + line);
            if (lineSplit[0].equals("v")) {
                coordNum += 1;
            }
            if(lineSplit[0].equals("f")) {
                numberOfFaces+=1;
            }
            i+=1;
        }
        numberOfCoords=coordNum;
        coordNum=0;
        xCoords = new float [numberOfCoords];
        yCoords = new float [numberOfCoords];
        zCoords = new float [numberOfCoords];
        faceList = new int [numberOfFaces*3];
        i=0;
        int f=0;
        while(i<fileList.length) {
            line=fileList[i];
            lineSplit = line.split(" ");
            if (lineSplit[0].equals("v")){
                xCoords[coordNum]=(Float.valueOf(lineSplit[1]));
                yCoords[coordNum]=(Float.valueOf(lineSplit[2]));
                zCoords[coordNum]=(Float.valueOf(lineSplit[3]));
                coordNum +=1;
            }
            else if(lineSplit[0].equals("f")) {
                System.out.println("Registering face.");
                faceList[f] = Integer.parseInt(lineSplit[3].split("/")[0]) - 1;
                faceList[f + 1] = Integer.parseInt(lineSplit[2].split("/")[0]) - 1;
                faceList[f + 2] = Integer.parseInt(lineSplit[1].split("/")[0]) - 1;
                f+=3;
                System.out.println(faceList);
            }
            i+=1;
        }
    }
}


