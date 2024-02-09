package com.reactive.streams.data.utils;

import com.mongodb.MongoTimeoutException;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@UtilityClass
public final class SubscriberHelpers {

    public class ObservableSubscriber<T> implements Subscriber<T> {
        @Getter
        private final List<T> received;
        private final List<Throwable> errors;
        private final CountDownLatch latch;

        @Getter
        private volatile Subscription subscription;
        @Getter
        private volatile boolean completed;

        public ObservableSubscriber() {
            this.received = new ArrayList<T>();
            this.errors = new ArrayList<Throwable>();
            this.latch = new CountDownLatch(1);
        }


        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
        }

        @Override
        public void onNext(T t) {
            this.received.add(t);
        }

        @Override
        public void onError(Throwable throwable) {
            errors.add(throwable);
            onComplete();
        }

        @Override
        public void onComplete() {
            this.completed = true;
            latch.countDown();
        }

        public Throwable getError() {
            if (!this.errors.isEmpty()) {
                return errors.get(0);
            }
            return null;
        }

        public List<T> get(final long timeout, final TimeUnit unit) throws Throwable {
            return await(timeout, unit).getReceived();
        }

        public ObservableSubscriber<T> await() throws Throwable {
            return await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

        public ObservableSubscriber<T> await(final long timeout, final TimeUnit unit) throws Throwable {
            subscription.request(Integer.MAX_VALUE);
            if (!latch.await(timeout, unit)) {
                throw new MongoTimeoutException("Publisher onComplete timed out");
            }
            if (!errors.isEmpty()) {
                throw errors.get(0);
            }
            return this;
        }
    }

    public class OperationSubscriber<T> extends ObservableSubscriber<T> {
        public OperationSubscriber(){
            super();
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            super.onSubscribe(subscription);
            subscription.request(Integer.MAX_VALUE);
        }
    }

    public class PrintSubscriber<T> extends OperationSubscriber<T> {

        private final String message;

        public PrintSubscriber(final String message) {
            super();
            this.message = message;
        }

        @Override
        public void onComplete() {
            System.out.println(format(message, getReceived()));
            super.onComplete();
        }
    }

    public class PrintDocumentSubcriber extends OperationSubscriber<Document> {

        public PrintDocumentSubcriber() {
            super();
        }

        @Override
        public void onNext(final Document document) {
            super.onNext(document);
            System.out.println(document.toJson());
        }
    }
}
