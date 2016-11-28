import toxi.geom.Vec3D;

public class trail extends Vec3D {
    private Print p1;
    Vec3D o;
    public int  trailNo = 80;

    trail( Print _p, Vec3D p, Vec3D _o) {
        super(p);
        p1 =_p;
        o = _o.copy();
        o = o.normalize();
    }
}