package com.app.githubusers

import com.app.githubusers.network.model.User
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {
    @Test
    fun userSizeTest1() {
        val items : MutableList<User> = mutableListOf()
        for(x in 1..100) {
            items.add(User(
                x,
                "name",
                "pic.jpg",
                "movie",
                "action",
                "action",
                "",
                "",
                "",
                0,
                "",
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                "",
                "",
                true,
                "",
                "",
                "",
                "",
                "",
                "",
                ""))
        }
        assertEquals(100, items.size)
    }

    @Test
    fun userSizeTest2() {
        val items : MutableList<User> = mutableListOf()
        for(x in 1..1000) {
            items.add(User(
                x,
                "name",
                "pic.jpg",
                "movie",
                "action",
                "action",
                "",
                "",
                "",
                0,
                "",
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                "",
                "",
                true,
                "",
                "",
                "",
                "",
                "",
                "",
                ""))
        }
        assertEquals(1000, items.size)
    }
}