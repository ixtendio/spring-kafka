package com.example.kafka.service.producer;

import com.example.kafka.config.KafkaTopic;
import com.example.kafka.model.Movie;
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
public class MovieProducer {

    private String samplePath;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public MovieProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.samplePath = Objects.requireNonNull(System.getProperty("movies.sample.path"), "movies.sample.path is not specified as system property");
        this.kafkaTemplate = kafkaTemplate;
    }

    private Observable<String> scanMovies(Path samplePath) {
        return Observable.create(emitter -> {
            try (Scanner scanner = new Scanner(samplePath, "UTF-8")) {
                scanner.nextLine();//skip the header
                while (scanner.hasNextLine()) {
                    emitter.onNext(scanner.nextLine());
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

    private Observable<String> scanLinks(Path samplePath) {
        return Observable.create(emitter -> {
            try (Scanner scanner = new Scanner(samplePath, "UTF-8")) {
                scanner.nextLine();//skip the header
                while (scanner.hasNextLine()) {
                    emitter.onNext(scanner.nextLine().split(",")[1]);
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
        scanMovies(Paths.get(samplePath, "movies.csv"))
                .zipWith(scanLinks(Paths.get(samplePath, "links.csv")), (l1, l2) -> l1 + "," + l2)
                .map(Movie::fromMovieLine)
                .take(KafkaTopic.MAX_RECORDS)
                .observeOn(Schedulers.io())
                .subscribe(movie -> kafkaTemplate.send(KafkaTopic.MOVIES, movie.getId(), movie), Throwable::printStackTrace);
    }
}
