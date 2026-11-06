package visitor;

import element.DirectoryNode;
import element.FileNode;

public interface Visitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}