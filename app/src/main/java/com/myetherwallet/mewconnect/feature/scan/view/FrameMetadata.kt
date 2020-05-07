package com.myetherwallet.mewconnect.feature.scan.view

/**
 * Created by BArtWell on 06.05.2020.
 */

class FrameMetadata private constructor(var width: Int, var height: Int, var rotation: Int, var cameraFacing: Int) {

    class Builder {

        private var width = 0
        private var height = 0
        private var rotation = 0
        private var cameraFacing = 0

        fun setWidth(width: Int): Builder {
            this.width = width
            return this
        }

        fun setHeight(height: Int): Builder {
            this.height = height
            return this
        }

        fun setRotation(rotation: Int): Builder {
            this.rotation = rotation
            return this
        }

        fun setCameraFacing(facing: Int): Builder {
            cameraFacing = facing
            return this
        }

        fun build(): FrameMetadata {
            return FrameMetadata(width, height, rotation, cameraFacing)
        }
    }
}
