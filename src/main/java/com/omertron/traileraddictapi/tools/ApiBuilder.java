/*
 *      Copyright (c) 2004-2016 Stuart Boston
 *
 *      This file is part of TrailerAddict API.
 *
 *      TrailerAddict API is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      TrailerAddict API is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with TrailerAddict API.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.omertron.traileraddictapi.tools;

import com.omertron.traileraddictapi.TrailerAddictException;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamj.api.common.exception.ApiExceptionType;

/**
 * Build the API URL that is used to fetch the data
 *
 * @author stuart.boston
 */
public final class ApiBuilder {
    /*
     * Logger
     */

    private static final Logger LOG = LoggerFactory.getLogger(ApiBuilder.class);
    /*
     * TrailerAddict API Base URL
     */
    private static final String API_BASE = "http://api.traileraddict.com/";
    /*
     * Parameter configuration
     */
    private static final String DELIMITER_FIRST = "?";
    private static final String DELIMITER_SUBSEQUENT = "&";
    private static final String PARAMETER_WIDTH = "width=";
    private static final String PARAMETER_COUNT = "count=";
    private static final String PARAMETER_FEATURED = "featured=";
    private static final String PARAMETER_FILM = "film=";
    private static final String PARAMETER_ACTOR = "actor=";
    private static final String PARAMETER_IMDB = "imdb=";
    private static final String FEATURED_YES = "yes";
    private static final String FEATURED_NO = "no";
    /*
     * Defaults & Maximums
     */
    private static final int DEFAULT_INT = -1;
    private static final String DEFAULT_FEATURED = FEATURED_YES;
    private static final int COUNT_MAX = 8;
    /*
     * Constants
     */
    private static final String FAILED_TO_CREATE_URL = "Failed to create URL: {} - {}";
    private static final String URL = "URL: {}";

    private ApiBuilder() {
        // prevents calls from subclass
        throw new UnsupportedOperationException("Class can not be initialised!");
    }

    /**
     * Build the URL using all available parameters.
     *
     * @param method
     * @param value
     * @param width
     * @param count
     */
    private static URL buildUrl(String method, String value, int count, int width) throws TrailerAddictException {
        StringBuilder searchUrl = new StringBuilder(API_BASE);

        /*
         * There can only be one of "Featured", "Film" and "Actor"
         * Imdb look up is use instead of film
         */
        searchUrl.append(DELIMITER_FIRST).append(method).append(value);

        // Append the width
        if (width != DEFAULT_INT) {
            searchUrl.append(DELIMITER_SUBSEQUENT);
            searchUrl.append(PARAMETER_WIDTH);
            searchUrl.append(width);
        }

        // Append the count
        int urlCount = validateCount(count);
        if (urlCount != DEFAULT_INT) {
            searchUrl.append(DELIMITER_SUBSEQUENT);
            searchUrl.append(PARAMETER_COUNT);
            searchUrl.append(urlCount);
        }

        try {
            LOG.trace(URL, searchUrl.toString());
            return new URL(searchUrl.toString());
        } catch (MalformedURLException ex) {
            LOG.warn(FAILED_TO_CREATE_URL, searchUrl.toString(), ex.toString());
            throw new TrailerAddictException(ApiExceptionType.INVALID_URL, ex.getMessage(), searchUrl.toString(), ex);
        }
    }

    /**
     * Validate the count passed.
     *
     * Should be between -1 & COUNT_MAX
     *
     * @param count
     */
    private static int validateCount(int count) {
        int value = count;
        if (count > COUNT_MAX) {
            value = COUNT_MAX;
        } else if (count < DEFAULT_INT) {
            value = DEFAULT_INT;
        }
        return value;
    }

    /**
     * Get the featured URL
     *
     * @param featured
     * @param count
     * @param width
     * @return
     * @throws TrailerAddictException
     */
    public static URL getFeaturedUrl(String featured, int count, int width) throws TrailerAddictException {
        if (featured.equalsIgnoreCase(FEATURED_YES) || featured.equalsIgnoreCase(FEATURED_NO)) {
            return buildUrl(PARAMETER_FEATURED, featured, count, width);
        } else {
            // Use the default
            return buildUrl(PARAMETER_FEATURED, DEFAULT_FEATURED, count, width);
        }
    }

