package com.prostuti.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SkeletonComponentsTest {

    @Test
    fun `design system brand colors and gradients are initialized properly`() {
        assertNotNull(ProstutiCrimson)
        assertNotNull(ProstutiCrimsonDark)
        assertNotNull(ProstutiCrimsonLight)
        assertNotNull(ProstutiGradient)
        assertNotNull(ProstutiHeroDarkGradient)
        
        // Brand color equality check
        org.junit.Assert.assertEquals(androidx.compose.ui.graphics.Color(0xFF9E1B32), ProstutiCrimson)
    }

    @Test
    fun `mascot list has valid icons for splash and empty states`() {
        val owl = MascotPresets.getById("mascot_1")
        assertEquals("🦉", owl.emoji)
        assertEquals("বিজ্ঞ পেঁচা", owl.nameBangla)
    }
}
