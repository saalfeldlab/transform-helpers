# transform-helpers
Helper tools and API to apply transformations to coordinates and images

## Build

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
java -jar target/transform-helpers-0.0.1-SNAPSHOT-shaded.jar JRC2018F_FCWB_transform_quant16.h5 tALT.fafb.jrc2018.txt

```
