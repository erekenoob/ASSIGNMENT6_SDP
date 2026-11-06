package element;

import visitor.Visitor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DirectoryNode implements Element {
    private final String name;
    private final Map<String, Element> children = new LinkedHashMap<>();
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        validateName(name);
        this.name = name.trim();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public void addChild(Element child) {
        Objects.requireNonNull(child, "Child element cannot be null");
        validateSingleParent(child);
        validateNoDuplicateSibling(child.getName());
        validateNoCycle(child);

        attachParent(child);
        children.put(child.getName(), child);
    }

    public Collection<Element> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "Visitor cannot be null");
        visitor.visit(this);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.contains("/")) {
            throw new IllegalArgumentException("Directory name must be non-empty and cannot contain '/'");
        }
    }

    private void validateSingleParent(Element child) {
        if (child.getParent() != null) {
            throw new IllegalStateException("Element '" + child.getName() + "' already belongs to a parent directory");
        }
    }

    private void validateNoDuplicateSibling(String childName) {
        if (children.containsKey(childName)) {
            throw new IllegalArgumentException("Duplicate sibling name '" + childName + "' in directory '" + name + "'");
        }
    }

    private void validateNoCycle(Element child) {
        DirectoryNode current = this;
        while (current != null) {
            if (current == child) {
                throw new IllegalArgumentException("Cycle detected: cannot add ancestor or self as a child");
            }
            current = current.getParent();
        }
    }

    private void attachParent(Element child) {
        if (child instanceof FileNode file) {
            file.setParent(this);
        } else if (child instanceof DirectoryNode dir) {
            dir.setParent(this);
        }
    }
}