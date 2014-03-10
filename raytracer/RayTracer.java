package raytracer;

import java.util.ArrayList;
import java.util.List;

public class RayTracer {
	AggregatePrimitive p;
	List<Light> lights;

	public RayTracer() {
		this.p = new AggregatePrimitive(new ArrayList<Primitive>());
		this.lights = new ArrayList<Light>();
	}
	
	public RayTracer(AggregatePrimitive p, List<Light> lights) {
		this.p = p;
		this.lights = lights;
	}

	void trace(Ray ray, int depth, Color color) {
		Doublet d = new Doublet(0);
		Intersection in = new Intersection(null, null);
		if (depth > 5) {
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
		System.out.println("It intersected!");
		BRDF brdf = new BRDF(null, null, null, null);
		in.getPrimitive().getBRDF(in.getLocalGeo(), brdf);
		color.setR(1.0);
		color.setG(0);
		color.setB(0);
		
		// There is an intersection, loop through all light source

		for (int i = 0; i < lights.size(); i++) {
			Ray lray = new Ray(null, null, 0, 0);
			Color lcolor = new Color(0, 0, 0);
			lights.get(i).generateLightRay(in.getLocalGeo(), lray, lcolor);

			// Check if the light is blocked or not

			if (!p.intersectP(lray)) {

				// If not, do shading calculation for this

				// light source

				color = color
						.plus(shading(in.getLocalGeo(), brdf, lray, lcolor));

			}

			// Handle mirror reflection

			if (brdf.getKr().getB() > 0 || brdf.getKr().getG() > 0 || brdf.getKr().getR() > 0) {

				Ray reflectRay = createReflectRay(in.getLocalGeo(), ray);

				// Make a recursive call to trace the reflected ray
				Color temp = new Color(0, 0, 0);
				trace(reflectRay, depth + 1, temp);

				color = color.plus(temp.times(brdf.getKr()));

			}
		}
	}

	Ray createReflectRay(LocalGeo geo, Ray ray) {
		return null;
	}

	Color shading(LocalGeo geo, BRDF brdf, Ray lray, Color lcolor) {
		return null;
	}
}
