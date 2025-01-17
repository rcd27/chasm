package io.github.charlietap.chasm.decoder.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.instruction.DROP
import io.github.charlietap.chasm.decoder.instruction.SELECT
import io.github.charlietap.chasm.decoder.instruction.SELECT_W_TYPE
import io.github.charlietap.chasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryParametricInstructionDecoderTest {

    @Test
    fun `can decode DROP instruction`() {

        val opcode = DROP

        val expected = Ok(ParametricInstruction.Drop)

        val actual = BinaryParametricInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode SELECT instruction`() {

        val opcode = SELECT

        val expected = Ok(ParametricInstruction.Select)

        val actual = BinaryParametricInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a SELECT_W_TYPE instruction`() {

        val opcode = SELECT_W_TYPE

        val valueTypeDecoder: ValueTypeDecoder = {
            fail("Value type decoder should never be called")
        }

        val expectedValueTypes = listOf(ValueType.Number(NumberType.I32))
        val vectorDecoder: VectorDecoder<ValueType> = { _, sub ->
            assertEquals(valueTypeDecoder, sub)
            Ok(Vector(expectedValueTypes))
        }
        val expected = Ok(ParametricInstruction.SelectWithType(expectedValueTypes))

        val actual = BinaryParametricInstructionDecoder(
            FakeWasmBinaryReader(),
            opcode,
            vectorDecoder,
            valueTypeDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidParametricInstruction(opcode))

        val actual = BinaryParametricInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }
}
