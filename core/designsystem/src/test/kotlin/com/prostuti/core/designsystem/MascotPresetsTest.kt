package com.prostuti.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MascotPresetsTest {

    @Test
    fun `MascotPresets contains exactly 6 distinct presets`() {
        assertEquals(6, MascotPresets.list.size)
        val distinctIds = MascotPresets.list.map { it.id }.toSet()
        assertEquals(6, distinctIds.size)
    }

    @Test
    fun `getById returns correct preset or defaults to first`() {
        val owl = MascotPresets.getById("mascot_1")
        assertEquals("বিজ্ঞ পেঁচা", owl.nameBangla)

        val tiger = MascotPresets.getById("mascot_4")
        assertEquals("লক্ষ্যভেদী বাঘ", tiger.nameBangla)

        val unknown = MascotPresets.getById("invalid_id")
        assertEquals("mascot_1", unknown.id)

        val nullId = MascotPresets.getById(null)
        assertEquals("mascot_1", nullId.id)
    }
}
