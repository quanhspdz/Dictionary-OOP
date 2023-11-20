package Controllers;

import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    List<String> keywords = new ArrayList<>();
    TrieNode failure;

    void addKeyword(String keyword) {
        // test
        System.out.println("test");
        //
        TrieNode node = this;
        for (char ch : keyword.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.keywords.add(keyword);
    }
}

