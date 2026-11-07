package visitor;

import element.DirectoryNode;
import element.Element;
import element.FileNode;

public class StatisticsVisitor implements Visitor {
    private int fileCount = 0;
    private int directoryCount = 0;
    private int maxDepth = 0;
    private int currentDepth = 0;
    private int activeCallDepth = 0;

    @Override
    public void visit(FileNode file) {
        resetIfRootEntry();
        fileCount++;
        updateMaxDepth(currentDepth);
    }

    @Override
    public void visit(DirectoryNode directory) {
        resetIfRootEntry();
        directoryCount++;
        updateMaxDepth(currentDepth);

        activeCallDepth++;
        currentDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            currentDepth--;
            activeCallDepth--;
        }
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
        currentDepth = 0;
        activeCallDepth = 0;
    }

    private void resetIfRootEntry() {
        if (activeCallDepth == 0) {
            fileCount = 0;
            directoryCount = 0;
            maxDepth = 0;
            currentDepth = 0;
        }
    }

    private void updateMaxDepth(int depth) {
        if (depth > maxDepth) {
            maxDepth = depth;
        }
    }
}