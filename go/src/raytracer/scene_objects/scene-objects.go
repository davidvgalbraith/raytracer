package scene_objects

type Shading struct {
	Ambient           []float64
	Diffuse           []float64
	Reflection        []float64
	Specular          []float64
	Specular_Exponent float64
}

type Camera struct {
	Origin    []float64
	ViewPlane struct {
		Lower_Left  []float64
		Lower_Right []float64
		Upper_Left  []float64
		Upper_Right []float64
	} `json:"view_plane"`
	PixelsX int
	PixelsY int
}

type Light struct {
	Type      string
	Direction []float64
	Color     []float64
}
