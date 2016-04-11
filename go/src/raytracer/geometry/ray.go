package geometry

type Ray struct {
	Position Vector
	Direction Vector
}

func (r Ray) ValueAt(time float64) Vector {
	return r.Position.Plus(r.Direction.Times(time))
}

func BuildRay(position, direction Vector) Ray {
	return Ray{
		Position: position,
		Direction: direction,
	}
}
