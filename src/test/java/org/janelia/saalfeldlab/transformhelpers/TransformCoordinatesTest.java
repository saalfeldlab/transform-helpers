package org.janelia.saalfeldlab.transformhelpers;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Reader;
import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5DisplacementField;
import org.junit.Test;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class TransformCoordinatesTest {

	public static final double dx = 10;
	public static final double dy = 4;
	public static final double dz = 2;

	public static final double rx = 40;
	public static final double ry = 20;
	public static final double rz = 10;

	public static final double EPS = 1e-9;

	private static final String path = "src/test/resources/small-dfield.h5";

	@Test
	public void testDfield() throws Exception	{

		final N5HDF5Reader h5 = new N5HDF5Reader(path, 3, 32, 32, 32);
		RealTransform dfield = N5DisplacementField.open(h5, N5DisplacementField.FORWARD_ATTR, false);

		// middle pixel in pixel units
		final int i = 2;
		final int j = 2;
		final int k = 2;

		final double[] fullDisplacement = new double[] { dx, dy, dz };
		final double[] halfDisplacement = new double[] { dx / 2, dy / 2, dz / 2 };
		final double[] zeroDisplacement = new double[3];

		final double[] p = new double[] { rx * i, ry * j, rz * k };
		final double[] q = new double[3];

		dfield.apply(p, q);
		subtract(q, p, q);
		assertArrayEquals(fullDisplacement, q, EPS);

		p[0] = rx * (i + 1);
		p[1] = ry * (j + 1);
		p[2] = rz * (k + 1);

		dfield.apply(p, q);
		subtract(q, p, q);
		assertArrayEquals(halfDisplacement, q, EPS);

		p[0] = rx * (i - 1);
		p[1] = ry * (j - 1);
		p[2] = rz * (k - 1);

		dfield.apply(p, q);
		subtract(q, p, q);
		assertArrayEquals(halfDisplacement, q, EPS);

		p[0] = rx * (i + 2);
		p[1] = ry * (j + 2);
		p[2] = rz * (k + 2);

		dfield.apply(p, q);
		subtract(q, p, q);
		assertArrayEquals(zeroDisplacement, q, EPS);

		p[0] = 0;
		p[1] = 0;
		p[2] = 0;

		dfield.apply(p, q);
		subtract(q, p, q);
		assertArrayEquals(zeroDisplacement, q, EPS);
	}

	/**
	 * Sets z[i] = x[i] - y[i]
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	private static void subtract(double[] x, double[] y, double[] z) {
		for (int i = 0; i < z.length; i++)
			z[i] = x[i] - y[i];
	}

	/**
	 * Generate a small displacement field for testing
	 * 
	 * @param path destination path
	 * @throws IOException the exception
	 */
	public static void makeTestDfield(String path) throws IOException {
		final RandomAccessibleInterval<DoubleType> dfield = Views.stack(makeDisplacement(dx), makeDisplacement(dy),
				makeDisplacement(dz));

		System.out.println(Intervals.toString(dfield));
		final N5HDF5Writer h5 = new N5HDF5Writer(path, 3, 32, 32, 32);
		N5DisplacementField.save(h5, N5DisplacementField.FORWARD_ATTR, null, dfield, new double[] { rx, ry, rz },
				new int[] { 5, 5, 5, 3 }, new GzipCompression());
	}

	public static RandomAccessibleInterval<DoubleType> makeDisplacement(final double maxDisplacement) {
		FunctionRandomAccessible<DoubleType> disp = new FunctionRandomAccessible<>(3, (p, v) -> {
			int x = p.getIntPosition(0);
			int y = p.getIntPosition(1);
			int z = p.getIntPosition(2);

			if (x == 0 && y == 0 && z == 0) {
				v.set(maxDisplacement);
			} else if (x >= -1 && x <= 1 && y >= -1 && y <= 1 && z >= -1 && z <= 1) {
				v.set(0.5 * maxDisplacement);
			} else {
				v.setZero();
			}
		}, DoubleType::new);

		return Views.zeroMin(Views.interval(disp, Intervals.createMinMax(-2, -2, -2, 2, 2, 2)));
	}

	public static void main(String[] args) throws Exception {
//		final String path = "src/test/resources/small-dfield.h5";
//		makeTestDfield(path);
	}

}
