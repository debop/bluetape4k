package io.bluetape4k.hyperscan.wrapper

import com.gliwka.hyperscan.jni.hs_database_t
import com.gliwka.hyperscan.jni.hyperscan
import org.bytedeco.javacpp.Pointer

class NativeDatabase: hs_database_t() {

    fun registerDeallocator() {
        val p = hs_database_t(this)
        deallocator<Pointer> { hyperscan.hs_free_database(p) }
    }
}
