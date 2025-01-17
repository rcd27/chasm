package io.github.charlietap.chasm.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.BlockType
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.instruction.BLOCK
import io.github.charlietap.chasm.decoder.instruction.BR
import io.github.charlietap.chasm.decoder.instruction.BR_IF
import io.github.charlietap.chasm.decoder.instruction.BR_TABLE
import io.github.charlietap.chasm.decoder.instruction.CALL
import io.github.charlietap.chasm.decoder.instruction.CALL_INDIRECT
import io.github.charlietap.chasm.decoder.instruction.END
import io.github.charlietap.chasm.decoder.instruction.IF
import io.github.charlietap.chasm.decoder.instruction.InstructionBlockDecoder
import io.github.charlietap.chasm.decoder.instruction.LOOP
import io.github.charlietap.chasm.decoder.instruction.NOP
import io.github.charlietap.chasm.decoder.instruction.RETURN
import io.github.charlietap.chasm.decoder.instruction.UNREACHABLE
import io.github.charlietap.chasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryControlInstructionDecoderTest {

    @Test
    fun `can decode an UNREACHABLE instruction`() {

        val opcode = UNREACHABLE
        val expected = Ok(ControlInstruction.Unreachable)

        val actual = BinaryControlInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a NOP instruction`() {

        val opcode = NOP
        val expected = Ok(ControlInstruction.Nop)

        val actual = BinaryControlInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BLOCK instruction`() {

        val opcode = BLOCK
        val expectedBlockType = BlockType.Empty
        val expectedInstructions = emptyList<Instruction>()
        val expected = Ok(ControlInstruction.Block(expectedBlockType, expectedInstructions))

        val blockTypeDecoder: BlockTypeDecoder = { _ ->
            Ok(expectedBlockType)
        }

        val instructionBlockDecoder: InstructionBlockDecoder = { _, endBlockOp ->
            assertEquals(END, endBlockOp)
            Ok(expectedInstructions)
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = blockTypeDecoder,
            instructionBlockDecoder = instructionBlockDecoder,
            ifDecoder = neverIfDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            vectorDecoder = neverVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a LOOP instruction`() {

        val opcode = LOOP
        val expectedBlockType = BlockType.Empty
        val expectedInstructions = emptyList<Instruction>()
        val expected = Ok(ControlInstruction.Loop(expectedBlockType, expectedInstructions))

        val blockTypeDecoder: BlockTypeDecoder = { _ ->
            Ok(expectedBlockType)
        }

        val instructionBlockDecoder: InstructionBlockDecoder = { _, endBlockOp ->
            assertEquals(END, endBlockOp)
            Ok(expectedInstructions)
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = blockTypeDecoder,
            instructionBlockDecoder = instructionBlockDecoder,
            ifDecoder = neverIfDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            vectorDecoder = neverVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a IF instruction`() {

        val opcode = IF
        val expectedBlockType = BlockType.Empty
        val expectedInstructions = emptyList<Instruction>()
        val expected = Ok(ControlInstruction.If(expectedBlockType, expectedInstructions, null))

        val blockTypeDecoder: BlockTypeDecoder = { _ ->
            Ok(expectedBlockType)
        }

        val ifDecoder: IfDecoder = { _ ->
            Ok(expectedInstructions to null)
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = blockTypeDecoder,
            instructionBlockDecoder = neverInstructionBlockDecoder,
            ifDecoder = ifDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            vectorDecoder = neverVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR instruction`() {

        val opcode = BR
        val expectedLabelIndex = Index.LabelIndex(117u)
        val expected = Ok(ControlInstruction.Br(expectedLabelIndex))

        val labelIndexDecoder: LabelIndexDecoder = { _ ->
            Ok(expectedLabelIndex)
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = neverBlockTypeDecoder,
            instructionBlockDecoder = neverInstructionBlockDecoder,
            ifDecoder = neverIfDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            vectorDecoder = neverVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR_IF instruction`() {

        val opcode = BR_IF
        val expectedLabelIndex = Index.LabelIndex(117u)
        val expected = Ok(ControlInstruction.BrIf(expectedLabelIndex))

        val labelIndexDecoder: LabelIndexDecoder = { _ ->
            Ok(expectedLabelIndex)
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = neverBlockTypeDecoder,
            instructionBlockDecoder = neverInstructionBlockDecoder,
            ifDecoder = neverIfDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            vectorDecoder = neverVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a BR_TABLE instruction`() {

        val opcode = BR_TABLE
        val expectedLabelVec = emptyList<Index.LabelIndex>()
        val expectedDefaultLabelIndex = Index.LabelIndex(117u)
        val expected = Ok(ControlInstruction.BrTable(expectedLabelVec, expectedDefaultLabelIndex))

        val labelIndexDecoder: LabelIndexDecoder = { _ ->
            Ok(expectedDefaultLabelIndex)
        }

        val vectorDecoder: VectorDecoder<Index.LabelIndex> = { _, _ ->
            Ok(Vector(expectedLabelVec))
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = neverBlockTypeDecoder,
            instructionBlockDecoder = neverInstructionBlockDecoder,
            ifDecoder = neverIfDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = labelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            vectorDecoder = vectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an RETURN instruction`() {

        val opcode = RETURN
        val expected = Ok(ControlInstruction.Return)

        val actual = BinaryControlInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an CALL instruction`() {

        val opcode = CALL
        val expectedFunctionIndex = Index.FunctionIndex(117u)
        val expected = Ok(ControlInstruction.Call(expectedFunctionIndex))

        val functionIndexDecoder: FunctionIndexDecoder = { _ ->
            Ok(expectedFunctionIndex)
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = neverBlockTypeDecoder,
            instructionBlockDecoder = neverInstructionBlockDecoder,
            ifDecoder = neverIfDecoder,
            functionIndexDecoder = functionIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            vectorDecoder = neverVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an CALL_INDIRECT instruction`() {

        val opcode = CALL_INDIRECT
        val expectedTypeIndex = Index.TypeIndex(117u)
        val expectedTableIndex = Index.TableIndex(118u)
        val expected = Ok(ControlInstruction.CallIndirect(expectedTypeIndex, expectedTableIndex))

        val typeIndexDecoder: TypeIndexDecoder = { _ ->
            Ok(expectedTypeIndex)
        }

        val tableIndexDecoder: TableIndexDecoder = { _ ->
            Ok(expectedTableIndex)
        }

        val actual = BinaryControlInstructionDecoder(
            reader = FakeWasmBinaryReader(),
            opcode = opcode,
            blockTypeDecoder = neverBlockTypeDecoder,
            instructionBlockDecoder = neverInstructionBlockDecoder,
            ifDecoder = neverIfDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
            vectorDecoder = neverVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown control instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidControlInstruction(opcode))

        val actual = BinaryControlInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    private companion object {
        private val neverBlockTypeDecoder: BlockTypeDecoder = { _ ->
            fail("block type decoder should not run in this scenario")
        }
        private val neverInstructionBlockDecoder: InstructionBlockDecoder = { _, _ ->
            fail("instruction block decoder should not run in this scenario")
        }
        private val neverIfDecoder: IfDecoder = { _ ->
            fail("if decoder should not run in this scenario")
        }
        private val neverLabelIndexDecoder: LabelIndexDecoder = { _ ->
            fail("label index decoder should not run in this scenario")
        }
        private val neverFunctionIndexDecoder: FunctionIndexDecoder = { _ ->
            fail("function index decoder should not run in this scenario")
        }
        private val neverTypeIndexDecoder: TypeIndexDecoder = { _ ->
            fail("type index decoder should not run in this scenario")
        }
        private val neverTableIndexDecoder: TableIndexDecoder = { _ ->
            fail("table index decoder should not run in this scenario")
        }
        private val neverVectorDecoder: VectorDecoder<Index.LabelIndex> = { _, _ ->
            fail("vector decoder should not run in this scenario")
        }
    }
}
