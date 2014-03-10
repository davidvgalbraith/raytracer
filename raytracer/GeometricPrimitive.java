package raytracer;

public class GeometricPrimitive implements Primitive {
	Transformation objToWorld, worldToObj;
	Shape shape;
	Material mat;

	@Override
	public boolean intersect(Ray ray, Doublet thit, Intersection in) {
		Ray oray = objToWorld.transform(ray);
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
		Ray oray = objToWorld.transform(ray);
		return shape.intersectP(oray);
	}

	@Override
	public void getBRDF(LocalGeo local, BRDF brdf) {
		mat.getBRDF(local, brdf); 
	}

}
