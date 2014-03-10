package raytracer;

public class DirectionalLight implements Light {

	Vector direction;
	Color color;

	public DirectionalLight(Vector direction, Color color) {
		super();
		this.direction = direction;
		this.color = color;
	}

	@Override
	public void generateLightRay(LocalGeo geo, Ray lray, Color col) {
		lray.setPos(geo.getPos());
		lray.setDir(direction.times(-1));
		lray.setTmin(0);
		lray.setTmax(Double.MAX_VALUE);
		col.setAll(color);
	}
	
	public String toString() {
		return "Directional light, " + color + " direction: " + direction;
	}

}
