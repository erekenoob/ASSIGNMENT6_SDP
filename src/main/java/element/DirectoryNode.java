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


    public void addChild(Element child) {
        Objects.requireNonNull(child, "Child element cannot be null");
        validateSingleParent(child);

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


}