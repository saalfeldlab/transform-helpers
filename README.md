# transform-helpers
Helper tools and API to apply transformations to coordinates and images

## Build

Currently depends on two SNAPSHOTS, i.e. latest master branch builds:

1. [imglib2-realtransform](https://github.com/imglib/imglib2-realtransform)
2. [n5-imglib2](https://github.com/saalfeldlab/n5-imglib2)

Normal
```bash
mvn clean install
```

Fat jar
```
mvn -Pfat clean package
```

## Run

Transform coordinates using a transformation field using @bogovicj's [HDF5 deformation fields](https://github.com/saalfeldlab/template-building/wiki/Hdf5-Deformation-fields) (TODO find a name, e.g. quantized deformation fields QDF) for a list of coordinates stored as space separated values in a text file:

```bash
java -jar target/transform-helpers-0.0.1-SNAPSHOT-shaded.jar \
  --transform JRC2018F_FCWB_transform_quant16.h5 \
  --coordinates tALT.fafb.jrc2018.txt \
  [--inverse]

```
