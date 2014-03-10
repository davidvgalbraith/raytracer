package raytracer;

public class Intersection {
    LocalGeo localGeo;

    Primitive primitive;

	public Intersection(LocalGeo localGeo, Primitive primitive) {
		this.localGeo = localGeo;
		this.primitive = primitive;
	}

	public LocalGeo getLocalGeo() {
		return localGeo;
	}

	public void setLocalGeo(LocalGeo localGeo) {
		this.localGeo = localGeo;
	}

	public Primitive getPrimitive() {
		return primitive;
	}

	public void setPrimitive(Primitive primitive) {
		this.primitive = primitive;
	}
}
