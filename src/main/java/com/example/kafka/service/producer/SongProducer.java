package com.example.kafka.service.producer;

import com.example.kafka.config.KafkaTopic;
import com.example.kafka.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import rx.Emitter;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

@Service
public class SongProducer {

    private String samplePath;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public SongProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.samplePath = Objects.requireNonNull(System.getProperty("songs.sample.path"), "songs.sample.path is not specified as system property");
        this.kafkaTemplate = kafkaTemplate;
    }

    private Observable<Song> scan(Path samplePath) {
        return Observable.create(emitter -> {
            try (Scanner scanner = new Scanner(samplePath, "UTF-8")) {
                while (scanner.hasNextLine()) {
                    emitter.onNext(Song.fromLine(scanner.nextLine()));
                }
                if (scanner.ioException() != null) {
                    throw scanner.ioException();
                } else {
                    emitter.onCompleted();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    @PostConstruct
    public void postConstruct() {
        scan(Paths.get(samplePath))
                .take(KafkaTopic.MAX_RECORDS)
                .observeOn(Schedulers.io())
                .subscribe(song -> kafkaTemplate.send(KafkaTopic.SONGS, song.getId(), song), Throwable::printStackTrace);
    }

}
