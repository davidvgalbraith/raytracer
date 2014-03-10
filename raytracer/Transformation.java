package raytracer;

public class Transformation {
	Matrix m;

	Point transform(Point a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getM()[0][0] * a.getX();
		x += m.getM()[0][1] * a.getY();
		x += m.getM()[0][2] * a.getZ();
		y += m.getM()[1][0] * a.getX();
		y += m.getM()[1][1] * a.getY();
		y += m.getM()[1][2] * a.getZ();
		z += m.getM()[2][0] * a.getX();
		z += m.getM()[2][1] * a.getY();
		z += m.getM()[2][2] * a.getZ();
		return new Point(x, y, z);
	}

	Ray transform(Ray a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getM()[0][0] * a.getX();
		x += m.getM()[0][1] * a.getY();
		x += m.getM()[0][2] * a.getZ();
		y += m.getM()[1][0] * a.getX();
		y += m.getM()[1][1] * a.getY();
		y += m.getM()[1][2] * a.getZ();
		z += m.getM()[2][0] * a.getX();
		z += m.getM()[2][1] * a.getY();
		z += m.getM()[2][2] * a.getZ();
		return new Ray(new Point(x, y, z), transform(a.getDir()), a.getTmin(),
				a.getTmax());
	}

	Vector transform(Vector a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getM()[0][0] * a.getX();
		x += m.getM()[0][1] * a.getY();
		x += m.getM()[0][2] * a.getZ();
		y += m.getM()[1][0] * a.getX();
		y += m.getM()[1][1] * a.getY();
		y += m.getM()[1][2] * a.getZ();
		z += m.getM()[2][0] * a.getX();
		z += m.getM()[2][1] * a.getY();
		z += m.getM()[2][2] * a.getZ();
		return new Vector(x, y, z);
	}

	LocalGeo transform(LocalGeo a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getM()[0][0] * a.getX();
		x += m.getM()[0][1] * a.getY();
		x += m.getM()[0][2] * a.getZ();
		y += m.getM()[1][0] * a.getX();
		y += m.getM()[1][1] * a.getY();
		y += m.getM()[1][2] * a.getZ();
		z += m.getM()[2][0] * a.getX();
		z += m.getM()[2][1] * a.getY();
		z += m.getM()[2][2] * a.getZ();
		return new LocalGeo(new Point(x, y, z), transform(a.getNormal()));
	}
//TODO THIS IS WRONG
	Normal transform(Normal a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getM()[0][0] * a.getX();
		x += m.getM()[0][1] * a.getY();
		x += m.getM()[0][2] * a.getZ();
		y += m.getM()[1][0] * a.getX();
		y += m.getM()[1][1] * a.getY();
		y += m.getM()[1][2] * a.getZ();
		z += m.getM()[2][0] * a.getX();
		z += m.getM()[2][1] * a.getY();
		z += m.getM()[2][2] * a.getZ();
		return new Normal(x, y, z);
	}
}
