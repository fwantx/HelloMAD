package edu.neu.madcourse.fengwan;

import android.app.Application;

import java.util.Set;

public class App extends Application {
    private Set<String> dictionary;

    public Set<String> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }
}
