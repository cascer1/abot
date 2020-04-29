package com.dongtronic.abot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException

class CopyChannelCommand() : Command() {

    init {
        this.name = "copychannel"
        this.help = "Copy all the messages in a channel to another channel"
        this.guildOnly = true
        this.arguments = "<channel from> <channel to>"
        this.userPermissions = arrayOf(Permission.ADMINISTRATOR)
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.replyWarning("You didn't give me any arguments!")
        } else {
            // split the arguments on all whitespaces
            val items = event.args.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (items.size < 2) {
                event.replyWarning("Required arguments: `from channel` and `to channel`. For example: `abot copychannel #foo #bar`")
                return
            }

            copyChannel(event)
        }
    }

    private fun copyChannel(event: CommandEvent) {
        val channels = event.message.mentionedChannels

        if (channels.size != 2) {
            event.replyError("You didn't give me two channels. Usage: `abot copy #foo #bar`")
        }
        val oldMessages = channels[1].history.retrievePast(100).complete()

        try {
            oldMessages.forEach { message ->
                message.delete().reason("Copying new information to this channel").complete()
            }
        } catch (e: InsufficientPermissionException) {
            event.replyError("Could not delete existing messages from ${channels[1].name}. Please grant me manage messages permission there.")
        }

        try {
            event.message.delete().reason("I'm a cool bot and I can delete messages 8)").complete()
        } catch (e: InsufficientPermissionException) {
            event.replyError("Could not remove command message. Please grant me manage messages permission in ${event.message.channel.name}")
        }

        val messages = channels[0].history.retrievePast(100).complete().reversed()
        var count = 0

        messages.forEach { message ->
            if (!message.author.isBot) {
                channels[1].sendMessage(message.contentRaw).queue()
                count++
            }
        }

        event.replySuccess("Posted $count messages to ${channels[1].name}")
    }
}
