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
            return ray(vector(camera.origin), screenpos);
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
        var shade = function(light, lightray, collision) {
            var spec = speculate(light, lightray, collision);
            var diff = diffuse(light, lightray, collision);
            return spec.plus(diff);
        }
        //handle specular shading
        var speculate = function(light, lightray, collision) {
            var l = lightray.direction.normalize();
            var n = collision.normal.direction;
            var r = l.times(-1).plus(n.times(2 * n.dot(l))).normalize();
            var v = origin.minus(collision.normal.position).normalize();
            var ks = vector(collision.shape.shading.specular);
            var sp = collision.shape.shading.specular_exponent;
            var dot = Math.max(r.dot(v), 0);
            dot = Math.pow(dot, sp);

            return vector(light.color).vtimes(ks).times(dot);
        }
        //handle diffuse shading
        var diffuse = function(light, lightray, collision) {
            var l = lightray.direction.normalize();
            var dot = l.dot(collision.normal.direction);
            var kd = vector(collision.shape.shading.diffuse);
            return vector(light.color).vtimes(kd).times(dot);
        }
        rt.trace = function(rayr, reflections) {
            origin = rayr.position;
            if (reflections > maxReflections) {
                return vector([0, 0, 0]);
            }
            //surface normal and shape or null
            var collision = shapes.intersect(rayr);
            if (!collision) {
                return vector([0, 0, 0]);
            }
            var color = vector([0, 0, 0]);
            var ka = collision.shape.shading.ambient;
            for (var l = 0; l < lights.length; l++) {
                var light = lights[l];
                if (reflections === 0) {
                    color = color.plus(vector(ka).vtimes(vector(light.color)));
                }
                if (light.type === "directional") {
                    var lightray = ray(collision.normal.position, vector(light.direction).times(-1));
                    if (!shapes.intersect(lightray)) {
                        color = color.plus(shade(light, lightray, collision));
                    }
                }
            }
            //handle reflection
            var kr = vector(collision.shape.shading.reflection);
            if (kr.exceeds(0)) {
                var reflectdir = (rayr.direction.normalize().times(-1).plus(collision.normal.direction.times(2 * collision.normal.direction.dot(rayr.direction.normalize())))).times(-1);
                color = color.plus(rt.trace(ray(collision.normal.position, reflectdir), reflections + 1).vtimes(kr));
            }
            return color;
        }
        return rt;
    }

    var shapeobject = function(shapes) {
        return {
            //returns a ray normal to the surface of closest intersection
            intersect: function(rayr) {
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
                        var thit = Math.max(t1, t2);
                        if (thit < 0.001) {
                            continue;
                        }
                        if (t2 < 0.001) {
                            thit = t1;
                        } else {
                            thit = t2;
                        }
                        if (thit < tmin) {
                            ret = ret || {};
                            tmin = thit;
                            var localIntersection = transray.valueAt(thit)
                            var localNormalDirection = localIntersection.minus(vector(shape.center))
                            ret.normal = ray(
                                shape.objToWorld.homomult(localIntersection, 1),
                                shape.worldToObj.transpose().homomult(localNormalDirection, 0).normalize()
                            );
                            ret.shape = shape;
                        }
                    }
                    if (shape.type === "triangle") {
                        //incomprehensible sequence of operations to find intersection
                        var ve = transray.position;
                        var vd = transray.direction;
                        var vc = vector(shape.vertices[2]);
                        var vb = vector(shape.vertices[1]);
                        var va = vector(shape.vertices[0]);
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
        };
    }

    var ray = function(position, direction) {
        return {
            position: position,
            direction: direction,
            valueAt: function(t) {
                return position.plus(direction.times(t));
            }
        };
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
                console.error("Tried to normalize a zero vector");
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
    inputFile.open("GET", "http://localhost:12345/red-ellipsoid", false);
    inputFile.overrideMimeType("application/json");
    inputFile.send(null);
    var objects = JSON.parse(inputFile.responseText);

    //parse the input file
    var cam = objects["camera"];
    cam.UL = vector(cam.view_plane.upper_left);
    cam.UR = vector(cam.view_plane.upper_right);
    cam.LL = vector(cam.view_plane.lower_left);
    var lights = objects["lights"];
    for (var shapeIndex = 0; shapeIndex < objects["shapes"].length; shapeIndex++) {
        var shape = objects["shapes"][shapeIndex];
        if (shape.type === "ellipsoid") {
            var radii = shape.radii;
            var rotations = shape.rotation;

            var scale = matrix(4, 4);
            scale.data[0][0] = radii.x;
            scale.data[1][1] = radii.y;
            scale.data[2][2] = radii.z;

            var translate = matrix(4, 4);
            translate.data[0][3] = shape.center[0];
            translate.data[1][3] = shape.center[1];
            translate.data[2][3] = shape.center[2];

            var rotx = rotation("x", Math.PI/180 * rotations.x);
            var roty = rotation("y", Math.PI/180 * rotations.y);
            var rotz = rotation("z", Math.PI/180 * rotations.z);

            var rotate = rotx.times(roty).times(rotz);

            var objToWorld = rotate.times(scale);
            objToWorld = translate.times(objToWorld);

            var descale = matrix(4, 4);
            descale.data[0][0] = 1/radii.x;
            descale.data[1][1] = 1/radii.y;
            descale.data[2][2] = 1/radii.z;

            var detranslate = matrix(4, 4);
            detranslate.data[0][3] = -shape.center[0];
            detranslate.data[1][3] = -shape.center[1];
            detranslate.data[2][3] = -shape.center[2];

            var derotx = rotation("x", -Math.PI/180 * rotations.x);
            var deroty = rotation("y", -Math.PI/180 * rotations.y);
            var derotz = rotation("z", -Math.PI/180 * rotations.z);

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
    var rayt = raytracer(shapes, lights, cam.origin, 5);
    var picture = scene(cam, rayt, imgData);
    ctx.putImageData(imgData, 0, 0);

    //profit

}());
