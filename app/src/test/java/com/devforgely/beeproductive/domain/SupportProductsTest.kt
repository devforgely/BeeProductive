package com.devforgely.beeproductive.domain

import com.devforgely.beeproductive.domain.model.SupportProducts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupportProductsTest {
    @Test
    fun catalogOffersThreeOptionalGifts() {
        assertEquals(3, SupportProducts.catalog.size)
        assertTrue(SupportProducts.HONEY in SupportProducts.ALL)
        assertTrue(SupportProducts.JAR in SupportProducts.ALL)
        assertTrue(SupportProducts.COMB in SupportProducts.ALL)
    }

    @Test
    fun matchingIgnoresAnythingThatIsNotAGift() {
        assertEquals(SupportProducts.JAR, SupportProducts.matching(listOf("other", SupportProducts.JAR)))
        assertNull(SupportProducts.matching(listOf("bee_plus_annual")))
        assertFalse("bee_plus_monthly" in SupportProducts.ALL)
    }
}
