package dev.meluhdy.melodia

import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

abstract class MelodiaTest {

    protected lateinit var server: ServerMock
        private set

    protected lateinit var plugin: JavaPlugin
        private set

    abstract fun extraSetUp()

    abstract fun extraTearDown()

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(MelodiaPluginTest::class.java)
        extraSetUp()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
        extraTearDown()
    }

}