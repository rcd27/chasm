package io.github.charlietap.chasm.executor.runtime.value

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.store.Address
import kotlin.jvm.JvmInline

sealed interface ReferenceValue : ExecutionValue {
    @JvmInline
    value class Null(val referenceType: ReferenceType) : ReferenceValue

    @JvmInline
    value class FunctionAddress(val address: Address.Function) : ReferenceValue

    @JvmInline
    value class ExternAddress(val address: Address.Extern) : ReferenceValue
}
