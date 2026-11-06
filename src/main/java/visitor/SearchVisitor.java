package visitor;

import element.DirectoryNode;
import element.Element;
import element.FileNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class SearchVisitor implements Visitor {
    private final String nameCriterion;
    private final long minSizeThreshold;
    private final List<String> matchedPaths = new ArrayList<>();
    private final Deque<String> pathSegments = new ArrayDeque<>();
    private int activeDepth = 0;

    public SearchVisitor(String nameCriterion, long minSizeThreshold) {
        this.nameCriterion = Objects.requireNonNull(nameCriterion, "Name criterion cannot be null");
        if (minSizeThreshold < 0) {
            throw new IllegalArgumentException("Minimum size threshold cannot be negative");
        }
        this.minSizeThreshold = minSizeThreshold;
    }

    @Override
    public void visit(FileNode file) {
        resetIfRootEntry();
        if (matchesCriteria(file)) {
            matchedPaths.add(buildRelativePath(file.getName()));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        resetIfRootEntry();
        boolean isRootDirectory = (activeDepth == 0);
        if (!isRootDirectory) {
            pathSegments.addLast(directory.getName());
        }

        activeDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            activeDepth--;
            if (!isRootDirectory) {
                pathSegments.removeLast();
            }
        }
    }

    public List<String> getMatchingPaths() {
        List<String> sortedResult = new ArrayList<>(matchedPaths);
        Collections.sort(sortedResult);
        return Collections.unmodifiableList(sortedResult);
    }

    public void reset() {
        matchedPaths.clear();
        pathSegments.clear();
        activeDepth = 0;
    }

    private void resetIfRootEntry() {
        if (activeDepth == 0) {
            matchedPaths.clear();
            pathSegments.clear();
        }
    }

    private boolean matchesCriteria(FileNode file) {
        return file.getName().contains(nameCriterion) && file.getSize() >= minSizeThreshold;
    }

    private String buildRelativePath(String fileName) {
        if (pathSegments.isEmpty()) {
            return fileName;
        }
        return String.join("/", pathSegments) + "/" + fileName;
    }
}