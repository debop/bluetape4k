package com.sksamuel.scrimage.examples

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.angles.Degrees
import com.sksamuel.scrimage.canvas.painters.LinearGradient
import com.sksamuel.scrimage.color.Colors
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import io.wrtn.kommons.junit5.folder.TempFolder
import io.wrtn.kommons.logging.KLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.awt.Color

class Examples: AbstractExampleTest() {

    companion object: KLogging()

    @Nested
    inner class CoverExample {
        @Test
        fun `cover image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // 이미지를 지정한 크기에 맞게 비율을 맞춰서 크기 변환을 합니다.
            image.cover(400, 300, ScaleMethod.FastScale, Position.TopCenter)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "cover_400_300_top_center.jpg"))

            image.cover(500, 200, Position.TopLeft)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "cover_500_200_top_left.jpg"))

            image.cover(400, 400)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "cover_400_400.jpg"))

            image.cover(400, 400, Position.CenterRight)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "cover_400_400_center_right.jpg"))
        }
    }

    @Nested
    inner class BrightnessExample {
        @Test
        fun `brightness image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // 밝기를 2배로 합니다.
            image.brightness(2.0)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "brightness_output_2.0.jpg"))

            // 밝기를 0.5배로 합니다.
            image.brightness(0.5)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "brightness_output_0.5.jpg"))
        }
    }

    @Nested
    inner class FillExample {
        @Test
        fun `fill image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // 단색으로 채우기
            image.fill(Color.BLUE)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "fill_blue.jpg"))

            // 수직 그라데이션으로 채우기
            image.fill(LinearGradient.vertical(Color.BLACK, Color.WHITE))
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "fill_linear_gradient_vertical.jpg"))

            // 수평 그라데이션으로 채우기
            image.fill(LinearGradient.horizontal(Color.BLACK, Color.WHITE))
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "fill_linear_gradient_horizontal.jpg"))
        }
    }

    @Nested
    inner class FitExample {
        @Test
        fun `fit image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // 이미지를 400x300 으로 bound 합니다. 기본은 ScaleMethod.BiCubic 입니다.
            image.fit(400, 300, Color.DARK_GRAY)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "fit_400_300.jpg"))

            image.fit(300, 300, Color.BLUE)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "fit_300_300.jpg"))

            image.fit(400, 100, Color.RED)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "fit_400_100.jpg"))
        }
    }

    @Nested
    inner class FlipExample {
        @Test
        fun `flip x-axis`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // 이미지를 x축으로 뒤집습니다.
            image.flipX()
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "flip_x.jpg"))
        }

        @Test
        fun `flip y-axis`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // 이미지를 y축으로 뒤집습니다.
            image.flipY()
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "flip_y.jpg"))
        }
    }

    @Nested
    inner class PadExample {
        @Test
        fun `fit image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // 이미지를 0.5배로 축소한 후 10px 의 여백을 추가합니다.
            image.scale(0.5)
                .pad(10, Color.DARK_GRAY)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "pad_10.jpg"))

            // 이미지를 0.5배로 축소한 후 25px 의 오른쪽 여백을 추가합니다.
            image.scale(0.5)
                .padRight(25, Color.BLUE)
                .padBottom(40, Color.RED)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "pad_r25_b40.jpg"))
        }
    }

    @Nested
    inner class RotateExample {
        @Test
        fun `rotate image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            image.rotateLeft()
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "rotate_left.jpg"))

            image.rotateRight()
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "rotate_right.jpg"))

            // Clockwise 방향으로 70도 회전 (뒷 배경은 투명으로)
            image.rotate(Degrees(70), Colors.Transparent.awt())
                .forWriter(PngWriter.MaxCompression)
                .write(getTargetPath(tempFolder, "rotate_70_degree.png"))

            // Clockwise 방향으로 115도 회전
            image.rotate(Degrees(115))
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "rotate_115_degree.jpg"))
        }
    }

    @Nested
    inner class ScaleExample {
        @Test
        fun `scale images`(tempFolder: TempFolder) {
            val image = ImmutableImage.loader()
                .fromResource("$EXAMPLE_BASE_PATH/input.jpg")

            image.scaleToWidth(400)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_w400.jpg"))

            image.scaleToHeight(200)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_h200.jpg"))

            image.scaleTo(400, 400)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_400_400.jpg"))

            image.scaleTo(400, 400, ScaleMethod.FastScale)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_400_400_fast.jpg"))

            image.scaleTo(400, 400, ScaleMethod.Progressive)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_400_400_progressive.jpg"))

            image.scale(0.5)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_0.5_progressive.jpg"))
        }

        @Test
        fun `scale method demo`(tempFolder: TempFolder) {
            val image = ImmutableImage.loader()
                .fromResource("$EXAMPLE_BASE_PATH/input.jpg")

            image.scaleToWidth(1600, ScaleMethod.FastScale)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_fast_scale.jpg"))

            image.scaleToWidth(1600, ScaleMethod.Bicubic)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_bicubic.jpg"))

            image.scaleToWidth(1600, ScaleMethod.Bilinear)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_bilinear.jpg"))

            image.scaleToWidth(1600, ScaleMethod.BSpline)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_bspline.jpg"))

            image.scaleToWidth(1600, ScaleMethod.Lanczos3)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_lanczos3.jpg"))

            image.scaleToWidth(1600, ScaleMethod.Progressive)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "scale_progressive.jpg"))
        }
    }

    @Nested
    inner class TakeExample {
        @Test
        fun `take some part from image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            image.takeLeft(300)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "take_left_300.jpg"))

            image.takeLeft(300).takeTop(200)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "take_left_300_top_200.jpg"))

            image.takeRight(400).takeBottom(200)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "take_right_400_bottom_200.jpg"))
        }
    }

    @Nested
    inner class Zoom {
        @Test
        fun `zoom image`(tempFolder: TempFolder) {
            val image = getExampleImage("input.jpg")

            // Zoom 은 기본적으로 scale 과 같다. ScaleMethods.Bicubic 이 기본이다.
            image.zoom(2.0)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "zoom_2.0.jpg"))

            image.zoom(1.3)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "zoom_1.3.jpg"))

            image.zoom(1.3, ScaleMethod.FastScale)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "zoom_1.3_fastscale.jpg"))

            image.zoom(1.3, ScaleMethod.Progressive)
                .forWriter(JpegWriter.Default)
                .write(getTargetPath(tempFolder, "zoom_1.3_progressive.jpg"))
        }
    }
}
