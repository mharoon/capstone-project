package com.example.haroonyousuf.moviepick.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMDB_Movie_Trailers {

    private int id;
    private List<TMDB_Trailer> results = new ArrayList<>();
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<TMDB_Trailer> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<TMDB_Trailer> results) {
        this.results = results;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
