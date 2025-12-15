package com.yana.ai.services;

import com.yana.ai.model.LoadedDocument;
import com.yana.ai.repository.DocumentRepository;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class DocumentLoaderService implements CommandLineRunner {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ResourcePatternResolver resolver;

    @Autowired
    private VectorStore vectorStore;

    @SneakyThrows
    public void loadDocument() {
        List<Resource> resources = Arrays.stream(
                resolver.getResources("classpath:/knowledgebase/**/*.txt"))
                .toList();

        resources.stream()
                .map(resource -> Pair.of(resource, calcContentHash(resource)))
                .filter(pair ->
                        !documentRepository.existsByFilenameAndContentHash(
                                pair.getFirst().getFilename(),
                                pair.getSecond())
                )
                .forEach(pair -> {
                    Resource resource = pair.getFirst();
                    List<Document> documents = new TextReader(resource).get();
                    TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                            .withChunkSize(500)
                            .build();
                    List<Document> chuncks = textSplitter.apply(documents);
                    vectorStore.accept(chuncks);

                    LoadedDocument loadedDocument = LoadedDocument.builder()
                            .document_type("txt")
                            .chunkCount(chuncks.size())
                            .filename(resource.getFilename())
                            .contentHash(pair.getSecond())
                            .build();

                    documentRepository.save(loadedDocument);
                });

    }

    @SneakyThrows
    private String calcContentHash(Resource resource) {
        return DigestUtils.md5DigestAsHex(resource.getInputStream());
    }

    @Override
    public void run(String... args) throws Exception {
        loadDocument();
    }
}
