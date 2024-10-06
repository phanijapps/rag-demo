package com.perficient.automation.rag.demo.service

import com.perficient.automation.rag.demo.util.logInfo
import jakarta.annotation.PostConstruct
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

import org.apache.poi.ooxml.POIXMLProperties
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TextSplitter
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream

@Service
class DataLoaderService(val vectorStore: VectorStore,
                    val embeddingModel: EmbeddingModel) {

    @Value("\${rag_demo_docx_location}")
    lateinit var docLocation : String

    /**
     * Load at the start.
     * Todo fix to only add that arent present in the db
     */
   // @PostConstruct
    fun loadDocs(){
        logInfo { "Loading File...." }
        val folder = File(docLocation)

        if (folder.exists() && folder.isDirectory) {
            // List all files in the folder
            folder.listFiles()?.forEach { file ->
                // For each file, get the file pointer (the file object itself)
                if (file.isFile) {
                    logInfo {  "File name: ${file.name}, Path: ${file.absolutePath}" }
                    when {
                        file.extension.equals("txt", ignoreCase = true) -> loadText(file)
                       // file.extension.equals("pdf", ignoreCase = true) -> loadPDF(file)
                        else -> logInfo { "Unsupported file type: ${file.name}" }
                    }
                }
            }
        } else {
            println("Invalid folder path or the folder doesn't exist.")
        }

    }

    fun loadText(file: File) {
        logInfo { "Loading Text file" }

        val documents = TextReader("file:${file.absolutePath}").get()
        val chunks =  TokenTextSplitter().apply(documents)


        logInfo { "Loaded Text ${chunks.size}" }

        chunks.chunked(20).forEach { batch ->
            logInfo { "Adding First Batch" }
            vectorStore.add(batch)
        }



    }

    fun loadPDF(filePath: File) {
         Loader.loadPDF(filePath).use { doc ->
             val pdfStripper = PDFTextStripper()
            // val document = Document(pdfStripper.getText(doc),getHistoryMetadata(filePath.name,))
             //add to vector store.
            // vectorStore.add(listOf(document))
         }

    }

    private fun getHistoryMetadata(filePath: String, location: String): Map<String, String> {
       return mapOf(

           "file" to filePath,
           "location" to location

       )
    }


    fun loadDoc(fileStream : FileInputStream) {
        XWPFDocument(fileStream).use { document ->
            val extractor = XWPFWordExtractor(document)
            var metadata = getDocxMetadata(document)
           // val embeddings = embeddingModel.embed(extractor.text)
            val document = Document(extractor.text,metadata)
            //add to vector store.
            vectorStore.add(listOf(document))
        }
    }

    fun getDocxMetadata(document: XWPFDocument): Map<String, String>{
        val coreProps: POIXMLProperties.CoreProperties = document.properties.coreProperties
        val metadata = mutableMapOf<String, String>()

        // Add metadata to the map
        metadata["Title"] = coreProps.title ?: "N/A"
        metadata["Author"] = coreProps.creator ?: "N/A"
        metadata["Subject"] = coreProps.subject ?: "N/A"
        metadata["Keywords"] = coreProps.keywords ?: "N/A"
        metadata["Description"] = coreProps.description ?: "N/A"
        metadata["Last Modified By"] = coreProps.lastModifiedByUser ?: "N/A"
        metadata["Revision"] = coreProps.revision ?: "N/A"
        metadata["Created"] = coreProps.created?.toString() ?: "N/A"
        metadata["Modified"] = coreProps.modified?.toString() ?: "N/A"

        return metadata
    }

}