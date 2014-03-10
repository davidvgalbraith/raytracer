package raytracer;

public class Transformation {
	Matrix m, minvt;
	public Transformation(Matrix m) {
		this.m = m;
		try {
			this.minvt = MatrixMathematics.transpose(MatrixMathematics.inverse(m));
		} catch (NoSquareException e) {
			e.printStackTrace();
		}
	}
	Point transform(Point a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getValues()[0][0] * a.getX();
		x += m.getValues()[0][1] * a.getY();
		x += m.getValues()[0][2] * a.getZ();
		y += m.getValues()[1][0] * a.getX();
		y += m.getValues()[1][1] * a.getY();
		y += m.getValues()[1][2] * a.getZ();
		z += m.getValues()[2][0] * a.getX();
		z += m.getValues()[2][1] * a.getY();
		z += m.getValues()[2][2] * a.getZ();
		return new Point(x, y, z);
	}

	Ray transform(Ray a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getValues()[0][0] * a.getX();
		x += m.getValues()[0][1] * a.getY();
		x += m.getValues()[0][2] * a.getZ();
		y += m.getValues()[1][0] * a.getX();
		y += m.getValues()[1][1] * a.getY();
		y += m.getValues()[1][2] * a.getZ();
		z += m.getValues()[2][0] * a.getX();
		z += m.getValues()[2][1] * a.getY();
		z += m.getValues()[2][2] * a.getZ();
		return new Ray(new Point(x, y, z), transform(a.getDir()), a.getTmin(),
				a.getTmax());
	}

	Vector transform(Vector a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getValues()[0][0] * a.getX();
		x += m.getValues()[0][1] * a.getY();
		x += m.getValues()[0][2] * a.getZ();
		y += m.getValues()[1][0] * a.getX();
		y += m.getValues()[1][1] * a.getY();
		y += m.getValues()[1][2] * a.getZ();
		z += m.getValues()[2][0] * a.getX();
		z += m.getValues()[2][1] * a.getY();
		z += m.getValues()[2][2] * a.getZ();
		return new Vector(x, y, z);
	}

	LocalGeo transform(LocalGeo a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += m.getValues()[0][0] * a.getX();
		x += m.getValues()[0][1] * a.getY();
		x += m.getValues()[0][2] * a.getZ();
		y += m.getValues()[1][0] * a.getX();
		y += m.getValues()[1][1] * a.getY();
		y += m.getValues()[1][2] * a.getZ();
		z += m.getValues()[2][0] * a.getX();
		z += m.getValues()[2][1] * a.getY();
		z += m.getValues()[2][2] * a.getZ();
		return new LocalGeo(new Point(x, y, z), transform(a.getNormal()));
	}
	Normal transform(Normal a) {
		double x = 0;
		double y = 0;
		double z = 0;
		x += minvt.getValues()[0][0] * a.getX();
		x += minvt.getValues()[0][1] * a.getY();
		x += minvt.getValues()[0][2] * a.getZ();
		y += minvt.getValues()[1][0] * a.getX();
		y += minvt.getValues()[1][1] * a.getY();
		y += minvt.getValues()[1][2] * a.getZ();
		z += minvt.getValues()[2][0] * a.getX();
		z += minvt.getValues()[2][1] * a.getY();
		z += minvt.getValues()[2][2] * a.getZ();
		return new Normal(x, y, z);
	}
}
