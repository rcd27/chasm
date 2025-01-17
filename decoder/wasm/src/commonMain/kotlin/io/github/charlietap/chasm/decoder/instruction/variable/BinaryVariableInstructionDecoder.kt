package io.github.charlietap.chasm.decoder.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.decoder.instruction.GLOBAL_GET
import io.github.charlietap.chasm.decoder.instruction.GLOBAL_SET
import io.github.charlietap.chasm.decoder.instruction.LOCAL_GET
import io.github.charlietap.chasm.decoder.instruction.LOCAL_SET
import io.github.charlietap.chasm.decoder.instruction.LOCAL_TEE
import io.github.charlietap.chasm.decoder.section.index.BinaryGlobalIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryLocalIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.GlobalIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.LocalIndexDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryVariableInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryVariableInstructionDecoder(
        reader = reader,
        opcode = opcode,
        localIndexDecoder = ::BinaryLocalIndexDecoder,
        globalIndexDecoder = ::BinaryGlobalIndexDecoder,
    )

fun BinaryVariableInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    localIndexDecoder: LocalIndexDecoder,
    globalIndexDecoder: GlobalIndexDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        LOCAL_GET -> {
            VariableInstruction.LocalGet(localIndexDecoder(reader).bind())
        }
        LOCAL_SET -> {
            VariableInstruction.LocalSet(localIndexDecoder(reader).bind())
        }
        LOCAL_TEE -> {
            VariableInstruction.LocalTee(localIndexDecoder(reader).bind())
        }
        GLOBAL_GET -> {
            VariableInstruction.GlobalGet(globalIndexDecoder(reader).bind())
        }
        GLOBAL_SET -> {
            VariableInstruction.GlobalSet(globalIndexDecoder(reader).bind())
        }

        else -> Err(InstructionDecodeError.InvalidVariableInstruction(opcode)).bind<Instruction>()
    }
}
