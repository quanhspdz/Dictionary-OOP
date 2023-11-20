package Controllers;

import java.util.*;

public class AhoCorasick {
    TrieNode root;

    public AhoCorasick(List<String> dictionary) {
        root = new TrieNode();
        buildTrie(dictionary);
        buildFailureFunction();
    }

    private void buildTrie(List<String> dictionary) {
        for (String word : dictionary) {
            root.addKeyword(word);
        }
    }

    private void buildFailureFunction() {
        Queue<TrieNode> queue = new LinkedList<>();
        for (TrieNode child : root.children.values()) {
            queue.add(child);
            child.failure = root;
        }

        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            for (Map.Entry<Character, TrieNode> entry : current.children.entrySet()) {
                char ch = entry.getKey();
                TrieNode child = entry.getValue();
                queue.add(child);

                TrieNode failureNode = current.failure;
                while (failureNode != null && !failureNode.children.containsKey(ch)) {
                    failureNode = failureNode.failure;
                }

                child.failure = failureNode != null ? failureNode.children.get(ch) : root;
                child.keywords.addAll(child.failure.keywords);
            }
        }
    }

    public List<String> search(String searchText) {
        List<String> result = new ArrayList<>();
        TrieNode node = root;

        for (char ch : searchText.toCharArray()) {
            if (node.children.containsKey(ch)) {
                //
                System.out.println("xem co tu vao queue");
                node = node.children.get(ch);
            } else {
                return result;  // Không có kết quả nếu không tìm thấy
            }
        }

        // Duyệt qua tất cả các từ khóa của nút hiện tại và các nút con
        Queue<TrieNode> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            result.addAll(current.keywords);

            for (TrieNode child : current.children.values()) {
                queue.add(child);
            }
        }

        return result;
    }
}
