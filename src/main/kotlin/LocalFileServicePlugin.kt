package top.mrxiaom.overflow.localfile

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.daysToMillis
import java.io.File
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

object LocalFileServicePlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "top.mrxiaom.overflow-local-file-service",
        name = "LocalFileService",
        version = BuildConstants.VERSION,
    ) {
        author("MrXiaoM")
    }
) {
    val timer = Timer()
    override fun PluginComponentStorage.onLoad() {
        reloadConfig()
        LocalFileService.register()
        logger.info("已注册本地文件服务")
    }

    override fun onEnable() {
        Commands.register()
        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() { doClean() }
        }, Calendar.getInstance().run {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            time
        }, 1.daysToMillis)
    }

    override fun onDisable() {
        timer.cancel()
    }

    fun reloadConfig() {
        Config.reload()
        LocalFileService.apply {
            tempFolder = Config.tempFolderFile
            useFileIfAvailable = Config.useFileIfAvailable
            useFileBlacklist.clear()
            useFileBlacklist.addAll(Config.useFileBlacklist)
        }
    }

    fun doClean() {
        Config.tempFolderFile
            .listFiles(Config::isFileExpired)
            ?.forEach(File::delete)
        logger.verbose("清理执行完成")
    }

    object Config : ReadOnlyPluginConfig("config") {
        @ValueDescription("临时目录，存放 mirai 端“上传”的文件。默认值(留空)代表 data/top.mrxiaom.overflow-local-file-service，以 . 开头代表与默认值目录的相对路径，不以 / 或盘符开头代表与 mirai 工作目录的相对路径，否则为绝对路径。")
        val tempFolder by value("")
        @ValueDescription("临时文件保留时间(天)，按文件最后修改时间算")
        val cleanDate by value(7)
        @ValueDescription("如果通过 ExternalResource 上传的是本地文件，则直接使用该文件的路径，不保存到临时目录。")
        val useFileIfAvailable by value(true)
        @ValueDescription("本地文件路径黑名单。有的插件不使用输入流来上传临时数据，而是保存临时文件再上传文件，上传完就删除临时文件，这种做法会让 Onebot 协议端无法正常读取到文件。文件路径包含以下内容其中之一时，将不直接使用文件，而是复制到临时目录。")
        val useFileBlacklist by value(listOf("temp"))
        val cleanDateMills: Long
            get() = cleanDate.daysToMillis

        fun isFileExpired(file: File): Boolean {
            return System.currentTimeMillis() - file.lastModified() < cleanDateMills
        }
        val tempFolderFile: File
            get() = if (tempFolder.isEmpty()) {
                dataFolder
            } else if (tempFolder.startsWith(".")) {
                File(dataFolder, tempFolder.substring(1))
            } else {
                File(tempFolder)
            }.also {
                it.mkdirs()
            }
    }
}
