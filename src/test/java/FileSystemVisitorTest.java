import element.DirectoryNode;
import element.FileNode;
import visitor.SearchVisitor;
import visitor.StatisticsVisitor;
import visitor.TotalSizeVisitor;
import visitor.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemVisitorTest {

    private DirectoryNode root;
    private DirectoryNode srcDir;
    private DirectoryNode testDir;
    private DirectoryNode emptyDir;

    @BeforeEach
    void setUp() {
        root = new DirectoryNode("project");
        srcDir = new DirectoryNode("src");
        testDir = new DirectoryNode("test");
        emptyDir = new DirectoryNode("empty");

        DirectoryNode mainDir = new DirectoryNode("main");
        mainDir.addChild(new FileNode("App.java", 1500));
        mainDir.addChild(new FileNode("AppConfig.java", 800));

        srcDir.addChild(mainDir);
        srcDir.addChild(new FileNode("module-info.java", 200));

        testDir.addChild(new FileNode("AppTest.java", 2000));

        root.addChild(srcDir);
        root.addChild(testDir);
        root.addChild(emptyDir);
        root.addChild(new FileNode("pom.xml", 500));
    }


    @Test
    void testInvalidTreeMutationsAreRejected() {
        // Negative file size
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -10));

        // Duplicate sibling name
        assertThrows(IllegalArgumentException.class, () -> root.addChild(new FileNode("pom.xml", 100)));

        // Multiple parent ownership
        FileNode sharedFile = new FileNode("shared.txt", 50);
        srcDir.addChild(sharedFile);
        assertThrows(IllegalStateException.class, () -> testDir.addChild(sharedFile));

        // Cycle prevention (adding root into its descendant)
        assertThrows(IllegalArgumentException.class, () -> srcDir.addChild(root));

        // Encapsulation check children collection is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> root.getChildren().clear());
    }
}