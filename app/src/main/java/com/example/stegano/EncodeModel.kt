package com.example.stegano

import android.graphics.Bitmap
import android.util.Log

class EncodeModel: CryptoManagerAES() {
    // Data frame structure:
    // [PACKAGE_BORDER][code.size][code==[password.size][password][encrypted message]][PACKAGE_BORDER]
    // [    2 Bytes   ][ 1  Byte ][1+k+n=[    1 Byte   ][k  Bytes][     n  Bytes    ]][    2 Bytes   ]

    companion object{
        private const val BYTE_SIZE = 8
        private const val NO_OF_COLOR_CHANNELS = 3
        private val PACKAGE_BORDER = ByteArray(2) { _ -> 0x00 }
        private const val MINIMAL_FRAME_SIZE = 8
    }

    private fun getBitFromByte(value: Int, position: Int): Int {
        return (value shr position) and 1
    }

    private fun setBitInColor(value: Int, bit: Int): Int {
        return if(bit == 1){
            (value or 0x01)
        }else{
            (value and 0xFE)
        }
    }

    private fun steganoLSB(packageData: ByteArray, coverImage: Bitmap): Bitmap{
        val width = coverImage.width
        val height = coverImage.height

        val dataFrame = PACKAGE_BORDER + packageData + PACKAGE_BORDER

//        Log.d("DEV", "Package data: ${dataFrame.decodeToString()}\n" +
//                "\t\t\t${dataFrame.sliceArray(2..dataFrame.size-3).decodeToString()}\n" +
//                "Byte size: ${dataFrame.size}" +
//                "\nBitsize: ${dataFrame.size}*${8}=${dataFrame.size * Byte.SIZE_BITS}")

        val minimalSizeRequired = dataFrame.size
        val totalNoOfBits = minimalSizeRequired * BYTE_SIZE

        if(totalNoOfBits > width * height * NO_OF_COLOR_CHANNELS){
            throw Exception("Image is too small")
        }

        var newImage = coverImage
        var byteIndex = 0
        var bitIndex = BYTE_SIZE - 1

        for(y in 0 until height){
            for(x in 0 until width){
                if(byteIndex >= minimalSizeRequired) break

                var pixel = newImage.getPixel(x, y)
                val alpha = pixel shr 24 and 0xff
                var red = pixel shr 16 and 0xff
                var green = pixel shr 8 and 0xff
                var blue = pixel and 0xff

                red = setBitInColor(red, getBitFromByte(dataFrame[byteIndex].toInt(), bitIndex))
                bitIndex--
                if(bitIndex < 0){
                    byteIndex++
                    bitIndex = BYTE_SIZE - 1
                }

                if(byteIndex < minimalSizeRequired){
                    green = setBitInColor(green, getBitFromByte(dataFrame[byteIndex].toInt(), bitIndex))
                    bitIndex--
                    if(bitIndex < 0) {
                        byteIndex++
                        bitIndex = BYTE_SIZE - 1
                    }
                }

                if(byteIndex < minimalSizeRequired){
                    blue = setBitInColor(blue, getBitFromByte(dataFrame[byteIndex].toInt(), bitIndex))
                    bitIndex--
                    if(bitIndex < 0){
                        byteIndex++
                        bitIndex = BYTE_SIZE - 1
                    }
                }

                pixel = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
                newImage.setPixel(x, y, pixel)
            }
        }

        return newImage
    }

    private fun readLSB(coverImage: Bitmap): ByteArray{
        val width = coverImage.width
        val height = coverImage.height

        var bitIndex = 0
        var currentByte = 0
        var bytes = ByteArray(0)

        for(y in 0 until height) {
            for (x in 0 until width) {

                val pixel = coverImage.getPixel(x, y)
                val redBit = pixel shr 16 and 0x01
                val greenBit = pixel shr 8 and 0x01
                val blueBit = pixel and 0x01

//                Getting bytes out of pixels
                currentByte = (currentByte shl 1) or redBit
                bitIndex++
                if(bitIndex == BYTE_SIZE) {
                    bytes += currentByte.toByte()
                    bitIndex = 0
                    currentByte = 0
                }

                currentByte = (currentByte shl 1) or greenBit
                bitIndex++
                if(bitIndex == BYTE_SIZE) {
                    bytes += currentByte.toByte()
                    bitIndex = 0
                    currentByte = 0
                }

                currentByte = (currentByte shl 1) or blueBit
                bitIndex++
                if(bitIndex == BYTE_SIZE) {
                    bytes += currentByte.toByte()
                    bitIndex = 0
                    currentByte = 0
                }

//                Checking if the image is steganographed
                if(bytes.size == 2 && !(bytes.contentEquals(PACKAGE_BORDER))){
                    throw Exception("Image is not steganographed")
                }

//                Checking if I reached the end of the frame
                if(bytes.size >= MINIMAL_FRAME_SIZE &&
                    bytes.sliceArray(bytes.size - 2..<bytes.size).contentEquals(PACKAGE_BORDER)){
                    return bytes
                }
            }
        }

        return ByteArray(0)
    }

    fun encapsulate(message: ByteArray, pass: ByteArray, coverImage: Bitmap): Bitmap{
        var code = byteArrayOf(pass.size.toByte())
        val encryptedMessage = super.encrypt(message)
        code += pass + encryptedMessage

        var packageData = ByteArray(1)
        packageData[0] = code.size.toByte()

        packageData += code

//        Log.d("DEV", "Password: ${pass.decodeToString()}\tEncrypted message: ${encryptedMessage.decodeToString()}" +
//                "\nCode: ${code.decodeToString()}" +
//                "\nPackage data: ${packageData.decodeToString()}")

        return steganoLSB(packageData, coverImage)
    }

    fun decapsulate(coverImage: Bitmap, password: ByteArray): ByteArray{
        val dataFrame : ByteArray

        try {
            dataFrame = readLSB(coverImage)
            if(dataFrame.size < MINIMAL_FRAME_SIZE){
                throw(Exception("Image is not steganographed"))
            }
        }catch (e: Exception){
            throw e
        }

        val packageData = dataFrame
            .sliceArray(PACKAGE_BORDER.size..<dataFrame.size - PACKAGE_BORDER.size)

        val codeSize = packageData[0].toInt()
        val code = packageData.sliceArray(1..<packageData.size)

        if(code.size != codeSize){
            Log.w("Size mismatch", "Code size: ${code.size}\t" +
                    "Code size expected: $codeSize\n" +
                    "Is also a very small possibility that the image is not steganographed.")
//            throw Exception("Size mismatch, or image is not steganographed")
        }

        val passwordSize = code[0].toInt()
        val pass = code.sliceArray(1..passwordSize)
        val encryptedMessage = code.sliceArray(passwordSize + 1..<code.size)

//        Log.d("DEV", "Password: ${pass.decodeToString()}\tEncrypted message: ${encryptedMessage.decodeToString()}" +
//                "\nCode: ${code.decodeToString()}" +
//                "\nPackage data: ${packageData.decodeToString()}")

        if(!pass.contentEquals(password)){
            throw Exception("Incorrect password")
        }

        return super.decrypt(encryptedMessage)
    }
}