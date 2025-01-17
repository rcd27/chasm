package io.github.charlietap.chasm.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.instruction.BLOCK
import io.github.charlietap.chasm.decoder.instruction.BR
import io.github.charlietap.chasm.decoder.instruction.BR_IF
import io.github.charlietap.chasm.decoder.instruction.BR_TABLE
import io.github.charlietap.chasm.decoder.instruction.BinaryInstructionBlockDecoder
import io.github.charlietap.chasm.decoder.instruction.CALL
import io.github.charlietap.chasm.decoder.instruction.CALL_INDIRECT
import io.github.charlietap.chasm.decoder.instruction.END
import io.github.charlietap.chasm.decoder.instruction.IF
import io.github.charlietap.chasm.decoder.instruction.InstructionBlockDecoder
import io.github.charlietap.chasm.decoder.instruction.LOOP
import io.github.charlietap.chasm.decoder.instruction.NOP
import io.github.charlietap.chasm.decoder.instruction.RETURN
import io.github.charlietap.chasm.decoder.instruction.UNREACHABLE
import io.github.charlietap.chasm.decoder.section.index.BinaryFunctionIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryLabelIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryTableIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryTypeIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryControlInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryControlInstructionDecoder(
        reader = reader,
        opcode = opcode,
        blockTypeDecoder = ::BinaryBlockTypeDecoder,
        instructionBlockDecoder = ::BinaryInstructionBlockDecoder,
        ifDecoder = ::BinaryIfDecoder,
        functionIndexDecoder = ::BinaryFunctionIndexDecoder,
        typeIndexDecoder = ::BinaryTypeIndexDecoder,
        tableIndexDecoder = ::BinaryTableIndexDecoder,
        labelIndexDecoder = ::BinaryLabelIndexDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
    )

internal fun BinaryControlInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    blockTypeDecoder: BlockTypeDecoder,
    instructionBlockDecoder: InstructionBlockDecoder,
    ifDecoder: IfDecoder,
    functionIndexDecoder: FunctionIndexDecoder,
    typeIndexDecoder: TypeIndexDecoder,
    tableIndexDecoder: TableIndexDecoder,
    labelIndexDecoder: LabelIndexDecoder,
    vectorDecoder: VectorDecoder<Index.LabelIndex>,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        UNREACHABLE -> ControlInstruction.Unreachable
        NOP -> ControlInstruction.Nop
        BLOCK -> {
            val blockType = blockTypeDecoder(reader).bind()
            val instructions = instructionBlockDecoder(reader, END).bind()
            ControlInstruction.Block(blockType, instructions)
        }
        LOOP -> {
            val blockType = blockTypeDecoder(reader).bind()
            val instructions = instructionBlockDecoder(reader, END).bind()
            ControlInstruction.Loop(blockType, instructions)
        }
        IF -> {
            val blockType = blockTypeDecoder(reader).bind()
            val (thenInstructions, elseInstructions) = ifDecoder(reader).bind()
            ControlInstruction.If(blockType, thenInstructions, elseInstructions)
        }
        BR -> {
            val idx = labelIndexDecoder(reader).bind()
            ControlInstruction.Br(idx)
        }
        BR_IF -> {
            val idx = labelIndexDecoder(reader).bind()
            ControlInstruction.BrIf(idx)
        }
        BR_TABLE -> {
            val indices = vectorDecoder(reader, labelIndexDecoder).bind()
            val default = labelIndexDecoder(reader).bind()
            ControlInstruction.BrTable(indices.vector, default)
        }
        RETURN -> ControlInstruction.Return
        CALL -> {
            val idx = functionIndexDecoder(reader).bind()
            ControlInstruction.Call(idx)
        }
        CALL_INDIRECT -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val tableIndex = tableIndexDecoder(reader).bind()
            ControlInstruction.CallIndirect(typeIndex, tableIndex)
        }

        else -> Err(InstructionDecodeError.InvalidControlInstruction(opcode)).bind<Instruction>()
    }
}
