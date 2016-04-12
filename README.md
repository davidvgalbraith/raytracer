# Dave's Ray Tracers

I have a few ray tracers here. I wrote the Java one for CS 184 at Berkeley, and I wrote Javascript and Go ones to help me learn those languages.

My ray tracers take files that describe scenes as input. The Java and Go ones write out the scene to a PNG file, while the Javascript one renders the scene in the browser.

### Input

The input to the Go and Javascript ray tracers is a JSON object. The JSON object has keys called `camera`, `lights`, and `shapes`.

`camera` is a nested object describing the configuration of the scene. It has the following keys:
- `origin`: the position of the viewer, as a 3-element array representing x, y, and z coordinates
- `view_plane`: the rectangle that the viewer is looking at, specified as a nested object whose keys are `upper_left`, `upper_right`, `lower_left`, and `lower_right` and whose values are coordinate arrays specifying the location of each of those corners
- `pixelsX`: the width of the image in pixels
- `pixelsY`: the height of the image in pixels

`lights` is an array of objects representing lights. A light has a `type`. The only `type` currently supported is `"directional"`. A directional light has a `direction` key specifying which way it points. A directional light is like a big wave of light washing over the scene in the given `direction`. It also has a `color`, represented as a three-element array describing the red, green and blue values for the color on a float scale from 0 to 1.

`shapes` is an array of objects representing the shapes in the scene. A shape has a `type` field specifying what shape it is. There are three supported shapes right now:
- sphere: A sphere has a `center` and a `radius`.
- ellipsoid: An ellipsoid has a `center` and multiple radii, specified as an object in the `radii` key of the shape. The `radii` object has `x`, `y`, and `z` keys specifying the x-, y- and z-radii of the ellipsoid. It can also be arbitrated an arbitrary number of degrees around the x-, y- or z-axis, specified by a `rotation` key in the shapse whose value is an object with `x`, `y` and `z` keys specifying the number of degrees to rotate around each axis.
- triangle: A triangle has `vertices`, an array of arrays of floats. Each nested array has three elements specifying the x-, y-, and z-coordinates of a vertex of the triangle.

Also, every shape has a `shading`. The shading is a nested object with several keys:
- ambient: an array of three floats between 0 and 1 specifying the shape's RGB coefficients for ambient shading. Ambient shading is described [here](https://en.wikipedia.org/wiki/Shading).
- diffuse: an array of three floats between 0 and 1 specifying the shape's RGB coefficients for diffuse shading. Diffuse shading is described [here](https://en.wikipedia.org/wiki/Phong_shading).
- reflection: an array of three floats between 0 and 1 specifying the shape's RGB coefficients for reflectivity. Basically, with a coefficient of 1, the shape is a mirror for that color of light, and smaller coefficients reflect more dully until at 0 the shape does not reflect that color.
- specular: an array of three floats between 0 and 1 specifying the shape's RGB coefficients for specular shading. Specular shading is described [here](https://en.wikipedia.org/wiki/Phong_shading).
- specular_exponent: It is popular to raise the color values from specular shading to some power to make the shading more dramatic. Do so with this.

The Java tracer has its own input format, described in the Java section. It's a little obscure, though.

### Go

To run the Go ray tracer, check out the code, set your GOPATH to the `raytracer/` directory, and run

```
go run go/src/raytracer/main.go [input file]
```

The tracer will write the resulting scene in the file `draw.png`.

### Javascript

Running the Javascript ray tracer is a little harder. First get Node.js, then run

```
node js/input-server.js
```

to start up the server that reads input files and returns the results. Actually, this is completely unusable, so I'm going to fix it up and then document it.

### Java

Java input files should have a line that looks like:

eye=[ x y z ]

where (x, y, z) is the position in space where you'll be looking from. Then you should have four lines that look like:

LL=[ x y z ]
LR=[ x y z ]
UL=[ x y z ]
UR=[ x y z ]

which specifies the lower left, lower right, upper left, and upper right coordinates of the viewing plane. The idea is that the ray tracer is supposed to render basically what the scene looks like if you look at the viewing plane from the eye position. Then you should have lines called;

pixelsX=x
pixelsY=y

that specify the dimensions of your output image. That's all the dumb bureaucracy, so now we can make our image! First you should have some light sources, otherwise you won't be able to see anything. You can have directional lights or point lights. Directional lights look like:

DL=[ r g b ], [ x y z ]

where r g b is the color of your light, and x y z is its direction. Point lights look like:

PL=[ r g b ], [ x y z ]

where r g b is the color of your light, and x y z is its position. Once you have light sources, you can have shapes too! The first shape you should know about is a sphere. Spheres look like this:

Sphere with center=[ x y z ] radius=r
ka=[ kax kay kaz ]
kd=[ kdx kdy kdz ]
ks=[ ksx ksy ksz ]
kr=[ krx kry krz ]

where x y z is the position of the center, r is the radius, and ka, kd, ks, and kr specify the BRDF of your sphere. Then there's two types of triangles you can have. The first type of triangle looks like this:

Triangle with v1=[  x1  y1  z1 ] v2=[  x2 y2 z2 ] v3=[ x3 y3 z3 ]
ka=[ kax kay kaz ]
kd=[ kdx kdy kdz ]
ks=[ ksx ksy ksz ]
kr=[ krx kry krz ]

where v1, v2, and v3 are the vertices of your triangle and the rest is the BRDF as before. Then there's the other type of triangle, which looks like:

NormalTriangle with v1=[  x1  y1  z1 ] v2=[  x2 y2 z2 ] v3=[ x3 y3 z3 ]
ka=[ kax kay kaz ]
kd=[ kdx kdy kdz ]
ks=[ ksx ksy ksz ]
kr=[ krx kry krz ]
n1=[ n1x n1y n1z ] n2=[ n2x n2y n2z ] n3=[ n3x n3y n3z ]

where n1, n2, and n3 are the vertex normals at v1, v2, and v3.

Besides this input format, the raytracer can read and draw simple .obj files, as long as the faces are only triangular and are specified in terms of only vertices or vertices and vertex normals (no "texture vertices"). It can read as many .obj files as you want, although I don't guarantee that the images won't overlap.

To run the raytracer from the command line, do:

java raytracer.Main file1 [file2.obj]* outputfile.jpg

where file1 is in the form we described above, giving a camera position and scene parameters and shapes if you want, and [file2.obj]* represents an optional list of as many .obj files as you want it to render, and outputfile.jpg is the name of the output that you want to make. Couldn't be easier! Have fun tracing rays.
