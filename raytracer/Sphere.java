package raytracer;

public class Sphere implements Shape {
	Point center;
	double radius;

	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	@Override
	public boolean intersect(Ray ray, Doublet thit, LocalGeo local) {
		Point e = ray.getPos();
		Vector d = ray.getDir();
		Vector emc = e.minus(center);
		double a = d.dot(d);
		double b = 2 * d.dot(emc);
		double c = emc.dot(emc) - radius * radius;
		double disc = b * b - 4 * a * c;
		if (disc < 0) {
			return false;
		}
		double t1 = (-1 * b + Math.sqrt(disc)) / (2 * a);
		double t2 = (-1 * b - Math.sqrt(disc)) / (2 * a);
		Garbage g = new Garbage(ray.getTmin(), ray.getTmax());
		g.add(t1);
		g.add(t2);
		if (g.size() == 0) {
			return false;
		}
		double thitt = g.smaller();
		thit.setD(thitt);
		local.setPos(ray.eval(thitt));
		local.setNormal(Normal.normalize(ray.eval(thitt).minus(center)));
		return true;
	}

	@Override
	public boolean intersectP(Ray ray) {

		Point e = ray.getPos();
		Vector d = ray.getDir();
		double a = d.dot(d);
		double b = 2 * d.dot(e.minus(center));
		double c = e.minus(center).dot(e.minus(center)) - radius * radius;
		double disc = b * b - 4 * a * c;
		if (disc < 0) {
			return false;
		}
		double t1 = (-1 * b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		double t2 = (-1 * b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		Garbage g = new Garbage(ray.getTmin(), ray.getTmax());
		g.add(t1);
		g.add(t2);
		if (g.size() == 0) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "a sphere of radius " + radius + " centered at " + center;
	}

	public boolean isTriangle() {
		return false;
	}
}
   
