package geometry

import (
    . "raytracer/common"
)

type Matrix struct {
    Rows int
    Columns int
    Data [][]float64
}

func Identity(rows int, columns int) Matrix {
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
