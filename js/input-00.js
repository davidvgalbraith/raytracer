{
    "name":"scene1",
    "camera": {
	"eye":[ 0,  0,  0],
	"LL":[-1, -1, -3],
	"LR":[ 1, -1, -3],
	"UR":[ 1,  1, -3],
	"UL":[-1,  1, -3],
	"pixelsX":200,
	"pixelsY":200
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
	"type":"sphere",
	"center":[  0, 0, -17 ],
	"radius":2.0,
	"ka":[ 0.1,  0.1,  0.1 ],
	"kd":[ 1,  0,  0 ],
	"ks":[ 1,  1,  1 ],
	"kr":[ 0.9, 0.9,  0.9 ],
	"sp":50.0
	}
    ]
}