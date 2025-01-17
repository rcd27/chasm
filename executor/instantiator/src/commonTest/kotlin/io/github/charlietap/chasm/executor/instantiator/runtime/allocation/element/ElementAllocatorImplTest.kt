package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.element

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementAllocatorImplTest {

    @Test
    fun `can allocate a element instance`() {

        val elements = mutableListOf<ElementInstance>()
        val store = store(
            elements = elements,
        )

        val refType = ReferenceType.Funcref

        val expected = ElementInstance(
            referenceType = refType,
            references = mutableListOf(),
        )

        val address = ElementAllocatorImpl(store, refType, emptyList())

        assertEquals(Address.Element(0), address)
        assertEquals(expected, elements[0])
    }
}
