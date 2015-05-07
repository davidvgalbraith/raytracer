package raytracer;

public class GeometricPrimitive implements Primitive {
	Transformation objToWorld, worldToObj;
	Shape shape;
	Material mat;

	public GeometricPrimitive(Transformation objToWorld,
			Transformation worldToObj, Shape shape, Material mat) {
		this.objToWorld = objToWorld;
		this.worldToObj = worldToObj;
		this.shape = shape;
		this.mat = mat;
	}

	public String toString() {
		return shape + " transformed by " + worldToObj;
	}

	@Override
	public boolean intersect(Ray ray, Doublet thit, Intersection in) {
		Ray oray = worldToObj.transform(ray);
		LocalGeo olocal = new LocalGeo();
		if (!shape.intersect(oray, thit, olocal)) {
			return false;
		}
		in.setPrimitive(this);
		in.setLocalGeo(objToWorld.transform(olocal));
		return true;
	}

	@Override
	public boolean intersectP(Ray ray) {

		Ray oray = worldToObj.transform(ray);
		return shape.intersectP(oray);
	}

	@Override
	public void getBRDF(LocalGeo local, BRDF brdf) {
		mat.getBRDF(local, brdf);
	}

}
