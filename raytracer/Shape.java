package raytracer;

public interface Shape {
	// Test if ray intersects with the shape or not (in object space), if so,
	// return intersection point and normal

	boolean intersect(Ray ray, Doublet thit, LocalGeo local);

	// Same as intersect, but just return whether there is any intersection or
	// not

	boolean intersectP(Ray ray);

	// Triangle and Sphere are probably best implemented here
	// The intersection with the ray at t outside the range [t_min, t_max]
	// should return false.

	boolean isTriangle();
}
