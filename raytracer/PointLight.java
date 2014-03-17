package raytracer;

public class PointLight implements Light {

	Point location;
	Color color;

	public PointLight(Point location, Color color) {
		super();
		this.location = location;
		this.color = color;
	}

	@Override
	public void generateLightRay(LocalGeo geo, Ray lray, Color col) {
		lray.setPos(geo.getPos());
		lray.setDir(location.minus(geo.getPos()));
		lray.setTmin(0.1);
		lray.setTmax(100);
		col.setAll(color);
	}

}
   
