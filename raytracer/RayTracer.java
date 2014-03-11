package raytracer;

import java.util.ArrayList;
import java.util.List;

public class RayTracer {
	AggregatePrimitive p;
	List<Light> lights;
	Point origin;

	public RayTracer() {
		this.p = new AggregatePrimitive(new ArrayList<Primitive>());
		this.lights = new ArrayList<Light>();
	}

	public RayTracer(AggregatePrimitive p, List<Light> lights, Point origin) {
		this.p = p;
		this.lights = lights;
		this.origin = origin;
	}

	void trace(Ray ray, int depth, Color color) {

		Doublet d = new Doublet(0);

		if (depth > 0) {
			// d.setD(69);
		}
		origin = ray.getPos();
		Intersection in = new Intersection(null, null);
		if (depth > 1) {
			color.setB(0.0);
			color.setG(0.0);
			color.setR(0.0);
			return;
		}
		if (!p.intersect(ray, d, in)) {
			// No intersection
			color.setB(0.0);
			color.setG(0.0);
			color.setR(0.0);
			return;
		}
		// Obtain the brdf at intersection point
		if (depth > 0
				&& ((GeometricPrimitive) in.getPrimitive()).shape.isTriangle()) {
			System.err.println("Self intersection" + d.getD());
			System.exit(1);
		}
		BRDF brdf = new BRDF();
		in.getPrimitive().getBRDF(in.getLocalGeo(), brdf);
		// There is an intersection, loop through all light source
		for (int i = 0; i < lights.size(); i++) {
			Ray lray = new Ray(null, null, 0, 0);
			Color lcolor = new Color(0, 0, 0);
			lights.get(i).generateLightRay(in.getLocalGeo(), lray, lcolor);
			color.setAll(color.plus(brdf.getKa().times(lcolor)));
			// Check if the light is blocked or not
			if (!p.intersectP(lray)) {
				// If not, do shading calculation for this
				// light source
				// System.out.println("Hilarious! " + in.getLocalGeo() + " " +
				// brdf + " " + lray + " " + lcolor);
				color.setAll(color.plus(shading(in.getLocalGeo(), brdf, lray,
						lcolor)));

				// Handle mirror reflection
			}
		}
		if (brdf.getKr().getB() > 0 || brdf.getKr().getG() > 0
				|| brdf.getKr().getR() > 0) {
			// System.out.println("ray was " + ray);
			Ray reflectRay = createReflectRay(in.getLocalGeo(), ray);
			// System.out.println("Ray is " + ray);
			// Make a recursive call to trace the reflected ray
			Color temp = new Color(0, 0, 0);
			// System.out.println("Yes this is dog" + depth);
			trace(reflectRay, depth + 1, temp);
			System.out.println(color + " Not now dog); " + temp);
			color.setAll(color.plus(temp.times(brdf.getKr())));

		}
	}

	Ray createReflectRay(LocalGeo geo, Ray ray) {
		Vector l = ray.getDir().normalize();
		Vector n = geo.getNormal().vectorize();
		Vector r = (l.times(-1).plus(n.times(2 * n.dot(l))).normalize())
				.times(-1);
		Ray rey = new Ray(geo.getPos(), r, 0.01, Double.MAX_VALUE);
		return rey;
	}

	Color shading(LocalGeo geo, BRDF brdf, Ray lray, Color lcolor) {
		Color spec = speculate(geo, brdf, lray, lcolor);
		Color diff = diffuse(geo, brdf, lray, lcolor);
		// System.out.println("Diff: " + diff);
		return spec.plus(diff);
		// return spec;
		// return diff;
	}

	Color speculate(LocalGeo geo, BRDF brdf, Ray lray, Color lcolor) {
		Vector l = lray.getDir().normalize();
		Vector n = geo.getNormal().vectorize();
		Vector r = l.times(-1).plus(n.times(2 * n.dot(l))).normalize();
		Vector v = origin.minus(geo.getPos()).normalize();
		// System.out.println("Noraml was " + geo.getNormal() +
		// " and  pointlnnelsly calculated " + v);
		double dot = r.dot(v);
		if (dot < 0) {
			dot = 0;
		} else {
			dot = Math.pow(dot, 50);
		}
		// System.out.println("Dof " + dot);
		return colorific(dot, brdf.getKs(), lcolor);
	}

	Color diffuse(LocalGeo geo, BRDF brdf, Ray lray, Color lcolor) {
		Vector l = lray.getDir().normalize();
		Vector n = geo.getNormal().vectorize();
		double dot = n.dot(l);
		return colorific(dot, brdf.getKd(), lcolor);
	}

	Color colorific(double dot, Color k, Color lcolor) {
		double red = Math.max(dot * k.getR() * lcolor.getR(), 0);
		double green = Math.max(dot * k.getG() * lcolor.getG(), 0);
		double blue = Math.max(dot * k.getB() * lcolor.getB(), 0);
		return new Color(red, green, blue);

	}
}
