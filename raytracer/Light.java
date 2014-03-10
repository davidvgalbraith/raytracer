package raytracer;

public interface Light {

	void generateLightRay(LocalGeo geo, Ray lray, Color col);
	
	
}
