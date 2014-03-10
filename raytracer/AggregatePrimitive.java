package raytracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AggregatePrimitive implements Primitive {
	List<Primitive> list;
	public AggregatePrimitive(List<Primitive> list) {
		this.list = list;
	}
	public AggregatePrimitive() {
		this.list = new ArrayList<Primitive>();
	}
	public void add(Primitive p) {
		this.list.add(p);
	}
	@Override
	public boolean intersect(Ray ray, Doublet thit, Intersection in) {
		double d = Double.MAX_VALUE;
		HashMap<Double, Primitive> map = new HashMap<Double, Primitive>();
		for (Primitive p : list) {
			if (p.intersect(ray, thit, in)) {
				if (thit.getD() < d) {
					d = thit.getD();
					map.put(d, p);
				}
			}
		}
		if (d < Double.MAX_VALUE) {
			map.get(d).intersect(ray, thit, in);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean intersectP(Ray ray) {
		for (Primitive p : list) {
			if (p.intersectP(ray)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void getBRDF(LocalGeo local, BRDF brdf) {
		System.err.println("Impossible method call to getBRDF in AggregatePrimitive");
		System.exit(1);
	}

}
