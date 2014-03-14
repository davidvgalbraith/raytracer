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
		return "Gprim: " + shape;
	}
	
	@Override
	public boolean intersect(Ray ray, Doublet thit, Intersection in) {
		//System.out.println("Original " + ray);
		//System.out.println("wobj" + worldToObj.m);
		Ray oray = worldToObj.transform(ray);
		//System.out.println("Then " + oray);
		LocalGeo olocal = new LocalGeo();
		if (!shape.intersect(oray, thit, olocal)) {
			return false;
		}
		in.setPrimitive(this);
		//System.out.println("objw"+objToWorld);
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
