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
        assertTrue(SupportProducts.NECTAR in SupportProducts.ALL)
        assertTrue(SupportProducts.HIVE in SupportProducts.ALL)
        assertTrue(SupportProducts.GARDEN in SupportProducts.ALL)
    }

    @Test
    fun matchingIgnoresAnythingThatIsNotAGift() {
        assertEquals(SupportProducts.HIVE, SupportProducts.matching(listOf("other", SupportProducts.HIVE)))
        assertNull(SupportProducts.matching(listOf("bee_plus_annual")))
        assertFalse("bee_plus_monthly" in SupportProducts.ALL)
    }
}
