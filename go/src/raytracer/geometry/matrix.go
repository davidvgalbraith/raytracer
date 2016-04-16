package geometry

import (
	"math"
	. "raytracer/common"
)

type Matrix struct {
	Rows    int
	Columns int
	Data    [][]float64
}

func (m1 Matrix) Times(m2 Matrix) Matrix {
	newmat := Identity(m1.Rows, m2.Columns)
	for i := 0; i < newmat.Rows; i++ {
		for j := 0; j < newmat.Columns; j++ {
			sum := 0.0
			for k := 0; k < m1.Columns; k++ {
				sum += m1.Data[i][k] * m2.Data[k][j]
			}
			newmat.Data[i][j] = sum
		}
	}
	return newmat
}

func (matrix Matrix) HomogeneousTimes(v Vector, fourthColumn float64) Vector {
	result := []float64{0.0, 0.0, 0.0, 0.0}
	homogeneousArray := []float64{v.X, v.Y, v.Z, fourthColumn}
	for i := 0; i < matrix.Rows; i++ {
		for j := 0; j < matrix.Columns; j++ {
			result[i] += matrix.Data[i][j] * homogeneousArray[j]
		}
	}

	if fourthColumn == 1 {
		return BuildVector([]float64{result[0] / result[3], result[1] / result[3], result[2] / result[3]})
	} else {
		return BuildVector([]float64{result[0], result[1], result[2]})
	}
}

func (matrix Matrix) Transpose() Matrix {
	result := Identity(matrix.Columns, matrix.Rows)
	for i := 0; i < matrix.Rows; i++ {
		for j := 0; j < matrix.Columns; j++ {
			result.Data[j][i] = matrix.Data[i][j]
		}
	}
	return result
}

func Identity(rows, columns int) Matrix {
	var data = make([][]float64, rows)
	for row := 0; row < rows; row++ {
		data[row] = make([]float64, columns)
		for col := 0; col < columns; col++ {
			data[row][col] = TernaryFloat(row == col, 1.0, 0.0)
		}
	}

	return Matrix{
		Rows:    rows,
		Columns: columns,
		Data:    data,
	}
}

func ScaleMatrix(x, y, z float64) Matrix {
	matrix := Identity(4, 4)
	matrix.Data[0][0] = x
	matrix.Data[1][1] = y
	matrix.Data[2][2] = z

	return matrix
}

func TranslateMatrix(x, y, z float64) Matrix {
	matrix := Identity(4, 4)
	matrix.Data[0][3] = x
	matrix.Data[1][3] = y
	matrix.Data[2][3] = z

	return matrix
}

func RotationMatrix(x, y, z float64) Matrix {
	rotx := axisMatrix("x", radians(x))
	roty := axisMatrix("y", radians(y))
	rotz := axisMatrix("z", radians(z))

	return rotx.Times(roty).Times(rotz)
}

// returns the inverse matrix of RotationMatrix(x, y, z)
// NOT the same as RotationMatrix(-x, -y, -z) since matrix multiplication does not commute
func DeRotationMatrix(x, y, z float64) Matrix {
	rotx := axisMatrix("x", radians(-x))
	roty := axisMatrix("y", radians(-y))
	rotz := axisMatrix("z", radians(-z))

	return rotz.Times(roty).Times(rotx)
}

func axisMatrix(axis string, theta float64) Matrix {
	matrix := Identity(4, 4)
	switch axis {
	case "x":
		matrix.Data[1][1] = math.Cos(theta)
		matrix.Data[1][2] = -math.Sin(theta)
		matrix.Data[2][1] = math.Sin(theta)
		matrix.Data[2][2] = math.Cos(theta)
	case "y":
		matrix.Data[0][0] = math.Cos(theta)
		matrix.Data[0][2] = -math.Sin(theta)
		matrix.Data[2][0] = math.Sin(theta)
		matrix.Data[2][2] = math.Cos(theta)
	case "z":
		matrix.Data[0][0] = math.Cos(theta)
		matrix.Data[0][1] = -math.Sin(theta)
		matrix.Data[1][0] = math.Sin(theta)
		matrix.Data[1][1] = math.Cos(theta)

	default:
		panic("tried to rotate bogus axis " + axis)
	}

	return matrix
}

func radians(degrees float64) float64 {
	return (math.Pi / 180) * degrees
}
