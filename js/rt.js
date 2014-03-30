(function() {
    //universal loop index
    var k;
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

    //prep the canvas
    var c = document.getElementById("myCanvas");
    var ctx = c.getContext("2d");
    var imgData = ctx.createImageData(1000, 1000);
   
    //read the input file
    var inputFile = new XMLHttpRequest();
    inputFile.open("GET", "file:///home/dave/ray/js/input-00.txt", false);
    inputFile.send(null);
    var text = inputFile.responseText;
    var objects = JSON.parse(text);
    
    //parse the input file
    var cam = objects["camera"];
    cam.prototype = camera;
    var flm = objects["film"];
    flm.prototype = film;
    var samp = sampler(flm.pixelsX, flm.pixelsY);
    var lights = objects["lights"];
    for (k = 0; k < lights.length; k++) {
	if (lights[k].type === "DL") {
	    lights[k].prototype = directionalight;
	}
	if (lights[k].type === "PL") {
	    lights[k].prototype = pointlight;
	}
    }
    for (k = 0; k < shapes.length; k++) {
	if (shapes[k].type === "sphere") {
	    shapes[k].prototype = sphere;
	}
	if (shapes[k].type === "triangle") {
	    shapes[k].prototype = triangle;
	}
    }
    var ray = raytracer(shapes, lights, cam.eye, 5);
    var picture = scene(samp, cam, flm, ray);
    imgData.data = picture;
    ctx.putImageData(imgData, 0, 0);
}());