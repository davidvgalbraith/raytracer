(function() {

    //scene and its components

    var scene = function(sampler, camera, film, raytracer) {}
    var sampler = function(xdim, ydim) {}
    var camera = function() {}
    var film = function() {}
    var raytracer = function(shapes, lights, origin, maxReflections) {}
    
    var directionalight = function() {}
    var pointlight = function() {}
    var sphere = function() {}
    var triangle = function() {}

    //helper functions to extract lights and shapes from a file
    var getLights = function(objects) {
	var lights = [];
	for (var k = 2; k < objects.length; k++) {
	    var light = objects[k];
	    if (light.type === "DL") {
		light.prototype = directionalight;
		lights.push(light);
	    }
	    if (light.type === "PL") {
		light.prototype = pointlight;
		lights.push(light);
	    }
	}
	return lights;
    }

    var getShapes = function(objects) {
	var shapes = [];
	for (var k = 2; k < objects.length; k++) {
	    var shape = objects[k];
	    if (shape.type === "sphere") {
		shape.prototype = sphere;
		shapes.push(shape);
	    }
	    if (shape.type === "triangle") {
		shape.prototype = triangle;
		shapes.push(shape);
	    }
	}
	return shapes;
    }
    //prep the canvas
    var c = document.getElementById("myCanvas");
    var ctx = c.getContext("2d");
    var imgData = ctx.createImageData(100, 100);
   
    //read the input file
    var inputFile = new XMLHttpRequest();
    inputFile.open("GET", "file:///home/dave/ray/js/input-00.txt", false);
    inputFile.send(null);
    var text = inputFile.responseText;
    var objects = JSON.parse(text);
    
    //parse the input file
    var cam = objects[0];
    cam.prototype = camera;
    var flm = objects[1];
    flm.prototype = film;
    var lights = getLights(objects);
    var shapes = getShapes(objects);
    
    //draw
    for (var i = 0; i < imgData.data.length; i += 4) {
	imgData.data[i] = 0;
	imgData.data[i+1] = 0;
	imgData.data[i+2] = 0;
	imgData.data[i+3] = 255;
    }
    ctx.putImageData(imgData, 0, 0);
}());