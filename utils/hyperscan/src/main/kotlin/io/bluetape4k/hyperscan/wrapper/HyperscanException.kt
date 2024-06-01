package io.bluetape4k.hyperscan.wrapper

import com.gliwka.hyperscan.jni.hyperscan.HS_ARCH_ERROR
import com.gliwka.hyperscan.jni.hyperscan.HS_BAD_ALIGN
import com.gliwka.hyperscan.jni.hyperscan.HS_BAD_ALLOC
import com.gliwka.hyperscan.jni.hyperscan.HS_COMPILER_ERROR
import com.gliwka.hyperscan.jni.hyperscan.HS_DB_MODE_ERROR
import com.gliwka.hyperscan.jni.hyperscan.HS_DB_PLATFORM_ERROR
import com.gliwka.hyperscan.jni.hyperscan.HS_DB_VERSION_ERROR
import com.gliwka.hyperscan.jni.hyperscan.HS_INSUFFICIENT_SPACE
import com.gliwka.hyperscan.jni.hyperscan.HS_INVALID
import com.gliwka.hyperscan.jni.hyperscan.HS_NOMEM
import com.gliwka.hyperscan.jni.hyperscan.HS_SCAN_TERMINATED
import com.gliwka.hyperscan.jni.hyperscan.HS_SCRATCH_IN_USE
import com.gliwka.hyperscan.jni.hyperscan.HS_UNKNOWN_ERROR


fun hyperscanExceptionOf(hsError: Int): HyperscanException = when (hsError) {
    HS_INVALID            -> HyperscanException("An invalid parameter has been passed. Is scratch allocated?")
    HS_NOMEM              -> HyperscanException("Hyperscan was unable to allocate memory")
    HS_SCAN_TERMINATED    -> HyperscanException("The engine was terminated by callback.")
    HS_COMPILER_ERROR     -> HyperscanException("The pattern compiler failed.")
    HS_DB_VERSION_ERROR   -> HyperscanException("The given database was built for a different version of Hyperscan.")
    HS_DB_PLATFORM_ERROR  -> HyperscanException("The given database was built for a different platform.")
    HS_DB_MODE_ERROR      -> HyperscanException("The given database was built for a different mode of operation.")
    HS_BAD_ALIGN          -> HyperscanException("A parameter passed to this function was not correctly aligned.")
    HS_BAD_ALLOC          -> HyperscanException("The allocator did not return memory suitably aligned for the largest representable data type on this platform.")
    HS_SCRATCH_IN_USE     -> HyperscanException("The scratch region was already in use.")
    HS_ARCH_ERROR         -> HyperscanException("Unsupported CPU architecture. At least SSE3 is needed")
    HS_INSUFFICIENT_SPACE -> HyperscanException("Provided buffer was too small.")
    HS_UNKNOWN_ERROR      -> HyperscanException("Unknown error: $hsError")
    else                  -> HyperscanException("Unexpected error: $hsError")
}

class HyperscanException(message: String): RuntimeException(message) 
