package io.github.charlietap.chasm.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.decoder.instruction.DATA_DROP
import io.github.charlietap.chasm.decoder.instruction.ELEM_DROP
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F32_S
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F64_S
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F32_S
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F64_S
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.decoder.instruction.MEMORY_COPY
import io.github.charlietap.chasm.decoder.instruction.MEMORY_FILL
import io.github.charlietap.chasm.decoder.instruction.MEMORY_INIT
import io.github.charlietap.chasm.decoder.instruction.TABLE_COPY
import io.github.charlietap.chasm.decoder.instruction.TABLE_FILL
import io.github.charlietap.chasm.decoder.instruction.TABLE_GROW
import io.github.charlietap.chasm.decoder.instruction.TABLE_INIT
import io.github.charlietap.chasm.decoder.instruction.TABLE_SIZE
import io.github.charlietap.chasm.decoder.section.index.BinaryDataIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryElementIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryTableIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryPrefixInstructionDecoder(
    reader: WasmBinaryReader,
    prefix: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryPrefixInstructionDecoder(
        reader = reader,
        prefix = prefix,
        dataIndexDecoder = ::BinaryDataIndexDecoder,
        elementIndexDecoder = ::BinaryElementIndexDecoder,
        tableIndexDecoder = ::BinaryTableIndexDecoder,
    )

internal fun BinaryPrefixInstructionDecoder(
    reader: WasmBinaryReader,
    prefix: UByte,
    dataIndexDecoder: DataIndexDecoder,
    elementIndexDecoder: ElementIndexDecoder,
    tableIndexDecoder: TableIndexDecoder,
): Result<Instruction, WasmDecodeError> = binding {

    when (val opcode = reader.uint().bind()) {

        I32_TRUNC_SAT_F32_S -> NumericInstruction.I32TruncSatF32S
        I32_TRUNC_SAT_F32_U -> NumericInstruction.I32TruncSatF32U
        I32_TRUNC_SAT_F64_S -> NumericInstruction.I32TruncSatF64S
        I32_TRUNC_SAT_F64_U -> NumericInstruction.I32TruncSatF64U
        I64_TRUNC_SAT_F32_S -> NumericInstruction.I64TruncSatF32S
        I64_TRUNC_SAT_F32_U -> NumericInstruction.I64TruncSatF32U
        I64_TRUNC_SAT_F64_S -> NumericInstruction.I64TruncSatF64S
        I64_TRUNC_SAT_F64_U -> NumericInstruction.I64TruncSatF64U

        MEMORY_INIT -> {
            val dataIdx = dataIndexDecoder(reader).bind()
            reader.byte() // consume reserved byte
            MemoryInstruction.MemoryInit(dataIdx)
        }

        DATA_DROP -> {
            val dataIdx = dataIndexDecoder(reader).bind()
            MemoryInstruction.DataDrop(dataIdx)
        }

        MEMORY_COPY -> {
            reader.bytes(2) // consume two reserved bytes
            MemoryInstruction.MemoryCopy
        }

        MEMORY_FILL -> {
            reader.byte() // consume reserved byte
            MemoryInstruction.MemoryFill
        }

        TABLE_INIT -> {
            val elemIdx = elementIndexDecoder(reader).bind()
            val tableIdx = tableIndexDecoder(reader).bind()
            TableInstruction.TableInit(elemIdx, tableIdx)
        }
        ELEM_DROP -> {
            val elemIdx = elementIndexDecoder(reader).bind()
            TableInstruction.ElemDrop(elemIdx)
        }
        TABLE_COPY -> {
            val srcTableIdx = tableIndexDecoder(reader).bind()
            val destTableIdx = tableIndexDecoder(reader).bind()
            TableInstruction.TableCopy(srcTableIdx, destTableIdx)
        }
        TABLE_GROW -> {
            val tableIdx = tableIndexDecoder(reader).bind()
            TableInstruction.TableGrow(tableIdx)
        }
        TABLE_SIZE -> {
            val tableIdx = tableIndexDecoder(reader).bind()
            TableInstruction.TableSize(tableIdx)
        }
        TABLE_FILL -> {
            val tableIdx = tableIndexDecoder(reader).bind()
            TableInstruction.TableFill(tableIdx)
        }

        else -> Err(InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode)).bind<Instruction>()
    }
}
