package info.novatec.cbdg.plugin

import FreeMarkerService
import models.BpmnObject
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

@Mojo(name = "generate")
class GenerateMojo : AbstractMojo() {

    /**
     * Default usage is the templates/default.ftl from Jar-File.
     * To use it, it will be create in Build-Dir of the target project the empty file default.ftl
     * and fill it with the stream of templates/default.ftl from Jar-File
     */
    @Parameter(property = "templateFile", defaultValue = "\${project.build.directory}/classes/templates/default.ftl")
    lateinit var templateFile: File

    @Parameter(property = "camundaBpmnDir", defaultValue = "\${project.basedir}/src/main/resources/bpmn")
    lateinit var camundaBpmnDir: File

    @Parameter(property = "resultOutputDir", defaultValue = "\${project.build.directory}/cbdg/html")
    lateinit var resultOutputDir: File

    override fun execute() {
        if (templateFile.name.equals("default.ftl")) {
            FileOutputStream(templateFile, false).use { it -> javaClass.classLoader.getResourceAsStream("templates/default.ftl").transferTo(it) }
        }

        camundaBpmnDir.listFiles()?.forEach {
            log.info("Generating documentation for file ${it.absolutePath}")
            log.info("Using template ${templateFile.absolutePath}")
            val bpmnObject = BpmnObject(id = "test-id", it.name, "0.9.0", "Eine ellenlange Daokumentation")
            FreeMarkerService.writeTemplate(
                bpmnObject,
                templateFile.name,
                "${resultOutputDir.absolutePath}/${it.nameWithoutExtension}.html"
            ) {
                setDirectoryForTemplateLoading(templateFile.parentFile)
            }
            log.info("Output report into path ${resultOutputDir.absolutePath}")
        } ?: throw FileNotFoundException("${camundaBpmnDir.absolutePath} don't exist.")
        resultOutputDir.listFiles()?.forEach {
            log.info(it.absolutePath)
        } ?: throw FileNotFoundException("${resultOutputDir.absolutePath} don't exist.")
    }
}
