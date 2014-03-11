package raytracer;

public class Triangle implements Shape {
	Vector va, vb, vc;
	@Override
	public boolean intersect(Ray ray, Doublet thit, LocalGeo local) {
		Vector ve = new Vector(ray.getX(), ray.getY(), ray.getZ());
		Vector vd = ray.getDir();
		double a = va.getX() - vb.getX();
		double b = va.getY() - vb.getY();
		double c = va.getZ() - vb.getZ();
		double d = va.getX() - vc.getX();
		double e = va.getY() - vc.getY();
		double f = va.getZ() - vc.getZ();
		double g = vd.getX();
		double h = vd.getY();
		double i = vd.getZ();
		double j = va.getX() - ve.getX();
		double k = va.getY() - ve.getY();
		double l = va.getZ() - ve.getZ();
		double eihf = e * i - h * f;
		double gfdi = g * f - d * i;
		double dheg = d * h - e * g;
		double akjb = a * k - j * b;
		double jcal = j * c - a * l;
		double blkc = b * l - k * c;
		double m = a * eihf + b * gfdi + c * dheg;
		double beta = (j * eihf + k * gfdi + l * dheg) / m;
		double gamma = (i * akjb + h * jcal + g * blkc) / m;
		double t  = -1 * (f * akjb + e * jcal + d * blkc) / m;
		if (t < ray.getTmin() || t > ray.getTmax() || gamma < 0 || gamma > 1 || beta < 0 || beta > 1-gamma) {
			return false;
		}
		thit.setD(t);
		local.setPos(ray.eval(t));
		Vector norm1 = ((vb.minus(va)).cross((vc.minus(va)))).normalize();
		local.setNormal(new Normal(norm1.getX(), norm1.getY(), norm1.getZ()));
		return true;
		
	}

	public Triangle(Vector va, Vector vb, Vector vc) {
		super();
		this.va = va;
		this.vb = vb;
		this.vc = vc;
	}

	@Override
	public boolean intersectP(Ray ray) {
		Vector ve = new Vector(ray.getX(), ray.getY(), ray.getZ());
		Vector vd = ray.getDir();
		double a = va.getX() - vb.getX();
		double b = va.getY() - vb.getY();
		double c = va.getZ() - vb.getZ();
		double d = va.getX() - vc.getX();
		double e = va.getY() - vc.getY();
		double f = va.getZ() - vc.getZ();
		double g = vd.getX();
		double h = vd.getY();
		double i = vd.getZ();
		double j = va.getX() - ve.getX();
		double k = va.getY() - ve.getY();
		double l = va.getZ() - ve.getZ();
		double eihf = e * i - h * f;
		double gfdi = g * f - d * i;
		double dheg = d * h - e * g;
		double akjb = a * k - j * b;
		double jcal = j * c - a * l;
		double blkc = b * l - k * c;
		double m = a * eihf + b * gfdi + c * dheg;
		double beta = (j * eihf + k * gfdi + l * dheg) / m;
		double gamma = (i * akjb + h * jcal + g * blkc) / m;
		double t  = -1 * (f * akjb + e * jcal + d * blkc) / m;
		if (t < ray.getTmin() || t > ray.getTmax() || gamma < 0 || gamma > 1 || beta < 0 || beta > 1-gamma) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "Triangle: " + va + " " + vb + " " + vc;
	}
	public boolean isTriangle() {
		return true;
	}
}
