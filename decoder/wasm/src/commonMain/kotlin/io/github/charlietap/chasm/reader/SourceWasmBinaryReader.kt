package io.github.charlietap.chasm.reader

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.SourceReader
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.ext.toDoubleLe
import io.github.charlietap.chasm.ext.toFloatLe
import io.github.charlietap.chasm.ext.toIntLeb128
import io.github.charlietap.chasm.ext.toLongLeb128
import io.github.charlietap.chasm.ext.toUIntLeb128

class SourceWasmBinaryReader(
    private val source: SourceReader,
) : WasmBinaryReader {

    private var position = 0u
    private val byteStream = sequence {
        while (true) {
            position++
            yield(source.byte())
        }
    }
    private val unsignedByteStream = byteStream.map { it.toUByte() }

    override fun byte(): Result<Byte, WasmDecodeError> = tryRead {
        position++
        source.byte()
    }

    override fun ubyte(): Result<UByte, WasmDecodeError> = tryRead {
        position++
        source.byte().toUByte()
    }

    override fun bytes(amount: Int): Result<ByteArray, WasmDecodeError> = tryRead {
        position += amount.toUInt()
        source.bytes(amount)
    }

    override fun ubytes(amount: UInt): Result<UByteArray, WasmDecodeError> = tryRead {
        position += amount
        source.bytes(amount.toInt()).asUByteArray()
    }

    override fun int(): Result<Int, WasmDecodeError> = tryRead { byteStream.toIntLeb128() }

    override fun uint(): Result<UInt, WasmDecodeError> = tryRead { unsignedByteStream.toUIntLeb128() }

    override fun long(): Result<Long, WasmDecodeError> = tryRead { byteStream.toLongLeb128() }

    override fun float(): Result<Float, WasmDecodeError> = binding {
        val bytes = bytes(4).bind()

        bytes.toFloatLe().bind()
    }

    override fun double(): Result<Double, WasmDecodeError> = binding {
        val bytes = bytes(8).bind()

        bytes.toDoubleLe().bind()
    }

    override fun exhausted(): Result<Boolean, WasmDecodeError> = tryRead { source.exhausted() }

    override fun position(): UInt = position

    override fun peek(): WasmBinaryReader = SourceWasmBinaryReader(source.peek())

    companion object {
        fun <T> tryRead(
            block: () -> T,
        ): Result<T, WasmDecodeError> = runCatching(block)
            .mapError { err -> WasmDecodeError.IOError(err) }
    }
}
