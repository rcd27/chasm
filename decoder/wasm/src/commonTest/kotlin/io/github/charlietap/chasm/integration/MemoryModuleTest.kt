package io.github.charlietap.chasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.WasmModuleDecoder
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryModuleTest {

    @Test
    fun `can decode a memory module section`() {

        val byteStream = Resource("src/commonTest/resources/memory.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)
        val decoder = WasmModuleDecoder()

        val expectedMemory = Memory(
            idx = Index.MemoryIndex(0u),
            type = MemoryType(
                limits = Limits(1u, 2u),
            ),
        )

        val expected = Ok(
            Module(
                version = Version.One,
                types = emptyList(),
                imports = emptyList(),
                functions = emptyList(),
                tables = emptyList(),
                memories = listOf(expectedMemory),
                globals = emptyList(),
                exports = emptyList(),
                startFunction = null,
                elementSegments = emptyList(),
                dataSegments = emptyList(),
                customs = emptyList(),
            ),
        )

        val actual = decoder(reader)

        assertEquals(expected, actual)
    }
}
