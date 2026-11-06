package visitor;

import element.DirectoryNode;
import element.Element;
import element.FileNode;

public class TotalSizeVisitor implements Visitor {
    private long totalSize = 0;
    private int activeDepth = 0;

    @Override
    public void visit(FileNode file) {
        resetIfRootEntry();
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        resetIfRootEntry();
        activeDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            activeDepth--;
        }
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void reset() {
        totalSize = 0;
        activeDepth = 0;
    }

    private void resetIfRootEntry() {
        if (activeDepth == 0) {
            totalSize = 0;
        }
    }
}