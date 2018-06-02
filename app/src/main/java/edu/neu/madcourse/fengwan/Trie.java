package edu.neu.madcourse.fengwan;

public class Trie {
    private Node root;

    public Trie() {
        root = new Node();
    }

    // insert a word to trie
    public void insert(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (cur.children[index] == null) cur.children[index] = new Node();
            cur = cur.children[index];
        }
        cur.isEnd = true;
    }

    // check if the trie contains the word
    public boolean contains(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (cur.children[index] == null) return false;
            else cur = cur.children[index];
        }
        return cur.isEnd;
    }

    // check if the trie has this prefix
    public boolean hasPrefix(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (cur.children[index] == null) return false;
            cur = cur.children[index];
        }
        return true;
    }
}

class Node {
    boolean isEnd;
    Node[] children;

    public Node() {
        isEnd = false;
        children = new Node[26];
    }
}