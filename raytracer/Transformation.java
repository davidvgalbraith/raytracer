package raytracer;

public class Transformation {
	Matrix m;

	public Transformation(Matrix m) {
		this.m = m;
	}

	public String toString() {
		return "Transformation: \n" + m;
	}

	Point transform(Point a) {
		double[] toarr = new double[4];
		toarr[0] = a.getX();
		toarr[1] = a.getY();
		toarr[2] = a.getZ();
		toarr[3] = 1;
		double[] x = m.times(toarr);
		return new Point(x[0], x[1], x[2]);
	}

	Ray transform(Ray a) {
		return new Ray(transform(a.getPos()), transform(a.getDir()),
				a.getTmin(), a.getTmax());
	}

	Vector transform(Vector a) {
		double[] toarr = new double[4];
		toarr[0] = a.getX();
		toarr[1] = a.getY();
		toarr[2] = a.getZ();
		toarr[3] = 0;
		double[] x = m.times(toarr);
		return new Vector(x[0], x[1], x[2]);
	}

	LocalGeo transform(LocalGeo a) {
		return new LocalGeo(transform(a.getPos()), transform(a.getNormal()));
	}

	Normal transform(Normal a) {
		double[] toarr = new double[4];
		toarr[0] = a.getX();
		toarr[1] = a.getY();
		toarr[2] = a.getZ();
		toarr[3] = 0;
		double[] x = MatrixMathematics.transpose(MatrixMathematics.inverse(m)).times(toarr);
		return new Normal(x[0], x[1], x[2]);
	}
}
