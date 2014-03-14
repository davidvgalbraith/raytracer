This here is a 3d geometric ray tracer implemented in Java. You kinda have to know about graphics to use it, sorry. So basically to use it you've gotta give it some input. That input comes in the form of a file. Your file should have a line that looks like:

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