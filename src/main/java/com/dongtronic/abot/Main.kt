package com.dongtronic.abot

import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.jagrosh.jdautilities.examples.command.AboutCommand
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.util.*
import javax.security.auth.login.LoginException

object Main {

    @Throws(LoginException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val token = System.getenv("DISCORD_TOKEN")

        // define an eventwaiter, dont forget to add this to the JDABuilder!
        val waiter = EventWaiter()

        // define a command client
        val client = CommandClientBuilder()

        // The default is "Type !!help" (or whatver prefix you set)
        client.useDefaultGame()
        client.useHelpBuilder(true)

        // sets emojis used throughout the bot on successes, warnings, and failures
        client.setEmojis("\uD83D\uDC4C", "\uD83D\uDE2E", "\uD83D\uDE22")

        client.setPrefix("abot")

        client.setOwnerId("189436077793083392")

        // adds commands
        client.addCommands(
                // command to show information about the bot
                AboutCommand(java.awt.Color(137, 207, 240), "A Discord Bot",
                        arrayOf("Copy messages between channels"),
                        Permission.MANAGE_ROLES),


                CopyChannelCommand(),
                EditMessageCommand())

        val builtClient = client.build()

        DefaultShardManagerBuilder.createDefault(token)
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .disableCache(EnumSet.allOf(CacheFlag::class.java)) // We don't need any cached data
                .setShardsTotal(1) // Let Discord decide how many shards we need
                .addEventListeners(
                        waiter,
                        builtClient
                ).build()
    }

}
