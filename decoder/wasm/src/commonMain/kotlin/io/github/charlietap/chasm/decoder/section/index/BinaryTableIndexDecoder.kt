package io.github.charlietap.chasm.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryTableIndexDecoder(
    reader: WasmBinaryReader,
): Result<Index.TableIndex, WasmDecodeError> = BinaryIndexDecoder(reader, Index::TableIndex)