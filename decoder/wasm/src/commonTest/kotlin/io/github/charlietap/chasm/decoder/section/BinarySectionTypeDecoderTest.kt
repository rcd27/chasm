package io.github.charlietap.chasm.decoder.section

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.error.SectionDecodeError
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeUByteReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.section.SectionType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinarySectionTypeDecoderTest {

    @Test
    fun `can decode section types`() {

        val sectionTypes = SectionType.entries

        val sequence = sectionTypes.asSequence().iterator()

        val reader = FakeUByteReader {
            Ok(sequence.next().id)
        }

        val actual = (1..sectionTypes.size).map {
            BinarySectionTypeDecoder(reader)
        }

        val expected = sectionTypes.map(::Ok)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns invalid section type error on mismatch`() {

        val invalidSectionType: UByte = 0x7Fu

        val reader = FakeUByteReader {
            Ok(invalidSectionType)
        }

        val actual = BinarySectionTypeDecoder(reader)

        assertEquals(Err(SectionDecodeError.UnknownSectionType(invalidSectionType)), actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val actual = BinarySectionTypeDecoder(reader)

        assertEquals(err, actual)
    }
}
