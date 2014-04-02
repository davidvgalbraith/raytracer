//a javascript raytracer
//too slow to be useful but I like it anyway

(function() {

    //scene and its discontents

    var scene = function(camera, raytracer, imgData) {
	var x;
	//send a ray through the viewing plane
	var generateRay = function(x, y) {
	    var xpos = camera.UL.x + (x / camera.pixelsX) * (camera.UR.x - camera.UL.x) + 1 / (2 * camera.pixelsX);
	    var ypos = camera.UL.y + (y / camera.pixelsY) * (camera.LL.y - camera.UL.y) + 1 / (2 * camera.pixelsY);
	    var screenpos = vector([xpos, ypos, camera.UL.z]);
	    return ray(vector(camera.eye), screenpos);
	}
	for (x = 0; x < camera.pixelsX * camera.pixelsY * 4; x += 4) {
	    var xfour = x / 4;
	    var raaay = generateRay(xfour % camera.pixelsX, Math.floor(xfour / camera.pixelsX));
	    var color = raytracer.trace(raaay, 0).times(255).floor();
	    imgData.data[x] = color.x > 255 ? 255 : color.x;
	    imgData.data[x+1] = color.y > 255 ? 255 : color.y;
	    imgData.data[x+2] = color.z > 255 ? 255 : color.z;
	    imgData.data[x+3] = 255;
	}
	return imgData.data;
    }

    var raytracer = function(shapes, lights, origin, maxReflections) {
	var rt = {};
	var origin;
	//calculate the color given the normal vector and the light source
	var shade = function(light, lightray, inter) {
	    var spec = speculate(light, lightray, inter);
	    var diff = diffuse(light, lightray, inter);
	    return spec.plus(diff);
	}
	//handle specular shading
	var speculate = function(light, lightray, inter) {
	    var l = lightray.direction.normalize();
	    var n = inter.normal.direction;
	    var r = l.times(-1).plus(n.times(2 * n.dot(l))).normalize();
	    var v = origin.minus(inter.normal.position).normalize();
	    var dot = r.dot(v);
	    if (dot < 0) {
		dot = 0;
	    }
	    dot = Math.pow(dot, inter.shape.sp);
	    return vector(light.color).vtimes(vector(inter.shape.ks)).times(dot);
	}
	//handle diffuse shading
	var diffuse = function(light, lightray, inter) {
	    var l = lightray.direction.normalize();
	    var dot = l.dot(inter.normal.direction);
	    return vector(light.color).vtimes(vector(inter.shape.kd)).times(dot);
	}
	rt.trace = function(rayr, reflections) {
	    origin = rayr.position;
	    if (reflections > maxReflections) {
		return vector([0, 0, 0]);
	    }
	    //surface normal and shape or null
	    var inter = shapes.intersect(rayr);
	    if (!inter) {
		return vector([0, 0, 0]);
	    }
	    var color = vector([0, 0, 0]);
	    for (var l = 0; l < lights.length; l++) {
		var light = lights[l];
		if (reflections === 0) {
		    color = color.plus(vector(inter.shape.ka).vtimes(vector(light.color)));
		}
		if (light.type === "directional") {
		    var lightray = ray(inter.normal.position, vector(light.direction).times(-1));
		    if (!shapes.intersect(lightray)) {
			color = color.plus(shade(light, lightray, inter));
		    }
		}
	    }
	    //handle reflection
	    if (vector(inter.shape.kr).exceeds(0)) {
		var reflectdir = (rayr.direction.normalize().times(-1).plus(inter.normal.direction.times(2 * inter.normal.direction.dot(rayr.direction.normalize())))).times(-1);
		color = color.plus(rt.trace(ray(inter.normal.position, reflectdir), reflections + 1).vtimes(vector(inter.shape.kr)));
	    }
	    return color;
	}
	return rt;
    }
    
    var shapeobject = function(shapes) {
	var o = {};
	//returns a ray normal to the surface of closest intersection
	o.intersect = function(rayr) {
	    var tmin = Infinity;
	    var ret = null;
	    for (var k = 0; k < shapes.length; k++) {
		var shape = shapes[k];
		if (shape.type === "sphere") {
		    var emc = rayr.position.minus(vector(shape.center));
		    var a = rayr.direction.dot(rayr.direction);
		    var b = 2 * rayr.direction.dot(emc);
		    var c = emc.dot(emc) - shape.radius * shape.radius;
		    var disc = b * b - 4 * a * c;		    		 
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
		    if (thit < tmin && !ret) {
			ret = {};
		    }
		    if (thit < tmin) {
			tmin = thit; 
			ret.normal = ray(rayr.valueAt(thit), rayr.valueAt(thit).minus(vector(shape.center)).normalize());
			ret.shape = shape;
		    }
		}
		if (shape.type === "ellipsoid") {
		    var scale = matrix(4, 4);
		    scale.setValueAt(0, 0, shape.rx);
		    scale.setValueAt(1, 1, shape.ry);
		    scale.setValueAt(2, 2, shape.rz);
		    var translate = matrix(4, 4);
		    translate.setValueAt(0, 3, shape.center[0]);
		    translate.setValueAt(1, 3, shape.center[1]);
		    translate.setValueAt(2, 3, shape.center[2]);
		    var rotx = matrix.rotation("x", Math.PI/180 * shape.rotx);
		    var roty = matrix.rotation("y", Math.PI/180 * shape.roty);
		    var rotz = matrix.rotation("z", Math.PI/180 * shape.rotz);
		    var rotate = rotx.times(roty).times(rotz);
		    var objToWorld = rotate.times(scale);
		    objToWorld = translate.times(objToWorld);
		    var descale = matrix(4, 4);
		    descale.setValueAt(0, 0, 1/shape.rx);
		    descale.setValueAt(1, 1, 1/shape.ry);
		    descale.setValueAt(2, 2, 1/shape.rz);
		    var detranslate = matrix(4, 4);
		    detranslate.setValueAt(0, 3, -shape.center[0]);
		    detranslate.setValueAt(1, 3, -shape.center[1]);
		    detranslate.setValueAt(2, 3, -shape.center[2]);
		    var derotx = matrix.rotation("x", -Math.PI/180 * shape.rotx);
		    var deroty = matrix.rotation("y", -Math.PI/180 * shape.roty);
		    var derotz = matrix.rotation("z", -Math.PI/180 * shape.rotz);
		    var derotate = derotz.times(deroty).times(derotx);
		    var worldToObj = derotate.times(detranslate);
		    worldToObj = descale.times(worldToObj);
		}
		if (shape.type === "triangle") {
		    //incomprehensible sequence of operations to find intersection
		    var ve = rayr.position;
		    var vd = rayr.direction;
		    var vc = vector(shape.vc);
		    var vb = vector(shape.vb);
		    var va = vector(shape.va);
		    var a = va.x - vb.x;
		    var b = va.y - vb.y;
		    var c = va.z - vb.z;
		    var d = va.x - vc.x;
		    var e = va.y - vc.y;
		    var f = va.z - vc.z;
		    var g = vd.x;
		    var h = vd.y;
		    var i = vd.z;
		    var j = va.x - ve.x;
		    var kk = va.y - ve.y;
		    var l = va.z - ve.z;
		    var eihf = e * i - h * f;
		    var gfdi = g * f - d * i;
		    var dheg = d * h - e * g;
		    var akjb = a * kk - j * b;
		    var jcal = j * c - a * l;
		    var blkc = b * l - kk * c;
		    var m = a * eihf + b * gfdi + c * dheg;
		    var beta = (j * eihf + kk * gfdi + l * dheg) / m;
		    var gamma = (i * akjb + h * jcal + g * blkc) / m;
		    var t  = -1 * (f * akjb + e * jcal + d * blkc) / m;
		    if (t < .001 || t > tmin || gamma < 0 || gamma > 1 || beta < 0 || beta > 1-gamma) {
			continue;
		    }
		    ret = {};
		    tmin = t;
		    if (shape.normals) {
			ret.normal = ray(rayr.valueAt(t), (shape.normals[0].times(1-beta-gamma).plus(shape.normals[1].times(beta)).plus(shape.normals[2].times(gamma))).normalize());
			ret.shape = shape;
		    } else {
			ret.normal = ray(rayr.valueAt(t), ((vb.minus(va)).cross(vc.minus(va))).normalize());
			ret.shape = shape;
		    }
		}
	    }
	    return ret;
	}
	return o;
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
	v.vtimes = function(w) {
	    return vector([v.x * w.x, v.y * w.y, v.z * w.z]);
	}
	v.dot = function(w) {
	    return v.x * w.x + v.y * w.y + v.z * w.z;
	}
	v.normalize = function() {
	    var norm = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
	    if (norm === 0) {
		console.log("Tried to normalize a zero vector");
		return this;
	    }
	    return vector([v.x / norm, v.y / norm, v.z / norm]);
	}
	v.floor = function() {
	    return vector([Math.floor(v.x), Math.floor(v.y), Math.floor(v.z)]);
	}
	v.cross = function(w) {
	    return vector([v.y * w.z - v.z * w.y, v.z * w.x - v.x * w.z, v.x * w.y - v.y * w.x]);
	}
	v.exceeds = function(a) {
	    return v.x > a || v.y > a || v.z > a;
	}
	return v;
    }

    //matrix operations that we'll be needing for transformations
    var matrix = function(rows, colums) {
	//gives you the rotation of theta radians about axis axis
	var rotation = function(axis, theta) {
	    var ret = matrix(4, 4);
	    if (axis == "x") {
		ret.setValueAt(1, 1, Math.cos(theta));
		ret.setValueAt(1, 2, -Math.sin(theta));
		ret.setValueAt(2, 1, Math.sin(theta));
		ret.setValueAt(2, 2, Math.cos(theta));
	    }
	    if (axis == "y") {
		ret.setValueAt(0, 0, Math.cos(theta));
		ret.setValueAt(0, 2, -Math.sin(theta));
		ret.setValueAt(2, 0, Math.sin(theta));
		ret.setValueAt(2, 2, Math.cos(theta));
	    }
	    if (axis == "z") {
		ret.setValueAt(0, 0, Math.cos(theta));
		ret.setValueAt(0, 1, -Math.sin(theta));
		ret.setValueAt(1, 0, Math.sin(theta));
		ret.setValueAt(1, 1, Math.cos(theta));
	    }
	    return ret;
	}
	var m = {};
	m.data = [];
	m.rows = rows;
	m.colums = colums;
	//initialize to idenity
	for (var row = 0; row < rows; row++) {
	    m.data[row] = [];
	    for (var col = 0; col < colums; col++) {
		m.data[row][col] = (row === col) ? 1 : 0;
	    }
	}
	m.setValueAt = function(row, colum, value) {
	    m.data[row][colum] = value;
	}
	m.times = function(n) {
	    var ii, jj, kk;
	    var newmat = matrix(m.rows, n.colums);
	    for (ii = 0; ii < newmat.rows; ii++) {
		for (jj = 0; jj < newmat.colums; jj++) {
		    var sum = 0;
		    for (kk = 0; kk < m.colums; kk++) {
			sum += m.data[ii][kk] * n.data[kk][jj];
		    }
		    newmat.setValueAt(ii, jj, sum);
		}
	    }
	    return newmat;
	}
	//multiply the matrix by vect, an array representing a 4-vector
	m.vectortimes = function(vect) {
	    var ii, jj;
	    if (m.colums !== vect.length) {
		console.log("Invalid matrix-vector produck");
	    }
	    var result = zeros(m.rows);
	    for (ii = 0; ii < m.rows; ii++) {
		for (jj = 0; jj < m.colums; jj++) {
		    result[ii] += m.valueAt(ii, jj) * vect[jj];
		}
	    }
	    return result;
	}
	return m;
    }

    var zeros = function(n) {
	var result = [];
	for (var kthzero = 0; kthzero < n; kthzero++) {
	    result.push(0);
	}
	return result;
    }
    
    //read the input file
    var inputFile = new XMLHttpRequest();
    inputFile.open("GET", "file:///home/dave/ray/js/input-10.js", false);
    inputFile.overrideMimeType("application/json");
    inputFile.send(null);
    var objects = JSON.parse(inputFile.responseText);
    
    //parse the input file
    var cam = objects["camera"];
    cam.UL = vector(cam.UL);
    cam.UR = vector(cam.UR);
    cam.LL = vector(cam.LL);
    cam.LR = vector(cam.LR);
    var lights = objects["lights"];

    //prep the canvas
    var canvas = document.getElementById("myCanvas");
    var ctx = canvas.getContext("2d");
    var imgData = ctx.createImageData(cam.pixelsX, cam.pixelsY);

    //draw the picture
    var shapes = shapeobject(objects["shapes"]);
    var rayt = raytracer(shapes, lights, cam.eye, 5);
    var picture = scene(cam, rayt, imgData);
    ctx.putImageData(imgData, 0, 0);
    
    //profit

}());
