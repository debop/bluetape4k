package io.bluetape4k.hyperscan.wrapper

import com.gliwka.hyperscan.jni.hs_scratch_t
import com.gliwka.hyperscan.jni.hyperscan
import org.bytedeco.javacpp.Pointer

class NativeScratch: hs_scratch_t() {
    fun registerDeallocator() {
        val p = hs_scratch_t(this)
        deallocator<Pointer> { hyperscan.hs_free_scratch(p) }
    }
}
