/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.source.MessageSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.RuntimeErrorException;

/**
 * A caching, on-demand loading message source provider with configurable expiry
 *
 * <p>
 * This class uses a {@link MessageSourceLoader} internally to look up message
 * sources. As is the case for {@link StaticMessageSourceProvider}, you can also
 * set a default source if the loader fails to grab a source.</p>
 *
 * <p>
 * Apart from the loader, you can customize two aspects of the provider:</p>
 *
 * <ul>
 * <li>its load timeout (1 second by default);</li>
 * <li>its expiry time (10 minutes by default).</li>
 * </ul>
 *
 * <p>
 * Note that the expiry time is periodic only, and not per source. The loading
 * result (success or failure) is recorded permanently until the expiry time
 * kicks in.</p>
 *
 * <p>
 * In the event of a timeout, the task remains active until it gets a result;
 * this means, for instance, that if you set up a timeout of 500 milliseconds,
 * but the task takes 2 seconds to complete, during these two seconds, the
 * default source will be returned instead.</p>
 *
 * <p>
 * You can also configure a loader so that it never expires.</p>
 *
 * <p>
 * You cannot instantiate that class directly; use {@link #newBuilder()} to
 * obtain a builder class and set up your provider.</p>
 *
 * @see Builder
 */
public final class LoadingMessageSourceProvider
        implements MessageSourceProvider {

    private static final InternalBundle BUNDLE = InternalBundle.getInstance();

    /*
     * Loader and default source
     */
    private final MessageSourceLoader loader;
    private final MessageSource defaultSource;

    /*
     * List of sources
     */
    private final Map<Locale, FutureTask<MessageSource>> sources
            = new HashMap<Locale, FutureTask<MessageSource>>();

    private LoadingMessageSourceProvider(final Builder builder) {
        loader = builder.loader;
        defaultSource = builder.defaultSource;
    }

    /**
     * Create a new builder
     *
     * @return an empty builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public MessageSource getMessageSource(final Locale locale) {
        try {
            return loader.load(locale);
        } catch (Exception ex) {
            return defaultSource;
        }
    }

    /**
     * Builder class for a {@link LoadingMessageSourceProvider}
     */
    public static final class Builder {
        /*
         * NOTE: apart from requiring them to be positive, we do no checks at
         * all on what the user submits as timeout/expiry values; it could
         * want a 1 ns expiry that we woudln't prevent it.
         */

        private MessageSourceLoader loader;
        private MessageSource defaultSource;
        private long timeoutDuration = 1L;
        private TimeUnit timeoutUnit = TimeUnit.SECONDS;
        private long expiryDuration = 10L;
        private TimeUnit expiryUnit = TimeUnit.MINUTES;

        private Builder() {
        }

        /**
         * Set the message source loader
         *
         * @param loader the loader
         * @throws NullPointerException loader is null
         * @return this
         */
        public Builder setLoader(final MessageSourceLoader loader) {
            BUNDLE.checkNotNull(loader, "cfg.nullLoader");
            this.loader = loader;
            return this;
        }

        /**
         * Set the default message source if the loader fails to load
         *
         * @param defaultSource the default source
         * @throws NullPointerException source is null
         * @return this
         */
        public Builder setDefaultSource(final MessageSource defaultSource) {
            BUNDLE.checkNotNull(defaultSource, "cfg.nullDefaultSource");
            this.defaultSource = defaultSource;
            return this;
        }

        /**
         * Set the load timeout (1 second by default)
         *
         * <p>
         * If the loader passed as an argument fails to load a message source
         * after the specified timeout is elapsed, then the default
         * messagesource will be returned (if any).</p>
         *
         * @param duration number of units
         * @param unit the time unit
         * @throws IllegalArgumentException {@code duration} is negative or zero
         * @throws NullPointerException {@code unit} is null
         * @return this
         *
         * @see #setLoader(MessageSourceLoader)
         * @see #setDefaultSource(MessageSource)
         */
        public Builder setLoadTimeout(final long duration, final TimeUnit unit) {
            BUNDLE.checkArgument(duration > 0L, "cfg.nonPositiveDuration");
            BUNDLE.checkNotNull(unit, "cfg.nullTimeUnit");
            timeoutDuration = duration;
            timeoutUnit = unit;
            return this;
        }

        /**
         * Set the source expiry time (10 minutes by default)
         *
         * <p>
         * Do <b>not</b> use this method if you want no expiry at all; use
         * {@link #neverExpires()} instead.</p>
         *
         * @since 0.5
         *
         * @param duration number of units
         * @param unit the time unit
         * @throws IllegalArgumentException {@code duration} is negative or zero
         * @throws NullPointerException {@code unit} is null
         * @return this
         */
        public Builder setExpiryTime(final long duration, final TimeUnit unit) {
            BUNDLE.checkArgument(duration > 0L, "cfg.nonPositiveDuration");
            BUNDLE.checkNotNull(unit, "cfg.nullTimeUnit");
            expiryDuration = duration;
            expiryUnit = unit;
            return this;
        }

        /**
         * Set this loading provider so that entries never expire
         *
         * @since 0.5
         *
         * @return this
         */
        public Builder neverExpires() {
            expiryDuration = 0L;
            return this;
        }

        /**
         * Build the provider
         *
         * @return a {@link LoadingMessageSourceProvider}
         * @throws IllegalArgumentException no loader has been provided
         */
        public MessageSourceProvider build() {
            BUNDLE.checkArgument(loader != null, "cfg.noLoader");
            return new LoadingMessageSourceProvider(this);
        }
    }
}
