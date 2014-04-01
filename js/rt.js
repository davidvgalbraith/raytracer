(function() {

    //scene and its discontents

    var scene = function(camera, raytracer, imgData) {
	var x;
	//send a ray through the viewing plane
	var generateRay = function(x, y) {
	    var xpos = camera.UL.x + (x / camera.pixelsX) * (camera.UR.x - camera.UL.x) + 1 / (2 * camera.pixelsX);
	    var ypos = camera.UL.y + (y / camera.pixelsY) * (camera.LL.y - camera.UL.y) + 1 / (2 * camera.pixelsY);
	    console.log(xpos);
	    console.log(ypos);
	    console.log("\n");
	    var screenpos = vector([xpos, ypos, camera.UL.z]);
	    return ray(vector(camera.eye), screenpos);
	}
	for (x = 0; x < camera.pixelsX * camera.pixelsY * 4; x += 4) {
	    var xfour = x / 4;
	    console.log("xfour: " + xfour);
	    var raaay = generateRay(xfour % camera.pixelsX, Math.floor(xfour / camera.pixelsX));//, xfour % camera.pixelsX);
	    var color = raytracer.trace(raaay, 5);
	    imgData.data[x] = color.x > 255 ? 255 : color.x;
	    imgData.data[x+1] = color.y > 255 ? 255 : color.y;
	    imgData.data[x+2] = color.z > 255 ? 255 : color.z;
	    imgData.data[x+3] = 255;
	}
	return imgData.data;
    }

    var ray = function(position, direction) {
	r = {};
	r.position = position;
	r.direction = direction;
	r.valueAt = function(t) {
	    return position.plus(direction.times(t));
	}
	return r;
    }

    var vector = function(coordinates) {
	var v = {};
	v.x = coordinates[0];
	v.y = coordinates[1];
	v.z = coordinates[2];

	v.minus = function(w) {
	    return vector([v.x - w.x, v.y - w.y, v.z - w.z]);
	}

	v.plus = function(w) {
	    return vector([v.x + w.x, v.y + w.y, v.z + w.z]);
	}

	v.times = function(a) {
	    return vector([a * v.x, a * v.y, a * v.z]);
	}
	
	v.dot = function(w) {
	    return v.x * w.x + v.y * w.y + v.z * w.z;
	}

	v.normalize = function() {
	    var norm = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
	    if (norm === 0) {
		console.log("Tried to normalize a zero vector")
		return this;
	    }
	    return vector([v.x / norm, v.y / norm, v.z / norm]);
	}
	return v;
    }
    var raytracer = function(shapes, lights, origin, maxReflections) {
	var rt = {};
	rt.trace = function(rayr, reflections) {
	    if (reflections > maxReflections) {
		return vector([0, 0, 0]);
	    }
	    if (!shapes.intersect(rayr)) {
		return vector([0, 255, 0]);
	    }
	    return vector([255, 0, 0]);
	}
	return rt;
    }
    
    var shapeobject = function(shapes) {
	var o = {};
	o.intersect = function(rayr) {
	    var tmin = Infinity;
	    var ret = null;
	    for (var k = 0; k < shapes.length; k++) {
		var shape = shapes[k];
		if (shape.type === "sphere") {
		    var emc = rayr.position.minus(vector(shape.center));
//		    console.dir(emc);
		    var a = rayr.direction.dot(rayr.direction);
		    var b = 2 * rayr.direction.dot(emc);
		    var c = emc.dot(emc) - shape.radius * shape.radius;
		    var disc = b * b - 4 * a * c;
		    		 
//		    console.log("a: " + a);
//		    console.log("b: " + b);
//		    console.log("c: " + c);

//		    console.dir(rayr.direction);

//		    console.log(disc);
		    if (disc < 0) {
			continue;
		    }
		    var t1 = (-1 * b + Math.sqrt(disc)) / (2 * a);
		    var t2 = (-1 * b - Math.sqrt(disc)) / (2 * a);
		    var thit;
		    if (t1 < 0.001 && t2 < 0.001) {
			continue;
		    }
		    if (t1 > 0.001 && t2 < 0.001) {
			thit = t1;
		    } else {
			thit = t2;
		    }
		    if (thit < tmin) {
			tmin = thit; 
			ret = ray(rayr.valueAt(thit), rayr.valueAt(thit).minus(vector(shape.center)).normalize());
		    }
		}
	    }
	    return ret;
	}
	return o;
    }

    var directionalight = function() {}
    var pointlight = function() {}
    var sphere = function() {}
    var triangle = function() {}




   
    //read the input file
    var inputFile = new XMLHttpRequest();
    inputFile.open("GET", "file:///home/dave/ray/js/input-00.js", false);
    inputFile.overrideMimeType("application/json");
    inputFile.send(null);
    var text = inputFile.responseText;
    var objects = JSON.parse(text);
    //parse the input file
    var cam = objects["camera"];
    cam.UL = vector(cam.UL);
    cam.UR = vector(cam.UR);
    cam.LL = vector(cam.LL);
    cam.LR = vector(cam.LR);

    var lights = objects["lights"];
    var k;
    for (k = 0; k < lights.length; k++) {
	if (lights[k].type === "DL") {
	    lights[k].prototype = directionalight;
	}
	if (lights[k].type === "PL") {
	    lights[k].prototype = pointlight;
	}
    }

    //prep the canvas
    var canvas = document.getElementById("myCanvas");
    var ctx = canvas.getContext("2d");
    var imgData = ctx.createImageData(cam.pixelsX, cam.pixelsY);

    var shapes = shapeobject(objects["shapes"]);
    var rayt = raytracer(shapes, lights, cam.eye, 5);
    var picture = scene(cam, rayt, imgData);
    ctx.putImageData(imgData, 0, 0);
    //console.dir(picture);
    console.log("done");
}());