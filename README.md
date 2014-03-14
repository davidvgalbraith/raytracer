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

where r g b is the color of your light, and x y z is its position.