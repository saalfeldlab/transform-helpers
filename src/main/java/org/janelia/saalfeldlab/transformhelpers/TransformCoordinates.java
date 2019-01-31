/**
 *
 */
package org.janelia.saalfeldlab.transformhelpers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Reader;
import org.janelia.saalfeldlab.n5.imglib2.N5DisplacementField;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import net.imglib2.realtransform.ExplicitInvertibleRealTransform;

/**
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 */
public class TransformCoordinates {

	public static void main(final String... args) throws Exception {

		final IHDF5Reader hdf5Reader = HDF5Factory.openForReading(args[0]);
		final N5HDF5Reader n5 = new N5HDF5Reader(hdf5Reader, new int[]{16, 16, 16});

		final ExplicitInvertibleRealTransform transform = new ExplicitInvertibleRealTransform(
				N5DisplacementField.open(n5, "/dfield", false),
				N5DisplacementField.open(n5, "/invdfield", false));

		final double[] p = new double[3];
		final double[] q = new double[3];

		try (Stream<String> stream = Files.lines(Paths.get(args[1]))) {

			stream.forEach(line -> {
				final String[] coordinates = line.split("\\s+");
				if (coordinates.length == 3) {
					p[0] = Double.parseDouble(coordinates[0]);
					p[1] = Double.parseDouble(coordinates[1]);
					p[2] = Double.parseDouble(coordinates[2]);

					transform.apply(p, q);

					System.out.println(q[0] + " " + q[1] + " " + q[2]);
				}
			});
		}
	}
}
