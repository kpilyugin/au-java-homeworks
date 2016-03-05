package ru.spbau.mit.pilyugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kirill Pilyugin
 */
public class TrieImpl implements Trie {

    private final Node root = new Node();

    @Override
    public boolean add(String element) {
        return root.add(element, 0);
    }

    @Override
    public boolean contains(String element) {
        Node node = findNodeByPrefix(element);
        return node != null && node.isTerminal;
    }

    @Override
    public boolean remove(String element) {
        return root.remove(element, 0);
    }

    @Override
    public int size() {
        return root.howManyStartsWith();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        Node node = findNodeByPrefix(prefix);
        return node == null ? 0 : node.howManyStartsWith();
    }

    @Override
    public void serialize(OutputStream out) throws IOException {
        root.serialize(new DataOutputStream(out));
    }

    @Override
    public void deserialize(InputStream in) throws IOException {
        root.deserialize(new DataInputStream(in));
    }

    private Node findNodeByPrefix(String prefix) {
        Node current = root;
        for (char letter : prefix.toCharArray()) {
            current = current.getNextNode(letter);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private static class Node {
        private final Map<Character, Node> edges = new HashMap<>();
        private boolean isTerminal = false;
        private int numStringsInSubtree;

        private int howManyStartsWith() {
            return numStringsInSubtree + (isTerminal ? 1 : 0);
        }

        private boolean add(String element, int index) {
            if (index == element.length()) {
                if (isTerminal) {
                    return false;
                }
                isTerminal = true;
                return true;
            }

            char letter = element.charAt(index);
            Node nextNode = getNextNode(letter);

            if (nextNode != null) {
                boolean added = nextNode.add(element, index + 1);
                if (added) {
                    numStringsInSubtree++;
                }
                return added;
            }

            nextNode = new Node();
            edges.put(element.charAt(index), nextNode);
            nextNode.add(element, index + 1);
            numStringsInSubtree++;
            return true;
        }

        private boolean remove(String element, int index) {
            if (index == element.length()) {
                if (isTerminal) {
                    isTerminal = false;
                    return true;
                }
                return false;
            }

            char letter = element.charAt(index);
            Node nextNode = getNextNode(letter);
            if (nextNode == null) {
                return false;
            }

            boolean contained = nextNode.remove(element, index + 1);
            if (!contained) {
                return false;
            }

            numStringsInSubtree--;
            if (numStringsInSubtree == 0) {
                edges.remove(letter);
            }
            return true;
        }

        private Node getNextNode(char letter) {
            return edges.get(letter);
        }

        private void serialize(DataOutputStream out) throws IOException {
            out.writeBoolean(isTerminal);
            out.writeInt(numStringsInSubtree);
            out.writeInt(edges.size());
            for (Map.Entry<Character, Node> entry : edges.entrySet()) {
                out.writeChar(entry.getKey());
                entry.getValue().serialize(out);
            }
        }

        private void deserialize(DataInputStream in) throws IOException {
            isTerminal = in.readBoolean();
            numStringsInSubtree = in.readInt();
            int numEdges = in.readInt();
            edges.clear();
            for (int i = 0; i < numEdges; i++) {
                char letter = in.readChar();
                Node node = new Node();
                edges.put(letter, node);
                node.deserialize(in);
            }
        }
    }
}
