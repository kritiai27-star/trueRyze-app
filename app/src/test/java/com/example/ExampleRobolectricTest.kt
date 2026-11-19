package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("True Ryze", appName)
  }

  @Test
  fun `firestore create maps exclude id and filter nulls`() {
    val enquiry = com.example.data.model.Enquiry(
      id = "local_id",
      name = "Founder",
      email = "founder@example.com",
      businessName = null,
      serviceType = "Website development",
      plan = "Grow",
      budget = null,
      message = "Need a responsive site."
    )
    val map = enquiry.toCreateMap()
    org.junit.Assert.assertFalse(map.containsKey("id"))
    org.junit.Assert.assertFalse(map.containsKey("businessName"))
    org.junit.Assert.assertFalse(map.containsKey("budget"))
    assertEquals("submitted", map["status"])
    assertEquals("Website development", map["serviceType"])
  }

  @Test
  fun `ten stage lifecycle catalog is ordered and matches portal stages`() {
    val stages = com.example.data.model.TrueRyzeCatalog.lifecycleStages
    assertEquals(10, stages.size)
    assertEquals(stages.map { it.title }, com.example.data.model.TrueRyzeCatalog.portalStages)
    stages.forEachIndexed { idx, spec ->
      assertEquals(idx, spec.index)
      assertEquals((idx + 1).toString().padStart(2, '0'), spec.numberLabel)
    }
    org.junit.Assert.assertTrue(stages.last().isOptional)
  }

  @Test
  fun `client lifecycle stage derives honestly from firestore state`() {
    val catalog = com.example.data.model.TrueRyzeCatalog
    assertEquals(-1, catalog.deriveClientLifecycleStage("visitor", hasEnquiry = false, hasAccessRequest = false))
    assertEquals(0, catalog.deriveClientLifecycleStage("visitor", hasEnquiry = true, hasAccessRequest = false))
    assertEquals(1, catalog.deriveClientLifecycleStage("visitor", hasEnquiry = true, hasAccessRequest = true))
    assertEquals(2, catalog.deriveClientLifecycleStage("active", hasEnquiry = false, hasAccessRequest = false))
  }
}
