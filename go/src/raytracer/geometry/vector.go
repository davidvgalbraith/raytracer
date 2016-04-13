package geometry

import (
	"math"
	"math/rand"
)

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

var ZERO_VECTOR = BuildVector([]float64{0.0, 0.0, 0.0})

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

func (v1 Vector) Cross(v2 Vector) Vector {
	x := v1.Y * v2.Z - v1.Z * v2.Y
	y := v1.Z * v2.X - v1.X * v2.Z
	z := v1.X * v2.Y - v1.Y * v2.X

	return BuildVector([]float64{x, y, z})
}

func (v Vector) Abs() Vector {
	return BuildVector([]float64{math.Abs(v.X), math.Abs(v.Y), math.Abs(v.Z)})
}

func (v Vector) Contains(value float64) bool {
	return v.X == value || v.Y == value || v.Z == value
}

func (v Vector) Jitter() Vector {
	return BuildVector([]float64{randy(v.X), randy(v.Y), randy(v.Z)})
}

func randy(input float64) float64 {
	return input + (rand.Float64() / 5.0)
}
