package io.github.charlietap.chasm.error

import kotlin.jvm.JvmInline

sealed interface TypeDecodeError : WasmDecodeError {
    @JvmInline
    value class InvalidNumberType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidVectorType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidReferenceType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidValueType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidFunctionType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class UnknownLimitsFlag(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class UnknownMutabilityFlag(val encoded: UByte) : TypeDecodeError
}
