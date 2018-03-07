package model;

public class Vector {

	public double x; // projection of vector on x
	public double y; // projection of vector on y
	public double r; // length of vector
	public double teta; // angle of vector to x axis

	public final double pi = Math.PI;

	public Vector() {
		// Cartesian
		this.x = 0;
		this.y = 0;
		// Polar
		this.r = 0;
		this.teta = 0;

	}

	public Vector(double x, double y) {
		this();
		setCartesian(x, y);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getR() {
		return this.r;
	}

	public double getTeta() {
		return this.teta;
	}

	public void setTeta(double teta) {
		this.teta = teta;
	}

	/*
	 * Vector Class can work with both Cartesian coordinate system and Polar
	 * coordinate systems in two dimensions because it has r and teta variables
	 * as well as x and y.
	 */

	public void setCartesian(double x, double y) {
		this.x = x;
		this.y = y;
		this.r = Math.sqrt(x * x + y * y);
		// finding teta
		if (y / x < 0) {
			if (y > 0)
				this.teta = pi / 2 + Math.atan(-x / y);
			else
				this.teta = 2 * pi - Math.atan(-y / x);
		}
		if (y / x > 0) {
			if (y < 0)
				this.teta = pi + Math.atan(y / x);
			else
				this.teta = Math.atan(y / x);
		}
		if (y == 0) {
			if (x == 0)
				this.teta = 0;
			else if (x > 0)
				this.teta = 0;
			else
				this.teta = -pi;
		}
		if (x == 0) {
			if (y > 0)
				this.teta = pi / 2;
			else
				this.teta = 3 * pi / 2;
		}

	}

	public void setPolar(double r, double teta) {
		this.r = r;
		this.teta = teta;
		this.x = r * Math.cos(teta);
		this.y = r * Math.sin(teta);
	}

	public static Vector vectorSum(Vector A, Vector B) { // sum two vectors
		Vector vector = new Vector();
		vector.setCartesian(A.getX() + B.getX(), A.getY() + B.getY());
		return vector;
	}

	public static Vector vectorSubtract(Vector A, Vector B) { // subtract two
																// vectors
		Vector vector = new Vector();
		vector.setCartesian(A.getX() - B.getX(), A.getY() - B.getY());
		return vector;
	}

	public static double dotProduct(Vector A, Vector B) { // scalar vector
															// multibly

		return (A.getX() * B.getX() + A.getY() * B.getY());
	}

	public static double angle(Vector A, Vector B) { // find angle between two
														// vectors
		return Math.acos(dotProduct(A, B) / (A.getR() * B.getR()));
	}

	public static Vector product(double factor, Vector A) { // multible a vector
															// with a number
		Vector result = new Vector();
		result.setPolar(factor * A.getR(), A.getTeta());
		return result;
	}

	public static double distance(Vector A, Vector B) {
		return Math.sqrt(
				((A.getX() - B.getX()) * (A.getX() - B.getX())) + ((A.getY() - B.getY()) * (A.getY() - B.getY())));
	} // also vectorSubtract(A,B).getR() will return distance between A and B

}
