package io.wrtn.kommons.images

import javax.imageio.spi.IIORegistry
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.spi.ImageWriterSpi

object IIORegistryUtils {

    private val registry by lazy { IIORegistry.getDefaultInstance() }

    val imageReaderFormatNames: List<String> by lazy { getReadImageFormatNames() }
    val imageWriterFormatNames: List<String> by lazy { getWriteImageFormatNames() }

    private inline fun <reified T> getServiceProviders(): Sequence<T> {
        return registry.getServiceProviders(T::class.java, false).asSequence()
    }

    fun getImageReaderSpis(): List<ImageReaderSpi> {
        return getServiceProviders<ImageReaderSpi>().toList()
    }

    fun getImageWriterSpis(): List<ImageWriterSpi> {
        return getServiceProviders<ImageWriterSpi>().toList()
    }

    fun getReadImageFormatNames(): List<String> {
        return getImageReaderSpis().flatMap { it.formatNames.toList() }
    }

    fun getWriteImageFormatNames(): List<String> {
        return getImageWriterSpis().flatMap { it.formatNames.toList() }
    }
}
