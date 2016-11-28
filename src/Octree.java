/**
 * Created by Vikram on 11/12/2016.
 */

import toxi.geom.PointOctree;
import toxi.geom.Vec3D;


public class Octree extends PointOctree {
    private Print p;

    Octree(Print _p, Vec3D o, float d) {
        super(o, d);
        p = _p;
    }

    private void aB(Boid var1) {
        addPoint(var1);
    }

    public void aP(meshvertices var1){addPoint(var1);}

    public void r() {
        uT();
    }

    private void uT() {
        empty();
        for (Boid var1 : p.flock.b) {
            aB(var1);
        }
    }

    public void d() {
        dN(this);
        System.out.println("p = " + this.getNumChildren());
    }

    private void dN(PointOctree n) {
        if (n.getNumChildren() > 0) {
            p.noFill();
            p.stroke(255);
            p.strokeWeight(1);
            p.pushMatrix();
            p.translate(n.x, n.y, n.z);
            p.popMatrix();
            PointOctree[] childNodes = n.getChildren();
            for (int i = 0; i < 8; i++) {
                if (childNodes[i] != null) dN(childNodes[i]);
            }
        }
    }


}
