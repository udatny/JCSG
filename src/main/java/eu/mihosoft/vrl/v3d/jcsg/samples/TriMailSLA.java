/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d.jcsg.samples;

import eu.mihosoft.vrl.v3d.jcsg.CSG;
import eu.mihosoft.vrl.v3d.jcsg.FileUtil;
import eu.mihosoft.vrl.v3d.jcsg.Transform;
import static eu.mihosoft.vrl.v3d.jcsg.Transform.unity;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class TriMailSLA {

    public CSG toCSG(int numX, int numY) {

        final int numEdges = 3;

        PolyMailTile tile = new PolyMailTile().setNumEdges(numEdges).
                setRadius(10).
                setPinThickness(1.3).
                setPinLength(1.2).
                setHingeHoleScale(1.1).
                setConeLength(1.8).
                setJointRadius(1.1);

        double hingeHoleScale = tile.getHingeHoleScale();
        double radius = tile.getRadius();
        double side = tile.getSideLength();

        double pinOffset = tile.getPinLength()
                - (tile.getJointRadius() * hingeHoleScale
                - tile.getJointRadius());

        CSG malePart = tile.setMale().toCSG().transformed(
                unity().rotZ(360.0 / numEdges * 0.75));
        CSG femalePart = tile.setFemale().toCSG().transformed(
                unity().rotZ(360.0 / numEdges * 0.25));

        femalePart = femalePart.transformed(unity().translate(0, radius / 2, 0));

        double xOffset = Math.acos(Math.toRadians(30)) * pinOffset;
        double yOffset = Math.asin(Math.toRadians(30)) * pinOffset;

        femalePart = femalePart.transformed(unity().translate(0, yOffset, 0));

        double xStep = side * 0.5 + xOffset;
        double yStep = femalePart.getBounds().getBounds().y + yOffset + pinOffset;

        CSG result = null;

        for (int y = 0; y < numY; y++) {
            for (int x = 0; x < numX * 2; x++) {

                CSG part;

                if (x % 2 == 0) {
                    part = malePart;
                } else {
                    part = femalePart;
                }

                if (result == null) {
                    result = malePart;
                } else {
                    double xRowOffset = side * 0.5 + xOffset;

                    result = result.dumbUnion(part.transformed(unity().
                            translate(x * xStep + y * xRowOffset, y * yStep, 0)));
                }
            }
        }

        return result.transformed(Transform.unity());
    }

    public static void main(String[] args) throws IOException {

        int numX = 3;
        int numY = 3;

        FileUtil.write(new File("trimail-sla-" + numX + "x" + numY + ".stl"),
                new TriMailSLA().
                toCSG(numX, numY).toStlString());
    }
}
