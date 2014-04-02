//a javascript raytracer

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
		var tpos = shape.worldToObj.homomult(rayr.position, 1);
		var tdir = shape.worldToObj.homomult(rayr.direction, 0);
		var transray = ray(tpos, tdir);
		if (shape.type === "sphere") {
		    var emc = transray.position.minus(vector(shape.center));
		    var a = transray.direction.dot(transray.direction);
		    var b = 2 * transray.direction.dot(emc);
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
			ret.normal = ray(shape.objToWorld.homomult(transray.valueAt(thit), 1), shape.worldToObj.transpose().homomult(transray.valueAt(thit).minus(vector(shape.center)), 0).normalize());
			ret.shape = shape;
		    }
		}
		if (shape.type === "triangle") {
		    //incomprehensible sequence of operations to find intersection
		    var ve = transray.position;
		    var vd = transray.direction;
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
			ret.normal = ray(transray.valueAt(t), (shape.normals[0].times(1-beta-gamma).plus(shape.normals[1].times(beta)).plus(shape.normals[2].times(gamma))).normalize());
			ret.shape = shape;
		    } else {
			ret.normal = ray(transray.valueAt(t), ((vb.minus(va)).cross(vc.minus(va))).normalize());
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
		return v;
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
	var m = {};
	m.rows = rows;
	m.colums = colums;
	m.data = [];
	//initialize to idenity
	for (var row = 0; row < rows; row++) {
	    m.data[row] = [];
	    for (var col = 0; col < colums; col++) {
		m.data[row][col] = (row === col) ? 1 : 0;
	    }
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
		    newmat.data[ii][jj] = sum;
		}
	    }
	    return newmat;
	}
	//homogeneous multiplication of this matrix by vect augmented by fourth
	m.homomult = function(vect, fourth) {
	    var i, j;
	    if (m.colums !== 4) {
		throw "Invalid quaternary transformation";
	    }
	    var result = zeros(m.rows);
	    var vecarray = [vect.x, vect.y, vect.z, fourth];
	    for (i = 0; i < m.rows; i++) {
		for (j = 0; j < m.colums; j++) {
		    result[i] += m.data[i][j] * vecarray[j];
		}
	    }
	    if (fourth === 1) {
		return vector([result[0] / result[3], result[1] / result[3], result[2] / result[3]]);
	    } else {
		return vector([result[0], result[1], result[2]]);
	    }
	}
	m.transpose = function() { 
	    var i, j;
	    var ret = matrix(m.colums, m.rows);
	    for (i = 0; i < m.rows; i++) {
		for (j = 0; j < m.colums; j++) {
		    ret.data[j][i] = m.data[i][j];
		}
	    }
	    return ret;
	}
	return m;
    }

    //gives you the rotation matrix of theta radians about axis axis
    var rotation = function(axis, theta) {
	var ret = matrix(4, 4);
	if (axis == "x") {
	    ret.data[1][1] = Math.cos(theta);
	    ret.data[1][2] = -Math.sin(theta);
	    ret.data[2][1] = Math.sin(theta);
	    ret.data[2][2] = Math.cos(theta);
	}
	if (axis == "y") {
	    ret.data[0][0] = Math.cos(theta);
	    ret.data[0][2] = -Math.sin(theta);
	    ret.data[2][0] = Math.sin(theta);
	    ret.data[2][2] = Math.cos(theta);
	}
	if (axis == "z") {
	    ret.data[0][0] = Math.cos(theta);
	    ret.data[0][1] = -Math.sin(theta);
	    ret.data[1][0] = Math.sin(theta);
	    ret.data[1][1] = Math.cos(theta);
	}
	return ret;
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
    inputFile.open("GET", "file:///home/dave/ray/js/input-30.js", false);
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
    for (var shapeIndex = 0; shapeIndex < objects["shapes"].length; shapeIndex++) {
	var shape = objects["shapes"][shapeIndex];
	if (shape.type === "ellipsoid") {
	    var scale = matrix(4, 4);
	    scale.data[0][0] = shape.rx;
	    scale.data[1][1] = shape.ry;
	    scale.data[2][2] = shape.rz;
	    var translate = matrix(4, 4);
	    translate.data[0][3] = shape.center[0];
	    translate.data[1][3] = shape.center[1];
	    translate.data[2][3] = shape.center[2];
	    var rotx = rotation("x", Math.PI/180 * shape.rotx);
	    var roty = rotation("y", Math.PI/180 * shape.roty);
	    var rotz = rotation("z", Math.PI/180 * shape.rotz);
	    var rotate = rotx.times(roty).times(rotz);
	    var objToWorld = rotate.times(scale);
	    objToWorld = translate.times(objToWorld);
	    var descale = matrix(4, 4);
	    descale.data[0][0] = 1/shape.rx;
	    descale.data[1][1] = 1/shape.ry;
	    descale.data[2][2] = 1/shape.rz;
	    var detranslate = matrix(4, 4);
	    detranslate.data[0][3] = -shape.center[0];
	    detranslate.data[1][3] = -shape.center[1];
	    detranslate.data[2][3] = -shape.center[2];
	    var derotx = rotation("x", -Math.PI/180 * shape.rotx);
	    var deroty = rotation("y", -Math.PI/180 * shape.roty);
	    var derotz = rotation("z", -Math.PI/180 * shape.rotz);
	    var derotate = derotz.times(deroty).times(derotx);
	    var worldToObj = derotate.times(detranslate);
	    worldToObj = descale.times(worldToObj);
	    shape.objToWorld = objToWorld;
	    shape.worldToObj = worldToObj;
	    shape.radius = 1;
	    shape.center = [0, 0, 0];
	    shape.type = "sphere";
	} else {
	    shape.worldToObj = matrix(4, 4);
	    shape.objToWorld = matrix(4, 4);
	}
    }
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
