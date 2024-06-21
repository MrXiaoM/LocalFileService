package top.mrxiaom.overflow.localfile

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand

object Commands : CompositeCommand(
    owner = LocalFileServicePlugin,
    primaryName = "localfileservice",
    secondaryNames = arrayOf("lfs")
) {
    @SubCommand
    @Description("重载插件配置")
    suspend fun CommandSender.reload() {
        LocalFileServicePlugin.reloadConfig()
        sendMessage("重载成功")
    }
}
