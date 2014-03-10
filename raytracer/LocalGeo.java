package raytracer;
//Store the local geometry at the intersection point. May need to store
//other quantities (eg. texture coordinate) in a more complicated
//raytracer.
public class LocalGeo {
	Point pos;
	Normal normal;
	double x, y, z;
	public LocalGeo(Point pos, Normal normal) {
		this.pos = pos;
		this.normal = normal;
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	public LocalGeo() {
		this.pos = null;
		this.normal = null;
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public Normal getNormal() {
		return normal;
	}

	public void setNormal(Normal normal) {
		this.normal = normal;
	}


	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
