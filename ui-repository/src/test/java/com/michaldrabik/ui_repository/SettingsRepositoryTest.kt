package com.michaldrabik.ui_repository

import com.google.common.truth.Truth.assertThat
import com.michaldrabik.storage.database.dao.SettingsDao
import com.michaldrabik.ui_model.Settings
import com.michaldrabik.ui_repository.common.BaseMockTest
import com.michaldrabik.ui_repository.mappers.SettingsMapper
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest : BaseMockTest() {

  @MockK lateinit var settingsDao: SettingsDao

  private lateinit var SUT: SettingsRepository

  @Before
  override fun setUp() {
    super.setUp()
    every { database.settingsDao() } returns settingsDao
    SUT = SettingsRepository(database, mappers)
  }

  @Test
  fun `Should be initialized if there are settings in database`() {
    runBlocking {
      coEvery { settingsDao.getCount() } returns 1
      assertThat(SUT.isInitialized()).isTrue()
    }
  }

  @Test
  fun `Should not be initialized if there are no settings in database`() {
    runBlocking {
      coEvery { settingsDao.getCount() } returns 0
      assertThat(SUT.isInitialized()).isFalse()
    }
  }

  @Test
  fun `Should load settings properly`() {
    runBlocking {
      val mapper = SettingsMapper()
      val settings = Settings.createInitial()
      val settingsDb = mapper.toDatabase(settings)

      coEvery { mappers.settings } returns mapper
      coEvery { settingsDao.getAll() } returns settingsDb

      val loaded = SUT.load()

      assertThat(loaded).isEqualTo(settings)
      coVerify { settingsDao.getAll() }
      confirmVerified(settingsDao)
    }
  }

  @Test
  fun `Should update settings properly`() {
    runBlocking {
      val mapper = SettingsMapper()
      val settings = Settings.createInitial()
      val settingsDb = mapper.toDatabase(settings)

      coEvery { mappers.settings } returns mapper
      coEvery { settingsDao.upsert(settingsDb) } just Runs

      SUT.update(settings)

      coVerify { settingsDao.upsert(settingsDb) }
      confirmVerified(settingsDao)
    }
  }
}