    /**
     * Get the featured URL with the default width
     *
     * @param featured
     * @param count
     * @return
     * @throws TrailerAddictException
     */
    public static URL getFeaturedUrl(String featured, int count) throws TrailerAddictException {
        return getFeaturedUrl(featured, count, DEFAULT_INT);
    }

    /**
     * Get the featured URL with the default width & count
     *
     * @param featured
     * @return
     * @throws TrailerAddictException
     */
    public static URL getFeaturedUrl(String featured) throws TrailerAddictException {
        return getFeaturedUrl(featured, DEFAULT_INT, DEFAULT_INT);
    }

    /**
     * Get the film URL
     *
     * @param filmId
     * @param count
     * @param width
     * @return
     * @throws TrailerAddictException
     */
    public static URL getFilmUrl(String filmId, int count, int width) throws TrailerAddictException {
        return buildUrl(PARAMETER_FILM, filmId, count, width);
    }

    /**
     * Get the film URL with the default width
     *
     * @param filmId
     * @param count
     * @return
     * @throws TrailerAddictException
     */
    public static URL getFilmUrl(String filmId, int count) throws TrailerAddictException {
        return getFilmUrl(filmId, count, DEFAULT_INT);
    }

    /**
     * Get the film URL with the default width & count
     *
     * @param filmId
     * @return
     * @throws TrailerAddictException
     */
    public static URL getFilmUrl(String filmId) throws TrailerAddictException {
        return getFilmUrl(filmId, DEFAULT_INT, DEFAULT_INT);
    }

    /**
     * Get the film URL using the IMDB ID with the default width
     *
     * @param imdbId
     * @param count
     * @param width
     * @return
     * @throws TrailerAddictException
     */
    public static URL getImdbUrl(String imdbId, int count, int width) throws TrailerAddictException {
        String validatedImdb;
        if (imdbId.startsWith("tt")) {
            validatedImdb = imdbId.replace("tt", "");
        } else {
            validatedImdb = imdbId;
        }

        return buildUrl(PARAMETER_IMDB, validatedImdb, count, width);
    }

    /**
     * Get the film URL using the IMDB ID
     *
     * @param imdbId
     * @param count
     * @return
     * @throws TrailerAddictException
     */
    public static URL getImdbUrl(String imdbId, int count) throws TrailerAddictException {
        return getImdbUrl(imdbId, count, DEFAULT_INT);
    }

    /**
     * Get the film URL using the IMDB ID with the default width & count
     *
     * @param imdbId
     * @return
     * @throws TrailerAddictException
     */
    public static URL getImdbUrl(String imdbId) throws TrailerAddictException {
        return getImdbUrl(imdbId, DEFAULT_INT, DEFAULT_INT);
    }

    /**
     * Get the Actor URL
     *
     * @param actorId
     * @param count
     * @param width
     * @return
     * @throws TrailerAddictException
     */
    public static URL getActorUrl(String actorId, int count, int width) throws TrailerAddictException {
        return buildUrl(PARAMETER_ACTOR, actorId, count, width);
    }

    /**
     * Get the Actor URL with the default width
     *
     * @param actorId
     * @param count
     * @return
     * @throws TrailerAddictException
     */
    public static URL getActorUrl(String actorId, int count) throws TrailerAddictException {
        return getActorUrl(actorId, count, DEFAULT_INT);
    }

    /**
     * Get the Actor URL with the default width & count
     *
     * @param actorId
     * @return
     * @throws TrailerAddictException
     */
    public static URL getActorUrl(String actorId) throws TrailerAddictException {
        return getActorUrl(actorId, DEFAULT_INT, DEFAULT_INT);
    }

    /**
     * Convert the passed URL from the standard trailer URL to the simple API URL
     *
     * @param searchUrl
     * @return
     * @throws TrailerAddictException
     */
    public static URL getSimpleUrl(String searchUrl) throws TrailerAddictException {
        try {
            LOG.trace(URL, searchUrl);
            return new URL(searchUrl.replace("http://www.", "http://simpleapi."));
        } catch (MalformedURLException ex) {
            LOG.warn(FAILED_TO_CREATE_URL, searchUrl, ex.toString());
            throw new TrailerAddictException(ApiExceptionType.INVALID_URL, ex.getMessage(), searchUrl, ex);
        }
    }
}
