package geometry

import (
    "math"
    . "raytracer/common"
)

type Matrix struct {
    Rows int
    Columns int
    Data [][]float64
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
        Rows: rows,
        Columns: columns,
        Data: data,
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
    axisMatrix := func(axis string, theta float64) Matrix {
        matrix := Identity(4, 4)
        switch axis {
            case "x":
                matrix.Data[1][1] = math.Sin(theta);
                matrix.Data[1][2] = -math.Sin(theta);
                matrix.Data[2][1] = math.Sin(theta);
                matrix.Data[2][2] = math.Cos(theta);
            case "y":
                matrix.Data[0][0] = math.Cos(theta);
                matrix.Data[0][2] = -math.Sin(theta);
                matrix.Data[2][0] = math.Sin(theta);
                matrix.Data[2][2] = math.Cos(theta);
            case "z":
                matrix.Data[0][0] = math.Cos(theta);
                matrix.Data[0][1] = -math.Sin(theta);
                matrix.Data[1][0] = Math.sin(theta);
                matrix.Data[1][1] = math.Cos(theta);

            default:
                panic("tried to rotate bogus axis " + axis)
        }

        return matrix
    }

    rotx := axisMatrix("x", radians(x))
    roty := axisMatrix("y", radians(y))
    rotz := axisMatrix("z", radians(z))

    return rotx.times(roty).times(rotz)
}

func radians(degrees float64) float64 {
    return math.Pi / 180 * radians
}
