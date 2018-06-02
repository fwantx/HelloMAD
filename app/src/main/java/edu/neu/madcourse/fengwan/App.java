package edu.neu.madcourse.fengwan;

import android.app.Application;

import java.util.Set;

public class App extends Application {
    private Set<String> dictSet;

    public Set<String> getDictSet() {
        return dictSet;
    }

    public void setDictSet(Set<String> dictSet) {
        this.dictSet = dictSet;
    }
}
