package common

import (
    "image"
    "image/png"
    "math"
    "os"
)

func Panick(err error) {
	if (err != nil) {
		panic(err)
	}
}

func WriteImage(img *image.RGBA) {
	f, err := os.Create("draw.png")
	if err != nil {
		panic(err)
	}
	defer f.Close()
	png.Encode(f, img)
}

func TernaryInt(condition bool, ifInt int, elseInt int) int {
    if (condition) {
        return ifInt
    } else {
        return elseInt
    }
}

func TernaryFloat(condition bool, ifFloat float64, elseFloat float64) float64 {
    if (condition) {
        return ifFloat
    } else {
        return elseFloat
    }
}

func Min(nums ... float64) float64 {
    min := math.Inf(1)
    for _, num := range nums {
        min = math.Min(min, num)
    }

    return min
}

func Max(nums ... float64) float64 {
    min := math.Inf(-1)
    for _, num := range nums {
        min = math.Max(min, num)
    }

    return min
}
