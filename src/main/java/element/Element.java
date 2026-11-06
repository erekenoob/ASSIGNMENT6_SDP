package element;

import visitor.Visitor;

public interface Element {
    String getName();
    DirectoryNode getParent();
    void accept(Visitor visitor);
}