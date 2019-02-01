/**
 *
 */
package org.janelia.saalfeldlab.transformhelpers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Reader;
import org.janelia.saalfeldlab.n5.imglib2.N5DisplacementField;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.realtransform.InverseRealTransform;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;
import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 */
public class TransformCoordinates implements Callable<Void> {

	@Option(names = {"-t", "--transform"}, required = true, description = "deformation field filename, e.g. JRC2018F_FCWB_transform_quant16.h5")
	private String transformFile;

	@Option(names = {"-c", "--coordinates"}, required = true, description = "coordinates filename, e.g. tALT.fafb.jrc2018.txt")
	private String coordinatesFile;

	@Option(names = {"-i", "--inverse"}, required = false)
	private boolean inverse;

	public static void main(final String... args) {

		CommandLine.call(new TransformCoordinates(), args);
	}

	@Override
	public Void call() throws Exception {

		final IHDF5Reader hdf5Reader = HDF5Factory.openForReading(transformFile);
		final N5HDF5Reader n5 = new N5HDF5Reader(hdf5Reader, new int[]{16, 16, 16});

		RealTransform transform = N5DisplacementField.open(
				n5,
				inverse ? "/invdfield" : "/dfield",
				false);

		final double[] p = new double[3];
		final double[] q = new double[3];

		try (Stream<String> stream = Files.lines(Paths.get(coordinatesFile))) {

			stream.forEach(line -> {
				final String[] coordinates = line.split(",?\\s+");
				if (coordinates.length == 3) {
					p[0] = Double.parseDouble(coordinates[0]);
					p[1] = Double.parseDouble(coordinates[1]);
					p[2] = Double.parseDouble(coordinates[2]);

					transform.apply(p, q);

					System.out.println(q[0] + " " + q[1] + " " + q[2]);
				}
			});
		}

		return null;
	}

	public void testImg(final InverseRealTransform transform) {

		ArrayImg<UnsignedByteType, ByteArray> img = ArrayImgs.unsignedBytes(100, 200, 300);
		ExtendedRandomAccessibleInterval<UnsignedByteType, ArrayImg<UnsignedByteType, ByteArray>> extended = Views.extendZero(img);
		RealRandomAccessible<UnsignedByteType> interpolant = Views.interpolate(extended, new NLinearInterpolatorFactory<>());
		RealViews.transformReal(interpolant, transform);
	}
}