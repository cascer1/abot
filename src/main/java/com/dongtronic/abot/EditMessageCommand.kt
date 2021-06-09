package com.dongtronic.abot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException

class EditMessageCommand() : Command() {

    init {
        this.name = "editmessage"
        this.help = "Edit a specific message based on input"
        this.guildOnly = true
        this.arguments = "<message from> <message to>"
        this.userPermissions = arrayOf(Permission.ADMINISTRATOR)
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.replyWarning("You didn't give me any arguments!")
        } else {
            // split the arguments on all whitespaces
            val items = event.args.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (items.size < 4) {
                event.replyWarning("Required arguments: `target channel`, `target message`, `source channel`, `source message`. For example: `abot editmessage #rules 1234 #drafts 4321`")
                return
            }

            copyChannel(event)
        }
    }

    private fun copyChannel(event: CommandEvent) {
        val channels = event.message.mentionedChannels
        val items = event.args.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (channels.size != 2) {
            event.replyError("You didn't give me two channels.")
        }

        try {
            event.message.delete().reason("I'm a cool bot and I can delete messages 8)").complete()
        } catch (e: InsufficientPermissionException) {
            event.replyError("Could not remove command message. Please grant me manage messages permission in ${event.message.channel.name}")
        }

        val newText = channels[1].retrieveMessageById(items[3]).complete()

        channels[0].editMessageById(items[1], newText.contentRaw).complete()

        event.replySuccess("Edited message `${items[1]}` from channel ${channels[0].name} with content from message `${items[3]}` in channel ${channels[1].name}")
    }
}
