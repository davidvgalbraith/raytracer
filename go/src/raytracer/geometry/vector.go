package geometry

import ("math")

type Vector struct {
	X float64
	Y float64
	Z float64
}

func BuildVector(elements []float64) Vector {
	return Vector{
		X: elements[0],
		Y: elements[1],
		Z: elements[2],
	}
}

func (v1 Vector) Plus(v2 Vector) Vector {
	return BuildVector([]float64{v1.X + v2.X, v1.Y + v2.Y, v1.Z + v2.Z})
}

func (v1 Vector) Minus(v2 Vector) Vector {
	return BuildVector([]float64{v1.X - v2.X, v1.Y - v2.Y, v1.Z - v2.Z})
}

func (v1 Vector) Times(m float64) Vector {
	return BuildVector([]float64{v1.X * m, v1.Y * m, v1.Z * m})
}

func (v1 Vector) Vtimes(v2 Vector) Vector {
	return BuildVector([]float64{v1.X * v2.X, v1.Y * v2.Y, v1.Z * v2.Z})
}

func (v1 Vector) Dot(v2 Vector) float64 {
	return (v1.X * v2.X) + (v1.Y * v2.Y) + (v1.Z * v2.Z)
}

func (v Vector) Norm() float64 {
	return math.Sqrt(v.X * v.X + v.Y * v.Y + v.Z * v.Z);
}

func (v Vector) Normalize() Vector {
	norm := v.Norm()
	if norm == 0 { panic("Tried to normalize a zero vector") }

	return BuildVector([]float64{v.X / norm, v.Y / norm, v.Z / norm})
}
