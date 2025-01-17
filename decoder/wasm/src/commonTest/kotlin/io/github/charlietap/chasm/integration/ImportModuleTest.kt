package io.github.charlietap.chasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.WasmModuleDecoder
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportModuleTest {

    @Test
    fun `can decode an import module section`() {

        val byteStream = Resource("src/commonTest/resources/import.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)
        val decoder = WasmModuleDecoder()

        val expectedFunctionType = FunctionType(
            params = ResultType(emptyList()),
            results = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        val expectedFunctionImportType = Type(Index.TypeIndex(0u), expectedFunctionType)

        val expectedFunctionImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalFunction"),
            descriptor = Import.Descriptor.Function(Index.TypeIndex(0u)),
        )

        val expectedTableType = TableType(ReferenceType.Funcref, Limits(1u, 2u))

        val expectedTableImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalTable"),
            descriptor = Import.Descriptor.Table(expectedTableType),
        )

        val expectedMemoryType = MemoryType(Limits(1u, 2u))

        val expectedMemoryImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalMemory"),
            descriptor = Import.Descriptor.Memory(expectedMemoryType),
        )

        val expectedGlobalType = GlobalType(
            ValueType.Number(NumberType.I32),
            GlobalType.Mutability.Const,
        )

        val expectedGlobalImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalGlobal"),
            descriptor = Import.Descriptor.Global(expectedGlobalType),
        )

        val expected = Ok(
            Module(
                version = Version.One,
                types = listOf(expectedFunctionImportType),
                imports = listOf(
                    expectedFunctionImport,
                    expectedTableImport,
                    expectedMemoryImport,
                    expectedGlobalImport,
                ),
                functions = emptyList(),
                tables = emptyList(),
                memories = emptyList(),
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
