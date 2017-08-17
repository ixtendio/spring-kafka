package com.example.kafka.service.producer;

import com.example.kafka.config.KafkaTopic;
import com.example.kafka.model.UserOpinion;
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
public class UserOpinionProducer {

    private String samplePath;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public UserOpinionProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.samplePath = Objects.requireNonNull(System.getProperty("movies.sample.path"), "movies.sample.path is not specified as system property");
        this.kafkaTemplate = kafkaTemplate;
    }

    private Observable<UserOpinion> scanTags(Path samplePath) {
        return Observable.create(emitter -> {
            try (Scanner scanner = new Scanner(samplePath, "UTF-8")) {
                scanner.nextLine();//skip the header
                while (scanner.hasNextLine()) {
                    emitter.onNext(UserOpinion.fromTagsLine(scanner.nextLine()));
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

    private Observable<UserOpinion> scanRatings(Path samplePath) {
        return Observable.create(emitter -> {
            try (Scanner scanner = new Scanner(samplePath, "UTF-8")) {
                scanner.nextLine();//skip the header
                while (scanner.hasNextLine()) {
                    emitter.onNext(UserOpinion.fromRatingsLine(scanner.nextLine()));
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
        scanRatings(Paths.get(samplePath, "ratings.csv"))
                .mergeWith(scanTags(Paths.get(samplePath, "tags.csv")))
                .observeOn(Schedulers.io())
                .subscribe(u -> kafkaTemplate.send(KafkaTopic.USERS_OPINIONS, u.getUserId(), u), Throwable::printStackTrace);
    }
}
