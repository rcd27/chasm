package io.github.charlietap.chasm.decoder.type.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.decoder.type.result.BinaryResultTypeDecoder
import io.github.charlietap.chasm.decoder.type.result.ResultTypeDecoder
import io.github.charlietap.chasm.error.TypeDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryFunctionTypeDecoder(
    reader: WasmBinaryReader,
): Result<FunctionType, WasmDecodeError> = BinaryFunctionTypeDecoder(
    reader,
    ::BinaryResultTypeDecoder,
)

internal fun BinaryFunctionTypeDecoder(
    reader: WasmBinaryReader,
    resultTypeDecoder: ResultTypeDecoder,
): Result<FunctionType, WasmDecodeError> = binding {
    val byte = reader.ubyte().bind()

    if (byte != 0x60.toUByte()) {
        Err(TypeDecodeError.InvalidFunctionType(byte)).bind<Nothing>()
    }

    val params = resultTypeDecoder(reader).bind()
    val results = resultTypeDecoder(reader).bind()

    FunctionType(params, results)
}
