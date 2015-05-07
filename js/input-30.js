{
    "name":"scene1",
    "camera": {
	"eye":[ 0,  0,  0],
	"LL":[-1, -1, -3],
	"LR":[ 1, -1, -3],
	"UR":[ 1,  1, -3],
	"UL":[-1,  1, -3],
	"pixelsX":1000,
	"pixelsY":1000
    },
    "lights": [
	{
	    "type":"directional",
	    "direction": [ 0.57735027, -0.57735027, -0.57735027 ],
	    "color":[ 1,  1,  1]
	},
	{
	    "type":"directional",
	    "direction": [ -0.57735027, 0.57735027, 0.57735027 ],
	    "color":[ 1,  1,  1]
	}
    ],
    "shapes": [
	{
	    "type":"ellipsoid",
	    "center":[  0, 0, -17 ],
	    "rx":4.0,
	    "ry":2.0,
	    "rz":2.0,
	    "rotx":0,
	    "roty":0,
	    "rotz":0,
	    "ka":[ 0.1,  0.1,  0.1 ],
	    "kd":[ 1,  0,  0 ],
	    "ks":[ 1,  1,  1 ],
	    "kr":[ 0.9, 0.9,  0.9 ],
	    "sp":50.0
	},
	{
	    "type":"ellipsoid",
	    "center":[  -2, 4, -17 ],
	    "rx":0.5,
	    "ry":1.5,
	    "rz":1.0,
	    "rotx":0,
	    "roty":-45,
	    "rotz":-45,
	    "ka":[ 0.1,  0.1,  0.1 ],
	    "kd":[ 0,  1,  0 ],
	    "ks":[ 1,  1,  1 ],
	    "kr":[ 0.9, 0.9,  0.9 ],
	    "sp":50.0
	},
	{
	    "type":"ellipsoid",
	    "center":[  -2, -4, -17 ],
	    "rx":0.5,
	    "ry":1.5,
	    "rz":1.0,
	    "rotx":0,
	    "roty":-45,
	    "rotz":45,
	    "ka":[ 0.1,  0.1,  0.1 ],
	    "kd":[ 0,  0,  1 ],
	    "ks":[ 1,  1,  1 ],
	    "kr":[ 0.9, 0.9,  0.9 ],
	    "sp":50.0
	},
	{
	    "type":"ellipsoid",
	    "center":[  2, 4, -17 ],
	    "rx":0.5,
	    "ry":1.5,
	    "rz":1.0,
	    "rotx":0,
	    "roty":45,
	    "rotz":-135,
	    "ka":[ 0.1,  0.1,  0.1 ],
	    "kd":[ 1,  1,  0 ],
	    "ks":[ 1,  1,  1 ],
	    "kr":[ 0.9, 0.9,  0.9 ],
	    "sp":50.0
	},
	{
	    "type":"ellipsoid",
	    "center":[  2, -4, -17 ],
	    "rx":0.5,
	    "ry":1.5,
	    "rz":1.0,
	    "rotx":0,
	    "roty":45,
	    "rotz":135,
	    "ka":[ 0.1,  0.1,  0.1 ],
	    "kd":[ 0, 1, 1 ],
	    "ks":[ 1,  1,  1 ],
	    "kr":[ 0.9, 0.9,  0.9 ],
	    "sp":50.0
	}
    ]
}
